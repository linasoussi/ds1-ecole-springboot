package com.esb.projet.service;

import com.esb.projet.model.Classroom;
import com.esb.projet.repository.CourseRepository;
import com.esb.projet.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ClassroomService {

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public Classroom getClassroomById(Long id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salle introuvable"));
    }

    public Classroom createClassroom(Classroom classroom) {
        if (classroomRepository.existsByName(classroom.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Une salle avec ce nom existe déjà");
        }
        classroom.setId(null);
        return classroomRepository.save(classroom);
    }

    public Classroom updateClassroom(Long id, Classroom updated) {
        Classroom classroom = getClassroomById(id);
        if (!classroom.getName().equals(updated.getName()) && classroomRepository.existsByName(updated.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Une salle avec ce nom existe déjà");
        }
        classroom.setName(updated.getName());
        classroom.setCapacity(updated.getCapacity());
        return classroomRepository.save(classroom);
    }

    public String deleteClassroom(Long id) {
        Classroom classroom = getClassroomById(id);
        if (courseRepository.existsByClassroomId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de supprimer : cette salle est utilisée par des cours");
        }
        classroomRepository.delete(classroom);
        return "Salle supprimée";
    }
}
