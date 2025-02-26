package org.example.login_register.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "Xb6bQY2E3Cp1d9+zB+k7ZdFkHFfRyNhTeWxFb92Pj88=";
    private static final Key SECRET_KEY = new SecretKeySpec(Base64.getDecoder().decode(SECRET), "HmacSHA256");
    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 saat geçerli
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    public static String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public static boolean isTokenValid(String token) {
        return extractEmail(token) != null && !isTokenExpired(token);
    }

    private static boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
