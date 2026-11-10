package tarefas02.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getKeyBySecret() {
        return Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + this.expiration))
                .signWith(getKeyBySecret())
                .compact();
    }

    public boolean isValidToken(String token) {
        Claims claims = getClaims(token);
        if (Objects.nonNull(claims)) {
            String username = claims.getSubject();
            Date expirationDate = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());
            return Objects.nonNull(username) && Objects.nonNull(expirationDate) && now.before(expirationDate);
        }
        return false;
    }

    public String getUsername(String token) {
        Claims claims = getClaims(token);
        if (Objects.nonNull(claims)) {
            return claims.getSubject();
        }
        return null;
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKeyBySecret())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
