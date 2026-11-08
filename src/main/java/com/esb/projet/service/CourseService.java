package com.esb.projet.service;

import com.esb.projet.model.Classroom;
import com.esb.projet.model.Course;
import com.esb.projet.model.Teacher;
import com.esb.projet.repository.StudentRepository;
import com.esb.projet.repository.ClassroomRepository;
import com.esb.projet.repository.CourseRepository;
import com.esb.projet.repository.GradeRepository;
import com.esb.projet.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable"));
    }

    // Requête personnalisée : cours ayant au moins "min" étudiants, triés du plus au moins suivi
    public List<Course> getPopularCourses(int min) {
        if (min < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "min doit être supérieur ou égal à 1");
        }
        List<Course> courses = new ArrayList<>(courseRepository.findPopularCourses(min));
        courses.sort(Comparator.comparingLong((Course c) -> studentRepository.countByCoursesId(c.getId())).reversed());
        return courses;
    }

    // Requête personnalisée : moyenne d'un cours
    public Map<String, Object> getCourseAverage(Long id) {
        Course course = getCourseById(id);
        Double average = gradeRepository.findAverageByCourse(id);
        if (average == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucune note pour ce cours");
        }
        return Map.of("course", course.getTitle(), "average", Math.round(average * 100.0) / 100.0);
    }

    public Course createCourse(Course course) {
        if (courseRepository.existsByTitle(course.getTitle())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un cours avec ce titre existe déjà");
        }
        course.setId(null);
        course.setTeacher(findTeacher(course));
        course.setClassroom(findClassroom(course));
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course updated) {
        Course course = getCourseById(id);
        if (!course.getTitle().equals(updated.getTitle()) && courseRepository.existsByTitle(updated.getTitle())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un cours avec ce titre existe déjà");
        }
        Classroom classroom = findClassroom(updated);
        // Règle métier : la nouvelle salle doit pouvoir contenir les étudiants déjà inscrits
        if (classroom.getCapacity() < studentRepository.countByCoursesId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La salle est trop petite pour les étudiants déjà inscrits");
        }
        course.setTitle(updated.getTitle());
        course.setCredits(updated.getCredits());
        course.setTeacher(findTeacher(updated));
        course.setClassroom(classroom);
        return courseRepository.save(course);
    }

    public String deleteCourse(Long id) {
        Course course = getCourseById(id);
        if (studentRepository.existsByCoursesId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de supprimer : des étudiants sont inscrits à ce cours");
        }
        gradeRepository.deleteByCourseId(id);   // on supprime d'abord les notes du cours
        courseRepository.delete(course);
        return "Cours supprimé";
    }

    private Teacher findTeacher(Course course) {
        if (course.getTeacher() == null || course.getTeacher().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teacher.id est obligatoire");
        }
        return teacherRepository.findById(course.getTeacher().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enseignant introuvable"));
    }

    private Classroom findClassroom(Course course) {
        if (course.getClassroom() == null || course.getClassroom().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "classroom.id est obligatoire");
        }
        return classroomRepository.findById(course.getClassroom().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salle introuvable"));
    }
}
