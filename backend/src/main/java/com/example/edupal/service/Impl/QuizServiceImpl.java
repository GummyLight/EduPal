package com.example.edupal.service.Impl;

import com.example.edupal.common.Result;
import com.example.edupal.dto.request.CreateQuizRequest;
import com.example.edupal.dto.request.ModifyQuizRequest;
import com.example.edupal.dto.response.GetQuizStudentRepsonse;
import com.example.edupal.dto.response.GetTeacherQuizResponse;
import com.example.edupal.model.Quiz;
import com.example.edupal.model.QuizAnswer;
import com.example.edupal.model.Student;
import com.example.edupal.model.User;
import com.example.edupal.repository.QuizAnswerRepository;
import com.example.edupal.repository.QuizRepository;
import com.example.edupal.repository.StudentRepository;
import com.example.edupal.repository.UserRepository;
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
                            quiz.getCreateTime().toString(),
                            quiz.getUpdatedTime().toString(),
                            quiz.getDeadline().toString(),
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
                            quiz.getCreateTime().toString(),
                            quiz.getUpdatedTime().toString(),
                            quiz.getDeadline().toString(),
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
        Quiz quiz = quizRepository.findById(quizRequest.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizRequest.getQuizId()));

        quiz.setTitle(quizRequest.getTitle());
        quiz.setSubject(quizRequest.getSubject());
        quiz.setContentType(quizRequest.getContentType());
        quiz.setDifficulty(quizRequest.getDifficulty());
        quiz.setKnowledgePoints(quizRequest.getKnowledgePoints());
        quiz.setDescription(quizRequest.getDescription());
        quiz.setTeacherId(quizRequest.getTeacherId());
        quiz.setTeacherName(quizRequest.getTeacherName());
        quiz.setUpdatedTime(new Date());
        quiz.setDeadline(quizRequest.getDeadline());

        // 保存修改后的测验到数据库
        quizRepository.save(quiz);

        return new Result(true, "Quiz modified successfully", quiz);
    }

    @Override
    public Result deleteQuiz(String quizId) {
        // 删除测验逻辑
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));

        // 删除测验答案
        quizAnswerRepository.deleteByQuizId(quizId);

        // 删除测验
        quizRepository.delete(quiz);

        return new Result(true, "Quiz deleted successfully");
    }

    @Override
    public GetQuizStudentRepsonse getQuizStudent(String quizId) throws Exception{
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new Exception("Quiz not found with id: " + quizId));

        if(quiz == null) {
            return new GetQuizStudentRepsonse("error","Quiz not found", quizId, 0, null);
        }

        // 查询测验的学生列表
        List<QuizAnswer> quizAnswers = quizAnswerRepository.findAllByQuizId(quizId);

        if (quizAnswers == null || quizAnswers.isEmpty()) {
            return new GetQuizStudentRepsonse("success","No Students for this Quiz", quizId, 0, null);
        }

        List<Student> students = studentRepository.findByStudentClass(quiz.getClass1(), quiz.getClass2());

        List<GetQuizStudentRepsonse.answerDetail> Quizzes = students.stream()
                .map(student -> {
                    QuizAnswer answer = quizAnswerRepository.findByStudentIdAndQuizId(student.getStudentId(), quizId);
                    if (answer == null) {
                        // Student does not have a QuizAnswer
                        return new GetQuizStudentRepsonse.answerDetail(
                                student.getStudentId(),
                                student.getStudentName(),
                                0, // status
                                null, // score
                                null, // answerTime
                                null, // answerId
                                null  // answerContent
                        );
                    } else {
                        // Student has a QuizAnswer
                        int status = answer.getIsGraded() == 0 ? 1 : 2;
                        return new GetQuizStudentRepsonse.answerDetail(
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

        return new GetQuizStudentRepsonse("success","Get Students for this Quiz", quizId, Quizzes.size(), Quizzes);
    }

}
