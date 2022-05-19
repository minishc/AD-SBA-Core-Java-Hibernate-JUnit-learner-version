package sba.sms.services;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
class StudentServiceTest {

    static StudentService studentService;
    static Student expectedStudent;
    @BeforeAll
    static void beforeAll() {
        studentService = new StudentService();
        CommandLine.addData();
        expectedStudent = new Student("expected@gmail.com", "expected student", "password");
    }

    @Test
    void getAllStudents() {

        List<Student> expected = new ArrayList<>(Arrays.asList(
                new Student("reema@gmail.com", "reema brown", "password"),
                new Student("annette@gmail.com", "annette allen", "password"),
                new Student("anthony@gmail.com", "anthony gallegos", "password"),
                new Student("ariadna@gmail.com", "ariadna ramirez", "password"),
                new Student("bolaji@gmail.com", "bolaji saibu", "password"),
                expectedStudent
        ));
        assertThat(studentService.getAllStudents()).hasSameElementsAs(expected);
    }

    @Order(2)
    @Test
    void createStudent() {
        studentService.createStudent(expectedStudent);
        assertThat(studentService.getStudentByEmail(expectedStudent.getEmail())).isNotNull().isEqualTo(expectedStudent);
        Student notExpectedToWork = new Student("reema@gmail.com", "this isn't reema", "new password");
        studentService.createStudent(notExpectedToWork);
        assertThat(studentService.getAllStudents()).doesNotContain(notExpectedToWork);
        assertThat(studentService.getStudentByEmail(notExpectedToWork.getEmail())).isNotEqualTo(notExpectedToWork);
    }

    @Test
    @Order(1)
    void getStudentByEmail() {
        Student expected = new Student("reema@gmail.com", "reema brown", "password");
        assertThat(studentService.getStudentByEmail(expected.getEmail())).isNotNull().isEqualTo(expected);
        assertThat(studentService.getStudentByEmail(" ")).isNull();
    }

    @Test
    void validateStudent() {
        String email = "reema@gmail.com";
        String validPassword = "password";
        String invalidPassword = "01349u3iolqermlkf";

        assertThat(studentService.validateStudent(email, validPassword)).isTrue();
        assertThat(studentService.validateStudent(email, invalidPassword)).isFalse();
    }

    @Test
    void registerStudentToCourse() {
        Student student = studentService.getStudentByEmail("reema@gmail.com");
        Course expected = new CourseService().getCourseById(1);
        studentService.registerStudentToCourse(student.getEmail(), expected.getId());
        assertThat(studentService.getStudentCourses(student.getEmail()))
                .isNotNull().hasSize(1).containsExactly(expected);
    }

    @Test
    void getStudentCourses() {
        Student student = studentService.getStudentByEmail("anthony@gmail.com");
        CourseService cs = new CourseService();
        List<Course> expected = new ArrayList<>(cs.getAllCourses());
        expected.forEach((course) -> studentService.registerStudentToCourse(student.getEmail(), course.getId()));
        assertThat(studentService.getStudentCourses(student.getEmail())).isNotNull().hasSize(5)
                .containsAll(expected);
    }
}