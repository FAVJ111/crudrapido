package com.example.crudrapido.config;

import com.example.crudrapido.config.JwtService;
import com.example.crudrapido.service.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = jwtService.generateToken(loginRequest.getUsername());
        return ResponseEntity.ok(token);
    }
}
