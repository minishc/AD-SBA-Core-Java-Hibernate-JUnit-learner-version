package sba.sms.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "student")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {
    @Id
    @NonNull
    @Column(length = 50, nullable = false, name = "email")
    String email;

    @NonNull
    @Column(length = 50, nullable = false, name = "name")
    String name;

    @NonNull
    @Column(length = 50, nullable = false, name = "password")
    String password;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
        fetch = FetchType.EAGER)
    @JoinTable(name = "student_courses",
        joinColumns = @JoinColumn(name = "student_email"),
        inverseJoinColumns = @JoinColumn(name = "courses_id"))
    List<Course> courses = new ArrayList<>();

    public void addCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }

    /*
        Checks to ensure that the object to be compared is a Student
        and then compares the unique identifier of the Student's email
        to see if this Student is the same as the parameter Student
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;

        Student student = (Student) o;

        return  getEmail().equals(student.getEmail()) &&
                getName().equals(student.getName()) &&
                getPassword().equals(student.getPassword());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
