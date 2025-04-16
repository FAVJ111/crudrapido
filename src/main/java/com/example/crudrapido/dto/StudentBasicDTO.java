package com.example.crudrapido.dto;

import lombok.Data;

@Data
public class StudentBasicDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String courseName;
}
