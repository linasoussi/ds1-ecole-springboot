package com.esb.projet;

import com.esb.projet.model.Classroom;
import com.esb.projet.model.Course;
import com.esb.projet.model.Student;
import com.esb.projet.model.Teacher;
import com.esb.projet.model.Department;
import com.esb.projet.service.ClassroomService;
import com.esb.projet.service.CourseService;
import com.esb.projet.service.DepartmentService;
import com.esb.projet.service.StudentService;
import com.esb.projet.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class StudentServiceTest {

    @Autowired private StudentService studentService;
    @Autowired private CourseService courseService;
    @Autowired private TeacherService teacherService;
    @Autowired private DepartmentService departmentService;
    @Autowired private ClassroomService classroomService;

    private Course creerCoursDansSalleDeUnePlace() {
        Department d = new Department();
        d.setName("Informatique");
        d = departmentService.createDepartment(d);

        Teacher t = new Teacher();
        t.setFirstName("Hatem");
        t.setLastName("Awadi");
        t.setEmail("hatem@esb.tn");
        t.setDepartment(d);
        t = teacherService.createTeacher(t);

        Classroom r = new Classroom();
        r.setName("Salle A1");
        r.setCapacity(1);
        r = classroomService.createClassroom(r);

        Course c = new Course();
        c.setTitle("Spring Boot");
        c.setCredits(4);
        c.setTeacher(t);
        c.setClassroom(r);
        return courseService.createCourse(c);
    }

    private Student creerEtudiant(String email) {
        Student s = new Student();
        s.setFirstName("Ali");
        s.setLastName("Ben Salah");
        s.setEmail(email);
        return studentService.createStudent(s);
    }

    @Test
    void inscriptionRefuseeQuandLaSalleEstPleine() {
        Course cours = creerCoursDansSalleDeUnePlace();
        Student s1 = creerEtudiant("s1@esb.tn");
        Student s2 = creerEtudiant("s2@esb.tn");

        studentService.enrollStudent(s1.getId(), cours.getId());

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> studentService.enrollStudent(s2.getId(), cours.getId()));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    }

    @Test
    void emailDejaUtiliseRenvoie409() {
        creerEtudiant("doublon@esb.tn");
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> creerEtudiant("doublon@esb.tn"));
        assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    }

    @Test
    void etudiantInexistantRenvoie404() {
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> studentService.getStudentById(9999L));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }
}
