package com.example.edupal.service.Impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.edupal.common.Result;
import com.example.edupal.dto.request.QuestionRequest;
import com.example.edupal.dto.response.AnswerResponse;
import com.example.edupal.dto.response.HistoryResponse;
import com.example.edupal.model.User;
import com.example.edupal.repository.UserRepository;
import com.example.edupal.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.edupal.model.Question;
import com.example.edupal.model.Answer;
import com.example.edupal.repository.QuestionRepository;
import com.example.edupal.repository.AnswerRepository;

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

        // 删除问题
        questionRepository.delete(question);
        // 删除相关的答案
        List<Answer> answers = answerRepository.findAnswersByQuestionId(questionId);
        if (answers != null && !answers.isEmpty()) {
            answerRepository.deleteAll(answers);
        }
        return new Result(true, "问题删除成功");
    }
}

