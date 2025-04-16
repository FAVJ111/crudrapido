package com.example.crudrapido.controller;

import com.example.crudrapido.dto.StudentRequestDTO;
import com.example.crudrapido.entity.Activity;
import com.example.crudrapido.entity.Course;
import com.example.crudrapido.entity.Student;
import com.example.crudrapido.repository.ActivityRepository;
import com.example.crudrapido.repository.CourseRepository;
import com.example.crudrapido.repository.StudentRepository;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/actividades")
public class ActivityController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Operation(summary = "Registrar un nuevo estudiante", description = "Crea un nuevo estudiante con nombre, apellido y correo electrónico")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/registrar-estudiante")
    public ResponseEntity<?> createStudent(@RequestBody StudentRequestDTO dto) {
        // Validar que exista el curso
        Optional<Course> optionalCourse = courseRepository.findById(dto.getCourseId());
        if (optionalCourse.isEmpty()) {
            return ResponseEntity.badRequest().body("Course not found with ID: " + dto.getCourseId());
        }

        // Crear estudiante
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setCourse(optionalCourse.get());

        // Asociar actividades si existen
        Set<Activity> activities = new HashSet<>();
        if (dto.getActivityIds() != null && !dto.getActivityIds().isEmpty()) {
            activities = new HashSet<>(activityRepository.findAllById(dto.getActivityIds()));
        }
        student.setActivities(activities);

        // Guardar
        Student savedStudent = studentRepository.save(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }
}
