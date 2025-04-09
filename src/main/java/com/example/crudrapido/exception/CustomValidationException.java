package com.example.crudrapido.exception;

import java.util.List;

public class CustomValidationException extends RuntimeException {
    private List<String> errors;

    public CustomValidationException(List<String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    // Agregar un mensaje de error para ser usado en los manejadores
    @Override
    public String getMessage() {
        return String.join(", ", errors);
    }
}
