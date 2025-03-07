package org.dam16.tcgdropbox.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String extractUsername(String token) {
        return getClaims(token).getSubject(); // Extrae el usuario
    }

    public boolean validateToken(String token) {
        try {
            return getClaims(token) != null;
        } catch (Exception e) {
            return false; // Token inválido
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
    // Extraer un dato específico (por ejemplo, "sub" o "rol")
    public <T> T extraerDato(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getClaims(token));
    }

    // Extraer el rol del token
    public String extractRol(String token) {
        return extraerDato(token, claims -> claims.get(String.valueOf("admin"), String.class));
    }

}
