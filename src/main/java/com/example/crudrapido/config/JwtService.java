package com.example.crudrapido.config;

import java.util.Base64;
import java.util.Date;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import java.security.Key;

@Service
public class JwtService {
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        // Dividimos el token en sus partes: header.payload.signature
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Token JWT inválido");
        }
        // Decodificamos el payload (la segunda parte)
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        JSONObject jsonObject = new JSONObject(payloadJson);
        return jsonObject.getString("sub");
    }
}
