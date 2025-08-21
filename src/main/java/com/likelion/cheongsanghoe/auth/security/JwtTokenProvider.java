package com.likelion.cheongsanghoe.auth.security;

import com.likelion.cheongsanghoe.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    // 블랙리스트 저장소 (간단히 메모리 기반, 추후 Redis 등으로 교체 가능)
    private final List<String> blacklistedTokens = Collections.synchronizedList(new ArrayList<>());

    public JwtTokenProvider(
            @Value("${jwt.secret:myDefaultSecretKeyThatIsLongEnoughForHS256Algorithm}") String secretKey,
            @Value("${jwt.validity:3600000}") long validityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

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

    public boolean validateToken(String token) {
        if (isBlacklisted(token)) {
            throw new InvalidTokenException("이미 만료되었거나 무효화된 토큰입니다.");
        }

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }

    public String createToken(String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        String role = getRoleFromToken(token);

        if (email == null) {
            throw new InvalidTokenException("토큰에서 이메일 정보를 가져올 수 없습니다.");
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER"))
        );

        return new UsernamePasswordAuthenticationToken(email, token, authorities);
    }

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

    public void addToBlacklist(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public void invalidateToken(String token) {
        blacklistedTokens.add(token);
    }
}
