package sba.sms.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "course")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @NonNull
    @Column(length = 50, nullable = false)
    String name;

    @NonNull
    @Column(length = 50, nullable = false)
    String instructor;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    List<Student> students = new ArrayList<>();

    /*
        Helper methods for maintaining the list of students and courses in the
        respective models for the many-to-many relationship
    */

    public void addStudent(Student student) {
        students.add(student);
        student.getCourses().add(this);
    }

    public void removeStudent(String email) {

    }

    /*
        Checks if the parameter Object is the same as this Course Object, then
        if not checks if the parameter Object is a Course and returns a comparison
        based on the unique ID fields of this Course and parameter Course
    */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        return getId() == course.getId();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
