package minha_midia_fisica.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 31536000000L))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256(secretKey));
    }

    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(com.auth0.jwt.algorithms.Algorithm.HMAC256(secretKey))
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getExpiresAt().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return JWT.decode(token).getSubject();
    }
}