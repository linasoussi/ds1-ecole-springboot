package com.esb.projet.service;

import com.esb.projet.exception.ResourceNotFoundException;
import com.esb.projet.model.Exam;
import com.esb.projet.model.Grade;
import com.esb.projet.model.Student;
import com.esb.projet.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ExamService examService;

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found with id " + id));
    }

    public List<Grade> getGradesByStudentId(Long studentId) {
        studentService.getStudentById(studentId);
        return gradeRepository.findByStudentId(studentId);
    }

    public List<Grade> getGradesByExamId(Long examId) {
        examService.getExamById(examId);
        return gradeRepository.findByExamId(examId);
    }

    public Grade createGrade(Grade grade) {
        if (grade.getStudent() == null || grade.getStudent().getId() == null
                || grade.getExam() == null || grade.getExam().getId() == null) {
            throw new IllegalArgumentException("Student id and exam id are required");
        }
        Student student = studentService.getStudentById(grade.getStudent().getId());
        Exam exam = examService.getExamById(grade.getExam().getId());

        // Règles métier
        if (!studentService.isEnrolled(student, exam.getCourse().getId())) {
            throw new IllegalArgumentException("Student is not enrolled in the course of this exam");
        }
        if (gradeRepository.existsByStudentIdAndExamId(student.getId(), exam.getId())) {
            throw new IllegalArgumentException("A grade already exists for this student and this exam");
        }
        checkScore(grade.getScore(), exam);

        grade.setId(null);
        grade.setStudent(student);
        grade.setExam(exam);
        return gradeRepository.save(grade);
    }

    public Grade updateGrade(Long id, Grade gradeDetails) {
        Grade grade = getGradeById(id);
        checkScore(gradeDetails.getScore(), grade.getExam());
        grade.setScore(gradeDetails.getScore());
        return gradeRepository.save(grade);
    }

    public String deleteGrade(Long id) {
        Grade grade = getGradeById(id);
        gradeRepository.delete(grade);
        return "Grade deleted";
    }

    private void checkScore(Double score, Exam exam) {
        if (score != null && score > exam.getMaxScore()) {
            throw new IllegalArgumentException("Score cannot exceed the exam max score (" + exam.getMaxScore() + ")");
        }
    }
}
