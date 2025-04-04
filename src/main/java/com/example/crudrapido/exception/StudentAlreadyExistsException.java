package com.example.crudrapido.exception;

public class StudentAlreadyExistsException extends RuntimeException {

    public StudentAlreadyExistsException(String message) {
        super(message); // Llama al constructor de RuntimeException
    }
}
