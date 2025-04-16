package com.example.crudrapido.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.http.MediaType;

import com.example.crudrapido.dto.StudentRequestDTO;
import com.example.crudrapido.entity.Activity;
import com.example.crudrapido.entity.Course;
import com.example.crudrapido.entity.Student;
import com.example.crudrapido.exception.StudentAlreadyExistsException;
import com.example.crudrapido.exception.StudentLimitExceededException;
import com.example.crudrapido.exception.StudentNotFoundException;
import com.example.crudrapido.repository.ActivityRepository;
import com.example.crudrapido.repository.CourseRepository;
import com.example.crudrapido.repository.StudentRepository;
import com.example.crudrapido.exception.CustomValidationException;
import com.example.crudrapido.exception.EmailAlreadyExistsException;
import com.example.crudrapido.exception.ErrorResponse;
import com.example.crudrapido.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Tag(name = "Estudiantes", description = "API para la gestión de estudiantes")
@RestController
@RequestMapping(path = "api/v1/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ActivityRepository activityRepository;

    // Obtener todos los estudiantes con sus actividades
    @Operation(summary = "Obtener todos los estudiantes con actividades")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudentsWithRandomActivities();
        return ResponseEntity.ok(students);
    }

    @Operation(summary = "Obtener un estudiante por ID", description = "Busca un estudiante usando su ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{studentId}")
    public ResponseEntity<?> getById(@PathVariable("studentId") Long studentId) {
        return studentService.getStudentDTOById(studentId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + studentId + " not found"));
    }

    // Método para actualizar un estudiante
    @Operation(summary = "Actualizar un estudiante", description = "Actualiza los datos de un estudiante existente por su ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{studentId}")
    public ResponseEntity<String> updateStudent(@PathVariable("studentId") Long studentId,
            @RequestBody @Valid StudentRequestDTO studentRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            throw new CustomValidationException(errors);
        }
    
        // Llamar al servicio para actualizar el estudiante
        studentService.updateStudent(studentId, studentRequestDTO);
    
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

    @Operation(summary = "Obtener estudiantes por curso", description = "Lista los estudiantes inscritos en un curso específico")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/curso/{nombreCurso}")
    @Transactional
    public ResponseEntity<List<Student>> getStudentsByCourse(@PathVariable String nombreCurso) {
        List<Student> estudiantes = studentService.getStudentsByCourseName(nombreCurso);

        if (estudiantes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(estudiantes, HttpStatus.OK);
    }
}
