package com.esb.projet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "grades", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "exam_id"}))
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La note est obligatoire")
    @PositiveOrZero(message = "La note doit être positive ou nulle")
    @Column(name = "score", nullable = false)
    private Double score;

    @NotNull(message = "L'étudiant est obligatoire")
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "L'examen est obligatoire")
    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    // Champ calculé : non persisté en base (@Transient, Chapitre 3)
    @Transient
    private String mention;

    public String getMention() {
        if (score == null || exam == null || exam.getMaxScore() <= 0) {
            return null;
        }
        double sur20 = score * 20.0 / exam.getMaxScore();
        if (sur20 >= 16) return "Très bien";
        if (sur20 >= 14) return "Bien";
        if (sur20 >= 10) return "Passable";
        return "Insuffisant";
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

}
