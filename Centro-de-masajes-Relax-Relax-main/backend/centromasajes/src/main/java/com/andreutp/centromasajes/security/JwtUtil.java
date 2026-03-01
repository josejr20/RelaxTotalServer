package com.andreutp.centromasajes.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.andreutp.centromasajes.model.UserModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    // Nota: al multiplicar literales enteros el resultado también es int,
    // por eso forzamos uno de los operandos a long para evitar warnings y
    // posibles overflows.
    private static final long EXPIRATION_TIME = 1000L * 60 * 60; // 1 hora

    private final Key key;

    public JwtUtil(@org.springframework.beans.factory.annotation.Value("${jwt.secret}") String secretKey) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("JWT secret cannot be empty");
        }
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Constructor sin argumentos para facilitar pruebas unitarias que instancien
    // la clase directamente. Usa una clave por defecto de 32 bytes válida
    // para HS256. No debe usarse en producción; Spring inyecta la propiedad.
    public JwtUtil() {
        // Default secret for testing only - use properly configured jwt.secret in production via application.properties
        String testSecret = "01234567890123456789012345678901"; // 32 chars
        this.key = Keys.hmacShaKeyFor(testSecret.getBytes());
    }

    // Genera el token incluyendo userId y role
    public String generateToken(UserModel user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().getName());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Helper para extraer todos los claims
    private Claims extractAllClaims(String token) {
         return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Metodo  para extraer un claim especifico
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae el username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrae el userId
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
     }

     // Extrae el role
     public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
     }

     // Verifica si el token ha expirado
     private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
     }

    // Valida el token
    public boolean isTokenValid(String token) {
        try {
            final String username = extractUsername(token);
            // Verifica que el username exista y que el token no haya expirado
            return (username != null && !isTokenExpired(token));
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
}