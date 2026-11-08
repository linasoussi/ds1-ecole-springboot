package com.esb.projet.controller;

import com.esb.projet.model.Classroom;
import com.esb.projet.service.ClassroomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;   // Injection du SERVICE (pas du repository)

    // Récupérer tous (GET)
    @GetMapping
    public List<Classroom> getAllClassrooms() {
        return classroomService.getAllClassrooms();
    }

    // Récupérer un par ID (GET)
    @GetMapping("/{id}")
    public Classroom getClassroomById(@PathVariable Long id) {
        return classroomService.getClassroomById(id);
    }

    // Créer (POST) -> 201 Created
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Classroom createClassroom(@Valid @RequestBody Classroom classroom) {
        return classroomService.createClassroom(classroom);
    }

    // Mettre à jour (PUT)
    @PutMapping("/{id}")
    public Classroom updateClassroom(@PathVariable Long id, @Valid @RequestBody Classroom classroom) {
        return classroomService.updateClassroom(id, classroom);
    }

    // Supprimer (DELETE)
    @DeleteMapping("/{id}")
    public String deleteClassroom(@PathVariable Long id) {
        return classroomService.deleteClassroom(id);
    }
}
