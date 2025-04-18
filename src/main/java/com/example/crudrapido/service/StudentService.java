package com.example.crudrapido.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crudrapido.dto.StudentBasicDTO;
import com.example.crudrapido.dto.StudentRequestDTO;
import com.example.crudrapido.entity.Activity;
import com.example.crudrapido.entity.Course;
import com.example.crudrapido.entity.Parametrizacion;
import com.example.crudrapido.entity.Student;
import com.example.crudrapido.exception.CustomValidationException;
import com.example.crudrapido.exception.EmailAlreadyExistsException;
import com.example.crudrapido.exception.InvalidEmailFormatException;
import com.example.crudrapido.exception.InvalidNameException;
import com.example.crudrapido.exception.StudentLimitExceededException;
import com.example.crudrapido.exception.StudentNotFoundException;
import com.example.crudrapido.repository.ActivityRepository;
import com.example.crudrapido.repository.CourseRepository;
import com.example.crudrapido.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    ParametrizacionService parametrizacionService;

    @Autowired
    private ActivityRepository activityRepository;

    private void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException(fieldName + " is required");
        }
    }

    @Transactional
    public List<Student> getAllStudentsWithRandomActivities() {
        List<Student> students = studentRepository.findAll();
        List<Activity> allActivities = activityRepository.findAll();

        for (Student student : students) {
            if (student.getActivities() == null || student.getActivities().isEmpty()) {
                Collections.shuffle(allActivities);
                Set<Activity> selected = allActivities.stream()
                        .limit(3)
                        .collect(Collectors.toSet());

                student.setActivities(selected);
                selected.forEach(a -> a.getStudents().add(student));
            }
        }

        studentRepository.saveAll(students);
        return students;
    }

    @Transactional
    public Student saveWithRandomActivities(Student student) {
        // Obtener todas las actividades desde la base de datos
        List<Activity> allActivities = activityRepository.findAll();

        // Barajar aleatoriamente
        Collections.shuffle(allActivities);

        // Escoger 2 o 3 actividades aleatorias
        int cantidad = Math.min(3, allActivities.size());
        Set<Activity> selectedActivities = new HashSet<>(allActivities.subList(0, cantidad));

        // Asignar las actividades al estudiante
        student.setActivities(selectedActivities);

        // Establecer la relación inversa (opcional si usas cascade)
        for (Activity activity : selectedActivities) {
            activity.getStudents().add(student);
        }

        return studentRepository.save(student);
    }

    public void updateStudent(Long studentId, StudentRequestDTO studentRequestDTO) {
        // Buscar el estudiante por su ID
        Optional<Student> existingStudent = studentRepository.findById(studentId);
        if (existingStudent.isEmpty()) {
            throw new StudentNotFoundException("Student with id " + studentId + " not found");
        }
    
        // Actualizar los datos del estudiante
        Student student = existingStudent.get();
        student.setFirstName(studentRequestDTO.getFirstName());
        student.setLastName(studentRequestDTO.getLastName());
        student.setEmail(studentRequestDTO.getEmail());
    
        // Actualizar el curso y las actividades si es necesario
        Optional<Course> course = courseRepository.findById(studentRequestDTO.getCourseId());
        course.ifPresent(student::setCourse);
    
        Set<Activity> activities = new HashSet<>(activityRepository.findAllById(studentRequestDTO.getActivityIds()));
        student.setActivities(activities);
    
        // Guardar los cambios
        saveOrUpdate(student);
    }
    

    public Optional<StudentBasicDTO> getStudentDTOById(Long id) {
        return studentRepository.findById(id).map(student -> {
            StudentBasicDTO dto = new StudentBasicDTO();
            dto.setId(student.getStudentId());
            dto.setFirstName(student.getFirstName());
            dto.setLastName(student.getLastName());
            dto.setEmail(student.getEmail());
            dto.setCourseName(student.getCourse() != null ? student.getCourse().getName() : null);
            return dto;
        });
    }

    // Método para obtener un estudiante por ID
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public void saveOrUpdate(Student student) {
        // Validación del nombre y apellido
        validateName(student.getFirstName(), "First name");
        validateName(student.getLastName(), "Last name");
    
        // Verificar si el correo electrónico ya está registrado
        if (existsByEmail(student.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + student.getEmail());
        }
    
        // Verificar si el estudiante es nuevo y si el límite de estudiantes ha sido alcanzado
        if (student.getStudentId() == null || !studentRepository.existsById(student.getStudentId())) {
            Optional<Parametrizacion> maxEstudiantesParam = parametrizacionService.getByClave("MAX_ESTUDIANTES");
    
            if (maxEstudiantesParam.isPresent()) {
                int maxEstudiantes = Integer.parseInt(maxEstudiantesParam.get().getValor());
    
                long totalEstudiantes = studentRepository.count();
                if (totalEstudiantes >= maxEstudiantes) {
                    throw new StudentLimitExceededException("No se pueden registrar más estudiantes, límite alcanzado.");
                }
            }
        }
    
        // Guardar o actualizar el estudiante
        studentRepository.save(student);
    }
    
    @Transactional
    public List<Student> getStudentsByCourseName(String courseName) {
        List<Student> estudiantes = studentRepository.findByCourse_NameIgnoreCase(courseName);

        // Inicializamos explícitamente las colecciones perezosas
        for (Student student : estudiantes) {
            Hibernate.initialize(student.getActivities()); // Esto inicializa la colección de activities
        }

        return estudiantes;
    }

    public void delete(Long id) {
        studentRepository.deleteById(id);
        ;
    }

    public boolean exists(Long id) {
        return studentRepository.existsById(id);
    }

    public boolean existsByEmail(String email) {
        return studentRepository.existsByEmail(email); // Necesitarás agregar este método en el repositorio
    }
}
