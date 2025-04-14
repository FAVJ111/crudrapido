package com.example.crudrapido.entity;

import org.hibernate.annotations.processing.Pattern;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tbl_student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is required")
    // @NotEmpty(message = "Last name is required")
    // @NotNull(message = "Last name is required")
    private String lastName;

    @jakarta.validation.constraints.Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(name = "email_address", unique = true, nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    public Student(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

}
