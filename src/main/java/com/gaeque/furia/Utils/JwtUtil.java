package com.gaeque.furia.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final SecretKey SECRET_KEY;
    private static final long EXPIRATION_TIME = 86400000L;

    public JwtUtil() {
    }

    public static String generateToken(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 86400000L)).signWith(SECRET_KEY, SignatureAlgorithm.HS512).compact();
    }

    public String getUsernameFromToken(String token) {
        return ((Claims)Jwts.parserBuilder().setSigningKey(this.getSigningKey()).build().parseClaimsJws(token).getBody()).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(this.getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (IllegalArgumentException | JwtException var3) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        return SECRET_KEY;
    }

    static {
        SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}
