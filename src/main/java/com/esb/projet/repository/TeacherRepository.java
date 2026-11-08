package com.esb.projet.repository;

import com.esb.projet.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    boolean existsByEmail(String email);

    boolean existsByDepartmentId(Long departmentId);

    // REQUÊTE PERSONNALISÉE 1 : enseignants d'un département (par nom)
    @Query("SELECT t FROM Teacher t WHERE t.department.name = :departmentName")
    List<Teacher> findTeacherByDepartmentName(@Param("departmentName") String departmentName);
}
