package com.example.crudrapido.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.crudrapido.config.AuthResponse;
import com.example.crudrapido.config.LoginRequest;
import com.example.crudrapido.config.RegisterRequest;
import com.example.crudrapido.config.SyncState;
import com.example.crudrapido.entity.Role;
import com.example.crudrapido.entity.User;
import com.example.crudrapido.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final StudentSyncService studentSyncService;
        private final SyncState syncState;
        private final UserRepository userRepository;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticationManager;

        public AuthResponse login(LoginRequest request) {
                sincronizarUnaSolaVez();
                authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
                                                request.getPassword()));
                User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
                String token = jwtService.getToken(user);

                return AuthResponse.builder()
                                .token(token)
                                .build();
        }

        public AuthResponse register(RegisterRequest request) {
                sincronizarUnaSolaVez();
                User user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .firstname(request.getFirstname())
                                .lastname(request.getLastname())
                                .country(request.getCountry())
                                .role(Role.ADMIN)
                                .build();

                userRepository.save(user);

                return AuthResponse.builder()
                                .token(jwtService.getToken(user))
                                .build();
        }

        private void sincronizarUnaSolaVez() {
                if (!syncState.isSynced()) {
                        studentSyncService.sincronizarUsuariosExternos();
                        syncState.setSynced(true);
                }
        }

}
