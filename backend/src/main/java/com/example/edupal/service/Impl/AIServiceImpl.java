package com.example.edupal.service.Impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.QuestionRequest;
import com.example.edupal.dto.response.AnswerResponse;
import com.example.edupal.dto.response.HistoryResponse;
import com.example.edupal.dto.response.ViewQuestionResponse;
import com.example.edupal.model.*;
import com.example.edupal.repository.*;
import com.example.edupal.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AIServiceImpl implements AIService {

    private static final String API_URL = "https://spark-api-open.xf-yun.com/v2/chat/completions";
    private static final String API_PASSWORD = "GDmXDuVwnpEiserckONd:xSCyunWDskiLSTIDVRIb"; // Replace with your actual API password

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherAnswerRepository teacherAnswerRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    private String saveAnswer(AnswerResponse answerResponse, String questionId) {

        Answer answer = new Answer();
        Question question = questionRepository.findByQuestionId(questionId);
        answer.setAnswerContent(answerResponse.getAnswerContent());
        answer.setQuestionId(questionId);
        answer.setRelatedQuestion(question.getQuestionContent());
        answer.setAnswerType(0);
        answer.setAnswerTime(new Date());
        answerRepository.save(answer);

        question.setIsAnswered(1); // Mark the question as answered
        questionRepository.save(question); // Update the question to mark it as answered

        return answer.getAnswerId(); // Return the saved answer ID
    }

    private String saveQuestion(QuestionRequest questionRequest) {
        Question question = new Question();
        question.setStudentId(questionRequest.getStudentId());
        question.setQuestionContent(questionRequest.getQuestionContent());
        question.setQuestionType(questionRequest.getQuestionType());
        question.setQuestionSubject(questionRequest.getQuestionSubject());
        question.setIsAnswered(0);
        question.setQuestionTime(new Date());

        questionRepository.save(question);
        return question.getQuestionId(); // Return the saved question ID
    }


    @Override
    public AnswerResponse askQuestion(QuestionRequest questionRequest) {
        AnswerResponse answerResponse = new AnswerResponse();
        StringBuilder contentBuilder = new StringBuilder();

        // Save the question to the database
        String questionId=saveQuestion(questionRequest);
        if (questionRequest.getQuestionType() == 0) {
            try {
                // Create the JSON request payload
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("user", questionRequest.getStudentId());
                jsonObject.set("model", "x1");

                JSONArray messagesArray = new JSONArray();
                JSONObject messageObject = new JSONObject();
                messageObject.set("role", "user");
                messageObject.set("content", questionRequest.getQuestionContent());
                messageObject.set("temperature", "0.5");
                messagesArray.put(messageObject);

                jsonObject.set("messages", messagesArray);
                jsonObject.set("stream", true);
                jsonObject.set("max_tokens", 16384);

                // Set up the HTTP connection
                URL url = new URL(API_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Authorization", "Bearer " + API_PASSWORD);
                con.setDoOutput(true);

                // Send the request
                OutputStream os = con.getOutputStream();
                os.write(jsonObject.toString().getBytes());
                os.flush();
                os.close();

                // Read the response
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        // Parse each line as JSON and extract "content"
                        if (inputLine.startsWith("data:")) {
                            String jsonData = inputLine.substring(5).trim(); // Remove "data:" prefix
                            if (!jsonData.equals("[DONE]")) {
                                JSONObject responseData = new JSONObject(jsonData);
                                if (responseData.containsKey("choices")) {
                                    JSONArray choicesArray = responseData.getJSONArray("choices");
                                    for (int i = 0; i < choicesArray.size(); i++) {
                                        JSONObject choice = choicesArray.getJSONObject(i);
                                        if (choice.containsKey("delta")) {
                                            JSONObject delta = choice.getJSONObject("delta");
                                            if (delta.containsKey("content")) {
                                                contentBuilder.append(delta.getStr("content"));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    in.close();
                    answerResponse.setQuestionId(questionId);
                    // Set the final content in AnswerResponse
                    answerResponse.setAnswerContent(contentBuilder.toString());
                    String answerId=saveAnswer(answerResponse, questionId);
                    answerResponse.setAnswerId(answerId);

                } else {
                    answerResponse.setAnswerContent("Error: Received response code " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                answerResponse.setAnswerContent("Error: " + e.getMessage());
            }
        }
            return answerResponse;
        }

    @Override
    public HistoryResponse getHistory(String userId) {
        // Step 1: Get studentID from userId
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return new HistoryResponse("error", "用户不存在", null, null);
        }
        String studentId = user.getUserId();

        // Step 2: Get all questions for the studentID
        List<Question> questions = questionRepository.findQuestionByStudentId(studentId);

        // Step 3: For each question, get corresponding answers
        List<HistoryResponse.QA> questionSet = questions.stream().map(question -> {
            List<Answer> answers = answerRepository.findAnswersByQuestionId(question.getQuestionId());
            return new HistoryResponse.QA(question, answers);
        }).collect(Collectors.toList());

        // Step 4: Build and return the response
        return new HistoryResponse("success", "问答历史获取成功", studentId, questionSet);
    }

    @Override
    public Result deleteHistory(String userId, String questionId){
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return new Result(false, "用户不存在");
        }

        Question question = questionRepository.findByQuestionId(questionId);
        if (question == null) {
            return new Result(false, "问题不存在");
        }
        
        // 验证问题所有者
        if (!question.getStudentId().equals(userId)) {
            return new Result(false, "无权删除此问题");
        }

        try {
            // 先删除相关的答案（外键约束要求先删除子表）
            List<Answer> answers = answerRepository.findAnswersByQuestionId(questionId);
            if (answers != null && !answers.isEmpty()) {
                answerRepository.deleteAll(answers);
            }
            
            // 再删除问题
            questionRepository.delete(question);
            
            return new Result(true, "问题删除成功");
        } catch (Exception e) {
            return new Result(false, "删除失败: " + e.getMessage());
        }
    }

    @Override
    public Result transTeacher(String userId,String questionId,String teacherId){

        Student student = studentRepository.findByStudentId(userId);
        if (student == null) {
            return new Result(false, "学生不存在");
        }
        Teacher teacher = teacherRepository.findByTeacherId(teacherId);
        if (teacher == null) {
            return new Result(false, "教师不存在");
        }
        //检查teacher的class和student的class是否相同
        if (!teacher.getClass1().equals(student.getStudentClass())&& !teacher.getClass2().equals(student.getStudentClass())) {
            return new Result(false, "教师和学生不在同一班级，无法转交问题");
        }
        // 检查问题是否存在
        Question question = questionRepository.findByQuestionId(questionId);
        if (question == null) {
            return new Result(false, "问题不存在");
        }
        // 检查问题是否已经被转交
        if (question.getQuestionType() == 2) {
            return new Result(false, "问题已经被转交或回答，无需再次转交");
        }

        // 创建教师回答记录
        TeacherAnswer teacherAnswer = new TeacherAnswer();
        teacherAnswer.setTeacherId(teacherId);
        teacherAnswer.setStudentId(userId);
        teacherAnswer.setQuestionId(questionId);
        teacherAnswer.setAnswerId(null); // 初始时没有答案ID
        teacherAnswer.setTransTime(new Date());
        teacherAnswerRepository.save(teacherAnswer);

        // 更新问题类型为转交给教师
        question.setQuestionType(2); // 3表示问题已转交给教师
        questionRepository.save(question);
        return new Result(true, "问题已成功转交给教师");
    }

    @Override
    public ViewQuestionResponse viewQuestion(String teacherId) {
        Teacher teacher = teacherRepository.findByTeacherId(teacherId);
        if (teacher == null) {
            return new ViewQuestionResponse("error", "该教师不存在", teacherId, 0, List.of());
        }

        // 查询所有转交给该教师的问题
        List<TeacherAnswer> teacherAnswers = teacherAnswerRepository.findByTeacherId(teacherId);

        // 构建问题集合
        List<ViewQuestionResponse.QA> questionSet = teacherAnswers.stream().map(ta -> {
            Question question = questionRepository.findByQuestionId(ta.getQuestionId());
            Student student = studentRepository.findByStudentId(ta.getStudentId());

            // 获取教师回答集合
            List<Answer> AnswerList = answerRepository.findAnswersByQuestionId(ta.getQuestionId());

            // Map teacher answers to AnswerDetail objects
            List<ViewQuestionResponse.QA.AnswerDetail> teacherAnswersDetails = AnswerList.stream()
                    .map(answer -> new ViewQuestionResponse.QA.AnswerDetail(
                            answer.getAnswerType(),
                            answer.getAnswerContent(),
                            answer.getAnswerTime()
                    ))
                    .collect(Collectors.toList());

            return new ViewQuestionResponse.QA(
                    student.getStudentName(),
                    student.getStudentId(),
                    student.getStudentClass(),
                    question.getQuestionContent(),
                    ta.getTransTime(), // 问题转交时间
                    teacherAnswersDetails // 教师回答集合
            );
        }).collect(Collectors.toList());

        return new ViewQuestionResponse("success", "学生提问记录返回成功", teacherId, questionSet.size(), questionSet);
    }
}

