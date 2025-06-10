package com.example.auth.security;

import com.example.auth.domain.Role;
import com.example.auth.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2시간
    private Key secretKey;

    @PostConstruct
    public void init() {
        // 비밀키 생성 (랜덤 키 혹은 고정 키로 변경 가능)
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // 토큰 생성
    public String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("roles", user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toList()));
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // 토큰에서 사용자 이름 가져오기
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰에서 역할 리스트 가져오기
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return (List<String>) getClaims(token).get("roles");
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
