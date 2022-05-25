package sba.sms.services;

import org.junit.jupiter.api.*;
import sba.sms.models.Course;
import sba.sms.utils.CommandLine;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class CourseServiceTest {

    static Course testCourse;
    static CourseService testService;

    @BeforeAll
    static void beforeAll() {
        testCourse = new Course("C", "Chris");
        testService = new CourseService();
    }

    @Order(2)
    @Test
    void createCourse() {
        testService.createCourse(testCourse);
        testCourse.setId(6);
        Course expected = testCourse;
        Course actual = testService.getCourseById(6);
        assertEquals(expected, actual);
        testService.createCourse(null);
        assertNull(testService.getCourseById(7));
    }

    @Order(1)
    @Test
    void getCourseById() {
        Course expected = new Course("Java", "Phillip Witkin");
        expected.setId(1);
        Course actual = testService.getCourseById(1);
        assertEquals(expected, actual);
        assertNull(testService.getCourseById(0));
    }

    @Order(3)
    @Test
    void getAllCourses() {
        List<Course> expected = new ArrayList<>();
        Course course1 = new Course("Java", "Phillip Witkin");
        course1.setId(1);

        Course course2 = new Course("Frontend", "Kasper Kain");
        course2.setId(2);

        Course course3 = new Course("JPA", "Jafer Alhaboubi");
        course3.setId(3);

        Course course4 = new Course("Spring Framework", "Phillip Witkin");
        course4.setId(4);

        Course course5 = new Course("SQL", "Phillip Witkin");
        course5.setId(5);

        expected.add(course1);
        expected.add(course2);
        expected.add(course3);
        expected.add(course4);
        expected.add(course5);
        expected.add(testCourse);

        List<Course> actual = testService.getAllCourses();
        assertEquals(expected, actual);
    }
}
