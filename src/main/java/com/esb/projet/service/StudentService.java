package com.esb.projet.service;

import com.esb.projet.model.Course;
import com.esb.projet.model.Student;
import com.esb.projet.model.StudentStatus;
import com.esb.projet.repository.CourseRepository;
import com.esb.projet.repository.GradeRepository;
import com.esb.projet.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private GradeRepository gradeRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Étudiant introuvable"));
    }

    public List<Student> searchByLastName(String keyword) {
        return studentRepository.findByLastNameContainingIgnoreCase(keyword);
    }

    // Requête personnalisée : étudiants inscrits à un cours
    public List<Student> getStudentsByCourseTitle(String courseTitle) {
        return studentRepository.findStudentByCourseTitle(courseTitle);
    }

    // Requête personnalisée : moyenne générale d'un étudiant
    public Map<String, Object> getStudentAverage(Long id) {
        Student student = getStudentById(id);
        Double average = gradeRepository.findAverageByStudent(id);
        if (average == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cet étudiant n'a pas encore de notes");
        }
        return Map.of("student", student.getFirstName() + " " + student.getLastName(),
                "average", Math.round(average * 100.0) / 100.0);
    }

    public Student createStudent(Student student) {
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }
        student.setId(null);
        student.setCourses(new ArrayList<>());   // les inscriptions se font via /enroll
        if (student.getStatus() == null) {
            student.setStatus(StudentStatus.ACTIVE);
        }
        return studentRepository.save(student);
    }

    public Student updateStudent(Long id, Student updated) {
        Student student = getStudentById(id);
        if (!student.getEmail().equals(updated.getEmail()) && studentRepository.existsByEmail(updated.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet email est déjà utilisé");
        }
        student.setFirstName(updated.getFirstName());
        student.setLastName(updated.getLastName());
        student.setEmail(updated.getEmail());
        student.setBirthDate(updated.getBirthDate());
        if (updated.getStatus() != null) {
            student.setStatus(updated.getStatus());
        }
        return studentRepository.save(student);
    }

    public String deleteStudent(Long id) {
        Student student = getStudentById(id);
        gradeRepository.deleteByStudentId(id);   // on supprime d'abord ses notes
        studentRepository.delete(student);
        return "Étudiant supprimé";
    }

    // Logique métier : inscrire un étudiant à un cours
    public Student enrollStudent(Long studentId, Long courseId) {
        Student student = getStudentById(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable"));

        // Règle 1 : seul un étudiant actif peut s'inscrire
        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seul un étudiant actif peut s'inscrire");
        }
        // Règle 2 : pas de double inscription
        if (student.getCourses().contains(course)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'étudiant est déjà inscrit à ce cours");
        }
        // Règle 3 : la capacité de la salle ne doit pas être dépassée
        if (course.getClassroom() != null
                && studentRepository.countByCoursesId(courseId) >= course.getClassroom().getCapacity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cours complet : la capacité de la salle est atteinte");
        }
        student.getCourses().add(course);
        return studentRepository.save(student);
    }

    public Student unenrollStudent(Long studentId, Long courseId) {
        Student student = getStudentById(studentId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cours introuvable"));
        if (!student.getCourses().remove(course)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'étudiant n'est pas inscrit à ce cours");
        }
        return studentRepository.save(student);
    }
}
