package com.esb.projet.repository;

import com.esb.projet.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByTitle(String title);

    boolean existsByTeacherId(Long teacherId);

    boolean existsByClassroomId(Long classroomId);

    // REQUÊTE PERSONNALISÉE 2 : cours "populaires" (au moins :min étudiants inscrits)
    @Query("SELECT c FROM Course c WHERE (SELECT COUNT(s) FROM Student s JOIN s.courses sc WHERE sc.id = c.id) >= :min")
    List<Course> findPopularCourses(@Param("min") int min);
}
