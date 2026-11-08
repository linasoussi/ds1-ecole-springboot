package com.esb.projet.service;

import com.esb.projet.model.Department;
import com.esb.projet.repository.TeacherRepository;
import com.esb.projet.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Département introuvable"));
    }

    public Department createDepartment(Department department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un département avec ce nom existe déjà");
        }
        department.setId(null);
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, Department updated) {
        Department department = getDepartmentById(id);
        if (!department.getName().equals(updated.getName()) && departmentRepository.existsByName(updated.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un département avec ce nom existe déjà");
        }
        department.setName(updated.getName());
        department.setDescription(updated.getDescription());
        return departmentRepository.save(department);
    }

    public String deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        // Règle métier : on ne supprime pas un département qui a encore des enseignants
        if (teacherRepository.existsByDepartmentId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de supprimer : ce département contient encore des enseignants");
        }
        departmentRepository.delete(department);
        return "Département supprimé";
    }
}
