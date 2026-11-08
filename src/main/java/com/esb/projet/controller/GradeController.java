package com.esb.projet.controller;

import com.esb.projet.model.Grade;
import com.esb.projet.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @GetMapping
    public List<Grade> getAllGrades() {
        return gradeService.getAllGrades();
    }

    @GetMapping("/{id}")
    public Grade getGradeById(@PathVariable Long id) {
        return gradeService.getGradeById(id);
    }

    @GetMapping("/student/{studentId}")
    public List<Grade> getGradesByStudentId(@PathVariable Long studentId) {
        return gradeService.getGradesByStudentId(studentId);
    }

    @GetMapping("/exam/{examId}")
    public List<Grade> getGradesByExamId(@PathVariable Long examId) {
        return gradeService.getGradesByExamId(examId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Grade createGrade(@Valid @RequestBody Grade grade) {
        return gradeService.createGrade(grade);
    }

    @PutMapping("/{id}")
    public Grade updateGrade(@PathVariable Long id, @Valid @RequestBody Grade gradeDetails) {
        return gradeService.updateGrade(id, gradeDetails);
    }

    @DeleteMapping("/{id}")
    public String deleteGrade(@PathVariable Long id) {
        return gradeService.deleteGrade(id);
    }
}
