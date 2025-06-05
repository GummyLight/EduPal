package com.example.edupal.service.Impl;

import com.example.edupal.common.Result;
import com.example.edupal.dto.request.CreateQuizRequest;
import com.example.edupal.dto.request.ModifyQuizRequest;
import com.example.edupal.dto.response.GetMyQuizResponse;
import com.example.edupal.dto.response.GetQuizStudentResponse;
import com.example.edupal.dto.response.GetTeacherQuizResponse;
import com.example.edupal.model.Quiz;
import com.example.edupal.model.QuizAnswer;
import com.example.edupal.model.Student;
import com.example.edupal.model.User;
import com.example.edupal.repository.*;
import com.example.edupal.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.edupal.dto.response.GetStudentQuizResponse;

import java.util.Date;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private TeacherRepository teacherRepository;


    @Override
    public GetStudentQuizResponse getStudentQuiz(String userId) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        if(user == null) {
            return new GetStudentQuizResponse("error","User not found", userId, 0, null);
        }

        Student student = studentRepository.findByStudentId(userId);

        if(student == null) {
            return new GetStudentQuizResponse("error","Student not found", userId, 0, null);
        }

        // 查询用户的测验列表quiz
        List<Quiz> quizzes = quizRepository.findByClassId(student.getStudentClass());

        if (quizzes == null || quizzes.isEmpty()) {
            return new GetStudentQuizResponse("success","No Your Quiz", userId, 0, null);
        }

        QuizAnswer quizAnswer = null;

        List<GetStudentQuizResponse.quizDetail> Quizzes = quizzes.stream()
                .map(quiz -> {
                    QuizAnswer quizAnswerForQuiz = quizAnswerRepository.findByStudentIdAndQuizId(userId, quiz.getQuiz_id());
                    return new GetStudentQuizResponse.quizDetail(
                            quiz.getQuiz_id(),
                            quiz.getTitle(),
                            quiz.getSubject(),
                            quiz.getContentType(),
                            quiz.getDifficulty(),
                            quiz.getKnowledgePoints(),
                            quiz.getDescription(),
                            quiz.getTeacherId(),
                            quiz.getTeacherName(),
                            quiz.getCreateTime(),
                            quiz.getUpdatedTime(),
                            quiz.getDeadline(),
                            quizAnswerForQuiz == null ? 0 : quizAnswerForQuiz.getIsGraded() == 0 ? 1 : 2,
                            quizAnswerForQuiz == null ? -1 : quizAnswerForQuiz.getScore() == null ? 0 : quizAnswerForQuiz.getScore()
                    );
                }).toList();

        return new GetStudentQuizResponse("success","Get Your Quiz", userId, Quizzes.size(), Quizzes);
    }

    @Override
    public GetTeacherQuizResponse getTeacherQuiz(String userId) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        if(user == null) {
            return new GetTeacherQuizResponse("error","User not found", userId, 0, null);
        }

        // 查询教师的测验列表quiz
        List<Quiz> quizzes = quizRepository.findByTeacherId(userId);

        if (quizzes == null || quizzes.isEmpty()) {
            return new GetTeacherQuizResponse("success","No Your Quiz", userId, 0, null);
        }

        List<GetTeacherQuizResponse.quizDetail> Quizzes = quizzes.stream()
                .map(quiz -> {
                    int studentNum= studentRepository.countByStudentClass(quiz.getClass1(),quiz.getClass2());
                    int submitNum = quizAnswerRepository.countByQuizId(quiz.getQuiz_id());
                    int unSubmitNum = studentNum - submitNum;
                    int gradedNum = quizAnswerRepository.countByQuizIdAndIsGraded(quiz.getQuiz_id(), 1);
                    return new GetTeacherQuizResponse.quizDetail(
                            quiz.getQuiz_id(),
                            quiz.getTitle(),
                            quiz.getSubject(),
                            quiz.getContentType(),
                            quiz.getDifficulty(),
                            quiz.getKnowledgePoints(),
                            quiz.getDescription(),
                            quiz.getTeacherId(),
                            quiz.getTeacherName(),
                            quiz.getCreateTime(),
                            quiz.getUpdatedTime(),
                            quiz.getDeadline(),
                            submitNum,
                            unSubmitNum,
                            gradedNum
                    );
                }).toList();

        return new GetTeacherQuizResponse("success","Get Your Quiz", userId, Quizzes.size(), Quizzes);
    }

    @Override
    public Result createQuiz(CreateQuizRequest quizRequest) {
        // 创建测验逻辑
        Quiz quiz = new Quiz();
        quiz.setTitle(quizRequest.getTitle());
        quiz.setSubject(quizRequest.getSubject());
        quiz.setContentType(quizRequest.getContentType());
        quiz.setDifficulty(quizRequest.getDifficulty());
        quiz.setKnowledgePoints(quizRequest.getKnowledgePoints());
        quiz.setDescription(quizRequest.getDescription());
        quiz.setTeacherId(quizRequest.getTeacherId());
        quiz.setTeacherName(quizRequest.getTeacherName());
        quiz.setClass1(quizRequest.getClass1());
        quiz.setClass2(quizRequest.getClass2());
        quiz.setCreateTime(new Date());
        quiz.setUpdatedTime(new Date());
        quiz.setDeadline(quizRequest.getDeadline());

        // 保存测验到数据库
        quizRepository.save(quiz);

        return new Result(true, "Quiz created successfully", quiz);
    }

    @Override
    public Result modifyQuiz(ModifyQuizRequest quizRequest) {
        // 修改测验逻辑
        Quiz quiz = quizRepository.findByQuizId(quizRequest.getQuizId());

        quiz.setTitle(quizRequest.getTitle());
        quiz.setSubject(quizRequest.getSubject());
        quiz.setContentType(quizRequest.getContentType());
        quiz.setDifficulty(quizRequest.getDifficulty());
        quiz.setKnowledgePoints(quizRequest.getKnowledgePoints());
        quiz.setDescription(quizRequest.getDescription());
        quiz.setTeacherId(quizRequest.getTeacherId());
        quiz.setTeacherName(quizRequest.getTeacherName());
        quiz.setClass1(quizRequest.getClass1());
        quiz.setClass2(quizRequest.getClass2());
        quiz.setUpdatedTime(new Date());
        quiz.setDeadline(quizRequest.getDeadline());

        // 保存修改后的测验到数据库
        quizRepository.save(quiz);

        return new Result(true, "Quiz modified successfully", quiz);
    }

    @Override
    public Result deleteQuiz(Integer quizId) {
        // 删除测验逻辑
        Quiz quiz = quizRepository.findByQuizId(quizId);

        // 删除测验答案
        quizAnswerRepository.deleteByQuizId(quizId);

        // 删除测验
        quizRepository.delete(quiz);

        return new Result(true, "Quiz deleted successfully");
    }

    @Override
    public GetQuizStudentResponse getQuizStudent(Integer quizId) throws Exception{
        Quiz quiz = quizRepository.findByQuizId(quizId);

        if(quiz == null) {
            return new GetQuizStudentResponse("error","Quiz not found", quizId, 0, null);
        }

        // 查询测验的学生列表
        List<QuizAnswer> quizAnswers = quizAnswerRepository.findAllByQuizId(quizId);

        if (quizAnswers == null || quizAnswers.isEmpty()) {
            return new GetQuizStudentResponse("success","No Students for this Quiz", quizId, 0, null);
        }

        List<Student> students = studentRepository.findByStudentClass(quiz.getClass1(), quiz.getClass2());

        List<GetQuizStudentResponse.answerDetail> Quizzes = students.stream()
                .map(student -> {
                    QuizAnswer answer = quizAnswerRepository.findByStudentIdAndQuizId(student.getStudentId(), quizId);
                    if (answer == null) {
                        // Student does not have a QuizAnswer
                        return new GetQuizStudentResponse.answerDetail(
                                student.getStudentId(),
                                student.getStudentName(),
                                0, // status
                                null, // score
                                null, // answerTime
                                -1, // answerId
                                null  // answerContent
                        );
                    } else {
                        // Student has a QuizAnswer
                        int status = answer.getIsGraded() == 0 ? 1 : 2;
                        return new GetQuizStudentResponse.answerDetail(
                                student.getStudentId(),
                                student.getStudentName(),
                                status, // status based on isGraded
                                answer.getScore(), // score
                                answer.getSubmitTime() == null ? null : answer.getSubmitTime().toString(), // answerTime
                                answer.getAnswerId(), // answerId
                                answer.getAnswerContent() // answerContent
                        );
                    }
                }).toList();

        return new GetQuizStudentResponse("success","Get Students for this Quiz", quizId, Quizzes.size(), Quizzes);
    }

    @Override
    public GetMyQuizResponse getMyQuiz(String userId,Integer quizId) throws Exception{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        if(user == null) {
            return new GetMyQuizResponse("error","User not found",null,null, null, null, null,null,null,null,0,-1,null,-1);
        }

        Student student = studentRepository.findByStudentId(userId);
        if(student == null) {
            return new GetMyQuizResponse("error","Student not found",null,null, null, null, null,null,null,null,0,-1,null,-1);
        }

        Quiz quiz = quizRepository.findByQuizId(quizId);
        if(quiz == null) {
            return new GetMyQuizResponse("error","Quiz not found",null,null, null, null, null,null,null,null,0,-1,null,-1);
        }
        QuizAnswer quizAnswer = quizAnswerRepository.findByStudentIdAndQuizId(userId, quizId);
        if (quizAnswer == null) {
            return new GetMyQuizResponse("success","Get Your Quiz Without Answer",quiz.getQuiz_id(),quiz.getTitle(), quiz.getSubject(), quiz.getDifficulty(), quiz.getCreateTime(),quiz.getDeadline(),quiz.getTeacherName(),quiz.getTeacherId(),0,null,null,-1);
        }
        return new GetMyQuizResponse("success","Get Your Quiz", quizId, quiz.getTitle(), quiz.getSubject(),
                quiz.getDifficulty(), quiz.getCreateTime(), quiz.getDeadline(), quiz.getTeacherName(),
                quiz.getTeacherId(), quizAnswer.getIsGraded()==0?1:2, quizAnswer.getScore(),quizAnswer.getFeedback(), quizAnswer.getAnswerId());
    }

    @Override
    public Result gradeQuiz(Integer answerId,Integer score,String feedback) throws Exception {
        QuizAnswer quizAnswer = quizAnswerRepository.findByAnswerId(answerId);

        if(quizAnswer == null) {
            return new Result(false,"Quiz Answer not found",null);
        }

        // 更新分数和反馈
        quizAnswer.setScore(score);
        quizAnswer.setFeedback(feedback);
        quizAnswer.setIsGraded(1); // 设置为已批改
        quizAnswer.setGradeTime(new Date()); // 设置批改时间

        // 保存更新后的测验答案
        quizAnswerRepository.save(quizAnswer);

        return new Result(true,"Quiz graded successfully",quizAnswer);
    }

    @Override
    public  Integer getMaxAnswerId(){
        return quizAnswerRepository.findMaxResourceId();
    }

    @Override
    public Integer getMaxQuizId(){
        return quizRepository.findMaxQuizId();
    }

    @Override
    public Result submitQuiz(Integer quizId, String userId, String answerContent) throws Exception{
        // 检查测验是否存在
        Quiz quiz = quizRepository.findByQuizId(quizId);

        if(quiz == null) {
            return new Result(false,"Quiz not found",null);
        }

        // 检查学生是否存在
        Student student = studentRepository.findByStudentId(userId);
        if(student == null) {
            return new Result(false,"Student not found",null);
        }

        // 创建或更新测验答案
        QuizAnswer quizAnswer = quizAnswerRepository.findByStudentIdAndQuizId(userId, quizId);
        if (quizAnswer == null) {
            quizAnswer = new QuizAnswer();
            quizAnswer.setQuizId(quizId);
            quizAnswer.setStudentId(userId);
            quizAnswer.setSubmitTime(new Date());
        }

        quizAnswer.setAnswerContent(answerContent);
        quizAnswer.setIsGraded(0); // 设置为未批改
        quizAnswer.setSubmitTime(new Date());

        // 保存测验答案到数据库
        quizAnswerRepository.save(quizAnswer);

        return new Result(true,"Quiz submitted successfully",quizAnswer);
    }

    @Override
    public Result getTeacherClass(String userId) throws Exception{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found with id: " + userId));

        if(user == null) {
            return new Result(false,"User not found",null);
        }

        // 查询教师的班级列表
        List<String> classes = teacherRepository.findClassesByTeacherId(userId);

        if (classes == null || classes.isEmpty()) {
            return new Result(true,"No Classes found for this Teacher",null);
        }

        return new Result(true,"Get Teacher Classes successfully",classes);
    }

}
