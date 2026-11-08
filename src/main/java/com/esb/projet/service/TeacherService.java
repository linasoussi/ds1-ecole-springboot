package com.esb.projet.service;

import com.esb.projet.model.Department;
import com.esb.projet.model.Teacher;
import com.esb.projet.repository.CourseRepository;
import com.esb.projet.repository.DepartmentRepository;
import com.esb.projet.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enseignant introuvable"));
    }

    // Requête personnalisée : enseignants d'un département
    public List<Teacher> getTeachersByDepartmentName(String departmentName) {
        return teacherRepository.findTeacherByDepartmentName(departmentName);
    }

    public Teacher createTeacher(Teacher teacher) {
        if (teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }
        teacher.setId(null);
        teacher.setDepartment(findDepartment(teacher));
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Long id, Teacher updated) {
        Teacher teacher = getTeacherById(id);
        if (!teacher.getEmail().equals(updated.getEmail()) && teacherRepository.existsByEmail(updated.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }
        teacher.setFirstName(updated.getFirstName());
        teacher.setLastName(updated.getLastName());
        teacher.setEmail(updated.getEmail());
        teacher.setDepartment(findDepartment(updated));
        return teacherRepository.save(teacher);
    }

    public String deleteTeacher(Long id) {
        Teacher teacher = getTeacherById(id);
        if (courseRepository.existsByTeacherId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de supprimer : cet enseignant donne encore des cours");
        }
        teacherRepository.delete(teacher);
        return "Enseignant supprimé";
    }

    // Le JSON contient seulement {"department": {"id": 1}} : on recharge le vrai département
    private Department findDepartment(Teacher teacher) {
        if (teacher.getDepartment() == null || teacher.getDepartment().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "department.id est obligatoire");
        }
        return departmentRepository.findById(teacher.getDepartment().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Département introuvable"));
    }
}
