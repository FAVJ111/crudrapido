package com.example.crudrapido.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;



import com.example.crudrapido.entity.Student;
import com.example.crudrapido.exception.StudentAlreadyExistsException;
import com.example.crudrapido.exception.StudentNotFoundException;
import com.example.crudrapido.exception.CustomValidationException;
import com.example.crudrapido.exception.ErrorResponse;
import com.example.crudrapido.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Estudiantes", description = "API para la gestión de estudiantes")
@RestController
@RequestMapping(path = "api/v1/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Operation(summary = "Obtener todos los estudiantes", description = "Retorna una lista de todos los estudiantes registrados")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Student> getAll() {
        return studentService.getAllStudents();
    }

    @Operation(summary = "Obtener un estudiante por ID", description = "Busca un estudiante usando su ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{studentId}")
    public Student getById(@PathVariable("studentId") Long studentId) {
        return studentService.getStudentById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + studentId + " not found"));
    }

@Operation(summary = "Registrar un nuevo estudiante", description = "Crea un nuevo estudiante con nombre, apellido y correo electrónico")
@PreAuthorize("hasRole('ADMIN')")
@PostMapping
public ResponseEntity<?> createStudent(@RequestBody @Valid Student student, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
        // Construir el mapa de errores con los campos y los mensajes
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        // Retornar los errores en formato estructurado
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation error",
                errors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Verificar si el estudiante ya existe por correo electrónico
    if (studentService.existsByEmail(student.getEmail())) {
        throw new StudentAlreadyExistsException("Student with email " + student.getEmail() + " already exists");
    }

    // Guardar o actualizar el estudiante
    studentService.saveOrUpdate(student);

    return new ResponseEntity<>("Student created successfully", HttpStatus.CREATED);
}


   @Operation(summary = "Actualizar un estudiante", description = "Actualiza los datos de un estudiante existente por su ID")
   @PreAuthorize("hasRole('ADMIN')")
    // Actualizar estudiante
    @PutMapping("/{studentId}")
    public ResponseEntity<String> updateStudent(@PathVariable("studentId") Long studentId,
                                                @RequestBody @Valid Student student, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            throw new CustomValidationException(errors);
        }
        if (!studentService.exists(studentId)) {
            throw new StudentNotFoundException("Student with id " + studentId + " not found");
        }
        student.setStudentId(studentId);  // Asegurarse de que se actualice el id del estudiante
        studentService.saveOrUpdate(student);
        return new ResponseEntity<>("Student updated successfully", HttpStatus.OK);
    }


    @Operation(summary = "Eliminar un estudiante", description = "Elimina un estudiante usando su ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{studentId}")
    public String deleteStudent(@PathVariable("studentId") Long studentId) {
        if (!studentService.exists(studentId)) {
            throw new StudentNotFoundException("Student with id " + studentId + " not found");
        }
        studentService.delete(studentId);
        return "Estudiante eliminado con éxito"; // Mensaje de confirmación
    }
}
