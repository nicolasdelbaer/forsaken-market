package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class JwtUtils {
    //15min -> 15*60*1000
    private static final long expiration = 900_000;

    public static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(System.getenv("JWT_SECRET").getBytes());
    }

    public static String generateToken(PlayerSession player){
        return Jwts.builder().signWith(getSecretKey())
                .subject(player.name())
                .id(player.id().toString())
                .claim("email", player.email())
                .claim("roles", List.of(""))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
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


    public static Claims getClaims(String token){
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }

    public static boolean isValid(Claims claims){
        Date now = new Date();
        return now.after(claims.getIssuedAt()) &&
                now.before(claims.getExpiration());

    }
}
