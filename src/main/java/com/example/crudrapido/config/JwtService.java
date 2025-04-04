package com.example.crudrapido.config;

import java.util.Base64;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import java.security.Key;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class JwtService {
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public boolean isTokenValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String data = header + "." + payload;

            // Obtener bytes de la clave correctamente
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getEncoded(), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] signedBytes = hmac.doFinal(data.getBytes());
            String computedSignature = Base64.getUrlEncoder().withoutPadding().encodeToString(signedBytes);

            return signature.equals(computedSignature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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
