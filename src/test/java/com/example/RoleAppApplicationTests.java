package com.example;

import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
class RoleAppApplicationTests {
    @Value("${jwt.secretPath}")
    String path;

    @Autowired
    UserRepository userRepository;

    //    @Test
    void genSecret() throws IOException {
        SecretKey key = Jwts.SIG.HS256.key().build();

        byte[] keyEncoded = key.getEncoded();

        byte[] bs64Encoded = Base64.getEncoder().encode(keyEncoded);
        Path filePath = Path.of(path);
        Path parentPath = Files.createDirectories(filePath.getParent());
        Path writedPath = Files.write(filePath, bs64Encoded, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        System.out.println(parentPath);
        System.out.println(writedPath);
    }


    //    @Test
    void readSecret() throws IOException {
        Path filePath = Path.of(path);
        byte[] bs64Encoded = Files.readAllBytes(filePath);
        byte[] keyEncoded = Base64.getDecoder().decode(bs64Encoded);
        SecretKeySpec key = new SecretKeySpec(keyEncoded, "HmacSHA256");
        System.out.println(key.getAlgorithm());
    }

    //    @Test
    void testJwt() {
        String userIdOrigin = "199";
        String userToken = JwtUtil.buildJws("user token", userIdOrigin, Role.user().getName());
        User user;
        try {
            user = JwtUtil.parseUserId(userToken);
            System.out.println(user);
            Optional<User> userOptional = userRepository.findById(user.getId());
            userOptional.map(User::toString)
                    .ifPresentOrElse(System.out::println,
                            () -> System.out.println("user does not exist"));
        } catch (JwtException | IllegalArgumentException exception) {
            System.out.println("reject token: " + userToken);
            exception.printStackTrace();
        }

    }

    //    @Test
    String genFakeToken(String userToken) throws JsonProcessingException {
        String[] split = userToken.split("\\.");
        String payloadEncoded = split[1];
        byte[] decode = Base64.getDecoder().decode(payloadEncoded.getBytes());
        String payload = new String(decode);
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(payload, Map.class);
        map.put("user-role", "admin");
        byte[] bytes = objectMapper.writeValueAsBytes(map);
        byte[] encode = Base64.getEncoder().encode(bytes);
        String token = split[0] + "." + new String(encode).replace("=", "") + "." + split[2];
        System.out.println(token);
        System.out.println(userToken);
        return token;
    }

    @Test
    void testFakeJwt() throws JsonProcessingException {
        String userIdOrigin = "199";
        String userToken = JwtUtil.buildJws("user token", userIdOrigin, Role.user().getName());

        // hack the token
        String fakeToken = genFakeToken(userToken);

        User user;
        try {
            user = JwtUtil.parseUserId(fakeToken);
            System.out.println(user);
            Optional<User> userOptional = userRepository.findById(user.getId());
            userOptional.map(User::toString)
                    .ifPresentOrElse(System.out::println,
                            () -> System.out.println("user does not exist"));
        } catch (JwtException | IllegalArgumentException exception) {
            System.out.println("reject token: " + fakeToken);
            exception.printStackTrace();
        }

    }


}
