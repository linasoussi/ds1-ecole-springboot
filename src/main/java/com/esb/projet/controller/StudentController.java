package com.esb.projet.controller;

import com.esb.projet.model.Student;
import com.esb.projet.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;   // Injection du SERVICE (pas du repository)

    // Récupérer tous (GET)
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // Récupérer un par ID (GET)
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    // Recherche par nom (GET /api/students/search?name=ben)
    @GetMapping("/search")
    public List<Student> searchStudents(@RequestParam String name) {
        return studentService.searchByLastName(name);
    }

    // Requête personnalisée : étudiants inscrits à un cours (GET)
    @GetMapping("/course/{courseTitle}")
    public List<Student> getStudentsByCourse(@PathVariable String courseTitle) {
        return studentService.getStudentsByCourseTitle(courseTitle);
    }

    // Requête personnalisée : moyenne générale d'un étudiant (GET)
    @GetMapping("/{id}/average")
    public Map<String, Object> getStudentAverage(@PathVariable Long id) {
        return studentService.getStudentAverage(id);
    }

    // Inscrire un étudiant à un cours (POST)
    @PostMapping("/{studentId}/courses/{courseId}")
    public Student enrollStudent(@PathVariable Long studentId, @PathVariable Long courseId) {
        return studentService.enrollStudent(studentId, courseId);
    }

    // Désinscrire un étudiant d'un cours (DELETE)
    @DeleteMapping("/{studentId}/courses/{courseId}")
    public Student unenrollStudent(@PathVariable Long studentId, @PathVariable Long courseId) {
        return studentService.unenrollStudent(studentId, courseId);
    }

    // Créer (POST) -> 201 Created
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@Valid @RequestBody Student student) {
        return studentService.createStudent(student);
    }

    // Mettre à jour (PUT)
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @Valid @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    // Supprimer (DELETE)
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        return studentService.deleteStudent(id);
    }
}
