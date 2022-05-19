package sba.sms.services;

import jakarta.persistence.TypedQuery;
import lombok.extern.java.Log;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

@Log
public class StudentService implements StudentI {

    /**
     * Get a list of all students within the database
     *
     * @return An ArrayList containing all Students within the database
     */
    @Override
    public List<Student> getAllStudents() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        List<Student> students = new ArrayList<>();
        try {
            students = session.createQuery("FROM Student", Student.class).getResultList();
            tx.commit();
        }
        catch(Exception exception) {
            log.info(exception.toString());
            if(tx != null && tx.isActive()) {
                tx.rollback();
            }
        }

        session.close();
        return students;
    }

    /**
     * Adds a Student entry to the student table within the database
     *
     * @param student A Student object to be persisted to the database
     */
    @Override
    public void createStudent(Student student) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        //tries to persist the student to the database
        try {
            session.persist(student);
            tx.commit();
        } //if an exception occurs prints the exception info and rolls back the transaction
        catch(Exception exception) {
            log.info("Unable to create the student in the database");
            if(tx != null && tx.isActive()) {
                tx.rollback();
            }
        }
        //session is over close it
        session.close();
    }

    /**
     * Get a student from the database according to the email address of the student
     *
     * @param email The email address of the Student to be retrieved from the database
     * @return Will return a Student object if a student is found or will return null if a student is not found
     */
    @Override
    public Student getStudentByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Student result = null;
        Transaction tx = session.beginTransaction();

        //tries to query the database for a student with matching email
        try {
            TypedQuery<Student> query = session.createQuery("FROM Student WHERE email = :email", Student.class);
            query.setParameter("email", email);
            result = query.getSingleResult();
            tx.commit();
        } //if an exception occurs roll back the transaction, default result is null so do nothing with it
        catch(Exception exception) {
            log.info("There was no student found with this email address");
            if(tx != null && tx.isActive()) {
                tx.rollback();
            }
        }
        //close the session and return results from the query or null
        session.close();
        return result;
    }

    /**
     * Validates that a student's credentials are identical to how they are stored in the database
     *
     * @param email A student's email to check
     * @param password The password for the student
     * @return Will return true if the student's credentials match, false otherwise
     */
    @Override
    public boolean validateStudent(String email, String password) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        boolean result = false; //default value is invalid student credentials
        Transaction tx = session.beginTransaction();

        //try to retrieve a student with the credentials that were given
        try {
            TypedQuery<Student> query = session.createQuery("FROM Student WHERE :email = email AND :pw = password",
                    Student.class);
            query.setParameter("email", email);
            query.setParameter("pw", password);
            Student verification = query.getSingleResult();
            //if we get a Student value from the database adjust the result to valid credentials
            if(verification != null) {
                result = true;
            }
        } //in the case of an exception result is already false, roll back the transaction
        catch(Exception exception) {
            log.info("Invalid credentials please enter again");
            if(tx != null && tx.isActive()) {
                tx.rollback();
            }
        }
        //close the session and return the results
        session.close();
        return result;
    }

    /**
     * Updates a Student's list of courses and adds this Student to the Course's list of students
     *
     * @param email Email address of the student to add to a course
     * @param courseId The id of the course to add the student to
     */
    @Override
    public void registerStudentToCourse(String email, int courseId) {
        //retrieving relevant student and course from the database
        Student student = getStudentByEmail(email);
        CourseService service = new CourseService();
        Course course = service.getCourseById(courseId);
        //make sure that there were matching entries in the database if there aren't then just do nothing and return
        if(student != null && course != null) {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            //try adding the course for the student and merging the change
            try {
                student.addCourse(course);
                session.merge(student);
                tx.commit();
            } //in the event of an exception roll back the transaction
            catch(Exception exception) {
                log.info(exception.toString());
                if(tx != null && tx.isActive()) {
                    tx.rollback();
                }
            }
            //close the session
            session.close();
        }
    }

    /**
     * Get all courses that a student is currently registered for
     *
     * @param email Email address of the student to retrieve course information for
     * @return Returns a list of courses that the student is registered for
     */
    @Override
    public List<Course> getStudentCourses(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        List<Course> courses = new ArrayList<>();

        try {
            //selecting course information from course table based on the student's email
            NativeQuery<Course> query = session.createNativeQuery("SELECT c.id, c.name, c.instructor FROM student AS s " +
                                        "JOIN student_courses AS sc ON s.email = sc.student_email " +
                                        "JOIN course AS c ON sc.courses_id = c.id WHERE s.email = ?", Course.class);
            query.setParameter(1, email);
            courses = query.getResultList();
            tx.commit();
        } //if an exception is generated then print the exception and roll back the transaction
        catch(Exception exception) {
            log.info(exception.toString());
            if(tx != null && tx.isActive()) {
                tx.rollback();
            }
        }
        //close the session and return the list of courses, populated or not
        session.close();
        return courses;
    }
}
