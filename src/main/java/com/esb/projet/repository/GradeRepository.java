package com.esb.projet.repository;

import com.esb.projet.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findByStudentId(Long studentId);

    List<Grade> findByExamId(Long examId);

    boolean existsByStudentId(Long studentId);

    boolean existsByExamId(Long examId);

    boolean existsByStudentIdAndExamId(Long studentId, Long examId);

    // Requête personnalisée n°6 : moyenne d'un étudiant ramenée sur 20
    @Query("SELECT AVG(g.score * 20.0 / e.maxScore) FROM Grade g JOIN g.exam e WHERE g.student.id = :studentId")
    Double findAverageOn20ByStudentId(@Param("studentId") Long studentId);
}
