package com.likelion.cheongsanghoe.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(@Value("${jwt.secret:myDefaultSecretKeyThatIsLongEnoughForHS256Algorithm}") String secretKey,
                            @Value("${jwt.validity:3600000}") long validityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
    }

    // 이메일을 기반으로 토큰 생성
    public String getEmailFromToken(String token) {
        try {
            // 0.12.x 버전에서는 parser() 대신 parserBuilder() 사용
            Claims claims = Jwts.parser()
                    .verifyWith(key)  // setSigningKey 대신 verifyWith 사용
                    .build()
                    .parseSignedClaims(token)  // parseClaimsJws 대신 parseSignedClaims 사용
                    .getPayload();  // getBody 대신 getPayload 사용
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // 역할을 기반으로 토큰에서 역할 추출
    public String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return (String) claims.get("role");
        } catch (Exception e) {
            return null;
        }
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰 생성 (이메일과 역할을 포함)
    public String createToken(String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(email)  // setSubject 대신 subject 사용
                .claim("role", role)
                .issuedAt(now)  // setIssuedAt 대신 issuedAt 사용
                .expiration(validity)  // setExpiration 대신 expiration 사용
                .signWith(key)
                .compact();
    }

    // Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        String role = getRoleFromToken(token);

        if (email == null) {
            return null;
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER"))
        );

        return new UsernamePasswordAuthenticationToken(email, token, authorities);
    }

    // 토큰에서 만료 시간 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}