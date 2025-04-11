package com.example.crudrapido.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.crudrapido.entity.Role;
import com.example.crudrapido.entity.Student;
import com.example.crudrapido.entity.User;
import com.example.crudrapido.repository.StudentRepository;
import com.example.crudrapido.repository.UserRepository;
import com.example.crudrapido.config.LoginRequest;
import com.example.crudrapido.dto.UsuarioApiResponse;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public AuthResponse login(LoginRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user);

        guardarUsuariosExternosEnBD();


        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .country(request.getCountry())
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);

        guardarUsuariosExternosEnBD();

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }

    private void guardarUsuariosExternosEnBD() {
        String url = "https://jsonplaceholder.typicode.com/users";
        UsuarioApiResponse[] usuariosApi = restTemplate.getForObject(url, UsuarioApiResponse[].class);
    
        if (usuariosApi != null) {
            for (UsuarioApiResponse usuario : usuariosApi) {
                // Evitar duplicados por email
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
