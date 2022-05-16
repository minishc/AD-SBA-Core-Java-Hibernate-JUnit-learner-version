package sba.sms.services;


import jakarta.persistence.TypedQuery;
import lombok.extern.java.Log;
import org.hibernate.Session;
import org.hibernate.Transaction;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

@Log
public class CourseService implements CourseI {

    /**
     * Persists a Course object into the database
     *
     * @param course The Course object to be persisted into the database
     */
    @Override
    public void createCourse(Course course) {
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            session.beginTransaction();
            session.persist(course);
            session.getTransaction().commit();
        }
        catch(Exception exception) {
            log.info(exception.toString());
        }

        session.close();
    }

    /**
     * Retrieves a Course entry from the course table in the database based
     * on the unique id
     *
     * @param courseId The unique id of the course to be retrieved from the course table
     * @return Returns the Course entry that corresponds to the course id or null if no Course entry is found
     */
    @Override
    public Course getCourseById(int courseId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Course result = null;

        try {
            TypedQuery<Course> query = session.createQuery("FROM Course WHERE id = :id", Course.class);
            query.setParameter(1, courseId);
            result = query.getSingleResult();
            tx.commit();
        }
        catch(Exception exception) {
            log.info(exception.toString());
            if(tx != null && tx.isActive()) {
                tx.rollback();
            }
        }

        session.close();
        return result;
    }

    /**
     * Get a list of all the courses that exist within the database
     *
     * @return A list of the courses that exist within the database
     */
    @Override
    public List<Course> getAllCourses() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        List<Course> courses = new ArrayList<>();

        try {
            courses = session.createQuery("FROM Course", Course.class).getResultList();
            tx.commit();
        }
        catch(Exception exception) {
            log.info(exception.toString());
            if(tx != null && tx.isActive()) {
                tx.rollback();
            }
        }

        session.close();
        return courses;
    }
}
