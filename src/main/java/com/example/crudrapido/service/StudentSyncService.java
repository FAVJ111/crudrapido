package com.example.crudrapido.service;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.crudrapido.dto.UsuarioApiResponse;
import com.example.crudrapido.entity.Course;
import com.example.crudrapido.entity.Student;
import com.example.crudrapido.repository.CourseRepository;
import com.example.crudrapido.repository.StudentRepository;

@Service
public class StudentSyncService {
    private final StudentRepository studentRepository;
    private final RestTemplate restTemplate;
    private final CourseRepository courseRepository;

    public StudentSyncService(StudentRepository studentRepository, RestTemplate restTemplate, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.restTemplate = restTemplate;
        this.courseRepository = courseRepository;
    }

    public void sincronizarUsuariosExternos() {
        String url = "https://jsonplaceholder.typicode.com/users";
        UsuarioApiResponse[] usuariosApi = restTemplate.getForObject(url, UsuarioApiResponse[].class);

        List<Course> cursosDisponibles = courseRepository.findAll();
        Random random = new Random();

        if (usuariosApi != null && !cursosDisponibles.isEmpty()) {
            for (UsuarioApiResponse usuario : usuariosApi) {
                if (!studentRepository.existsByEmail(usuario.getEmail())) {
                    String[] partes = usuario.getName().split(" ", 2);
                    String firstName = partes.length > 0 ? partes[0] : "";
                    String lastName = partes.length > 1 ? partes[1] : "";

                    Course cursoAleatorio = cursosDisponibles.get(random.nextInt(cursosDisponibles.size()));

                    Student student = new Student();
                    student.setFirstName(firstName);
                    student.setLastName(lastName);
                    student.setEmail(usuario.getEmail());
                    student.setCourse(cursoAleatorio); // asigna curso aleatorio

                    studentRepository.save(student);
                }
            }
        }
    }
}