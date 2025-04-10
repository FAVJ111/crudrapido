package com.example.crudrapido.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crudrapido.entity.Parametrizacion;
import com.example.crudrapido.entity.Student;
import com.example.crudrapido.exception.CustomValidationException;
import com.example.crudrapido.exception.EmailAlreadyExistsException;
import com.example.crudrapido.exception.InvalidEmailFormatException;
import com.example.crudrapido.exception.InvalidNameException;
import com.example.crudrapido.exception.StudentLimitExceededException;
import com.example.crudrapido.repository.StudentRepository;

@Service
public class StudentService {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ParametrizacionService parametrizacionService;


    private void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidNameException(fieldName + " is required");
        }
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

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

        // Obtener el número máximo de estudiantes permitido desde la base de datos
        Optional<Parametrizacion> maxEstudiantesParam = parametrizacionService.getByClave("MAX_ESTUDIANTES");

        if (maxEstudiantesParam.isPresent()) {
            int maxEstudiantes = Integer.parseInt(maxEstudiantesParam.get().getValor());

            long totalEstudiantes = studentRepository.count();
            if (totalEstudiantes >= maxEstudiantes) {
                throw new StudentLimitExceededException("No se pueden registrar más estudiantes, límite alcanzado.");
            }
        }

        // Guardar el estudiante
        studentRepository.save(student);
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
