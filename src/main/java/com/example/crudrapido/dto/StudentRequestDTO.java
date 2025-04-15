package com.example.crudrapido.dto;

import java.util.Set;

public class StudentRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private Long courseId;
    private Set<Long> activityIds;

    // Constructor vacío
    public StudentRequestDTO() {}

    // Constructor completo
    public StudentRequestDTO(String firstName, String lastName, String email, Long courseId, Set<Long> activityIds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.courseId = courseId;
        this.activityIds = activityIds;
    }

    // Getters y Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Set<Long> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(Set<Long> activityIds) {
        this.activityIds = activityIds;
    }
}
