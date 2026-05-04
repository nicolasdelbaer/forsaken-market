package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.MissingEnvConfigurationException;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class JwtUtils {
    //15min -> 15*60*1000
    private static final long expiration = 900_000;

    public static String generateToken(PlayerSession player){
        String result;
        try {
            result = Jwts.builder().signWith(getSecretKey())
                    .subject(player.name())
                    .id(player.id().toString())
                    .claim("email", player.email())
                    .claim("roles", player.roles())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .compact();

        } catch (MissingEnvConfigurationException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static SecretKey getSecretKey() throws MissingEnvConfigurationException {
        String jwtSecret = System.getenv("JWT_SECRET");
        if(Objects.isNull(jwtSecret))
            throw new MissingEnvConfigurationException("missing Jwt scret conf");
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public static Claims getClaims(String token){
        try {
            return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
        } catch (MissingEnvConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Integer getId(Claims claims, String token){
        return Integer.parseInt(claims.getId());
    }

    public static String getUsername(Claims claims, String token){
        return claims.getSubject();
    }

    public static String getEmail(Claims claims, String token){
        return claims.get("email", String.class);
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRoles(Claims claims, String token){
        return (List<String>) claims.get("roles", List.class);
    }

    public static boolean isValid(Claims claims){
        Date now = new Date();
        return now.after(claims.getIssuedAt()) &&
                now.before(claims.getExpiration());

    }
}
