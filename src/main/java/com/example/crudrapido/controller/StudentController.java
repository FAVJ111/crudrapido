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
import com.example.crudrapido.exception.StudentNotFoundException;
import com.example.crudrapido.repository.ActivityRepository;
import com.example.crudrapido.repository.CourseRepository;
import com.example.crudrapido.repository.StudentRepository;
import com.example.crudrapido.exception.CustomValidationException;
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
    public Student getById(@PathVariable("studentId") Long studentId) {
        return studentService.getStudentById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + studentId + " not found"));
    }

    @Operation(summary = "Registrar un nuevo estudiante", description = "Crea un nuevo estudiante con nombre, apellido y correo electrónico")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
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
    
        // Buscar las actividades por ID y asociarlas
        Set<Activity> activities = new HashSet<>();
        if (dto.getActivityIds() != null && !dto.getActivityIds().isEmpty()) {
            activities = new HashSet<>(activityRepository.findAllById(dto.getActivityIds()));
        }
        student.setActivities(activities);
    
        // Guardar el estudiante
        Student savedStudent = studentRepository.save(student);
        return ResponseEntity.ok(savedStudent);
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
        student.setStudentId(studentId); // Asegurarse de que se actualice el id del estudiante
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
