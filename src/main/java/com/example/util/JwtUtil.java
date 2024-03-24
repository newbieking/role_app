package com.example.util;

import com.example.entity.Role;
import com.example.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Strings;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtUtil {

    @Autowired
    JwtConfigProperties jwtConfigProperties;

    private static SecretKeySpec key;

    @PostConstruct
    public void initKey() throws IOException {
        Path filePath = Path.of(jwtConfigProperties.getSecretPath());
        byte[] bs64Encoded = Files.readAllBytes(filePath);
        byte[] keyEncoded = Base64.getDecoder().decode(bs64Encoded);
        key = new SecretKeySpec(keyEncoded, "HmacSHA256");
    }


    public static String buildJws(String sub, String userId, String roleName) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 5);
        return buildJws(sub, userId, roleName, calendar.getTime());
    }

    public static String buildJws(String sub, String userId, String roleName, Date exp) {
        Date now = new Date();
        return Jwts.builder()
                .issuer("NBK")
                .subject(sub)
                .audience().add("users of NBK's site").and()
                .expiration(exp)
                .notBefore(now)
                .issuedAt(now)
                .id(userId)
                .claim("user-id", userId)
                .claim("user-role", roleName)
                .signWith(key)
                .compact();
    }

    public static Jws<Claims> parseJws(String jws) {
        JwtParser parser = Jwts.parser()
                .requireIssuer("NBK")
                .verifyWith(key)
                .build();

        return parser.parseSignedClaims(jws);
    }

    public static User parseUserId(String jws) {
        Jws<Claims> claimsJws = parseJws(jws);
        Claims claims = claimsJws.getPayload();
        String userId = (String) claims.get("user-id");
        String role = (String) claims.get("user-role");
        if (userId == null || role == null) {
            throw new MissingClaimException(claimsJws.getHeader(),
                    claims,
                    "user-id || user-role",
                    null,
                    "user-id and user-role must not be null");
        }
        return User.builder()
                .id(Long.valueOf(userId))
                .role(Role.builder().name(role).build())
                .build();
    }


}
