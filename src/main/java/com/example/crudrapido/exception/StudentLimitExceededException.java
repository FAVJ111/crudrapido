package com.example.crudrapido.exception;

public class StudentLimitExceededException extends RuntimeException {
    public StudentLimitExceededException(String message) {
        super(message);
    }
}
