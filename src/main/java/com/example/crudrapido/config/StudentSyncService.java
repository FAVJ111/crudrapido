package com.example.crudrapido.config;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.crudrapido.dto.UsuarioApiResponse;
import com.example.crudrapido.entity.Student;
import com.example.crudrapido.repository.StudentRepository;

@Service
public class StudentSyncService {
    private final StudentRepository studentRepository;
    private final RestTemplate restTemplate;

    public StudentSyncService(StudentRepository studentRepository, RestTemplate restTemplate) {
        this.studentRepository = studentRepository;
        this.restTemplate = restTemplate;
    }

    public void sincronizarUsuariosExternos() {
        String url = "https://jsonplaceholder.typicode.com/users";
        UsuarioApiResponse[] usuariosApi = restTemplate.getForObject(url, UsuarioApiResponse[].class);

        if (usuariosApi != null) {
            for (UsuarioApiResponse usuario : usuariosApi) {
                if (!studentRepository.existsByEmail(usuario.getEmail())) {
                    String[] partes = usuario.getName().split(" ", 2);
                    String firstName = partes.length > 0 ? partes[0] : "";
                    String lastName = partes.length > 1 ? partes[1] : "";

                    Student student = new Student();
                    student.setFirstName(firstName);
                    student.setLastName(lastName);
                    student.setEmail(usuario.getEmail());

                    studentRepository.save(student);
                }
            }
        }
    }
}
