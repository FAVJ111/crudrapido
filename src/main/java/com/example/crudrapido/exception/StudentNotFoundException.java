package com.example.crudrapido.exception;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String message) {
        super(message); // Llama al constructor de RuntimeException
    }
}
