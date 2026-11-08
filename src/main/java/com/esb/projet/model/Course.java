package com.esb.projet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre du cours est obligatoire")
    @Column(nullable = false, unique = true)
    private String title;

    @NotNull(message = "Les crédits sont obligatoires")
    @Min(value = 1, message = "Minimum 1 crédit")
    @Max(value = 10, message = "Maximum 10 crédits")
    private Integer credits;

    // Plusieurs cours pour un seul enseignant
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // Plusieurs cours dans une même salle
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    // Getters et Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }
}
