package com.esb.projet.controller;

import com.esb.projet.model.Course;
import com.esb.projet.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;   // Injection du SERVICE (pas du repository)

    // Récupérer tous (GET)
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // Récupérer un par ID (GET)
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    // Requête personnalisée : cours populaires (GET /api/courses/popular?min=2)
    @GetMapping("/popular")
    public List<Course> getPopularCourses(@RequestParam(defaultValue = "1") int min) {
        return courseService.getPopularCourses(min);
    }

    // Requête personnalisée : moyenne des notes d'un cours (GET)
    @GetMapping("/{id}/average")
    public Map<String, Object> getCourseAverage(@PathVariable Long id) {
        return courseService.getCourseAverage(id);
    }

    // Créer (POST) -> 201 Created
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(@Valid @RequestBody Course course) {
        return courseService.createCourse(course);
    }

    // Mettre à jour (PUT)
    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @Valid @RequestBody Course course) {
        return courseService.updateCourse(id, course);
    }

    // Supprimer (DELETE)
    @DeleteMapping("/{id}")
    public String deleteCourse(@PathVariable Long id) {
        return courseService.deleteCourse(id);
    }
}
