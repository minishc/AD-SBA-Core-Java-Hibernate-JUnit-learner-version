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
    @ManyToMany(mappedBy = "courses", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    List<Student> students = new ArrayList<>();

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
