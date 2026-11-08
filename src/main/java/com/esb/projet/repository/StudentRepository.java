package com.esb.projet.repository;

import com.esb.projet.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    // Nombre d'étudiants inscrits à un cours / existence d'inscrits
    long countByCoursesId(Long courseId);

    boolean existsByCoursesId(Long courseId);

    // Requête dérivée du nom de la méthode (pas besoin de @Query)
    List<Student> findByLastNameContainingIgnoreCase(String keyword);

    // REQUÊTE PERSONNALISÉE 3 : étudiants inscrits à un cours (par titre)
    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.title = :courseTitle")
    List<Student> findStudentByCourseTitle(@Param("courseTitle") String courseTitle);
}
