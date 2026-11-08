package com.esb.projet.controller;

import com.esb.projet.model.Teacher;
import com.esb.projet.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;   // Injection du SERVICE (pas du repository)

    // Récupérer tous (GET)
    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    // Récupérer un par ID (GET)
    @GetMapping("/{id}")
    public Teacher getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id);
    }

    // Requête personnalisée : enseignants par nom de département (GET)
    @GetMapping("/department/{departmentName}")
    public List<Teacher> getTeachersByDepartment(@PathVariable String departmentName) {
        return teacherService.getTeachersByDepartmentName(departmentName);
    }

    // Créer (POST) -> 201 Created
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Teacher createTeacher(@Valid @RequestBody Teacher teacher) {
        return teacherService.createTeacher(teacher);
    }

    // Mettre à jour (PUT)
    @PutMapping("/{id}")
    public Teacher updateTeacher(@PathVariable Long id, @Valid @RequestBody Teacher teacher) {
        return teacherService.updateTeacher(id, teacher);
    }

    // Supprimer (DELETE)
    @DeleteMapping("/{id}")
    public String deleteTeacher(@PathVariable Long id) {
        return teacherService.deleteTeacher(id);
    }
}
