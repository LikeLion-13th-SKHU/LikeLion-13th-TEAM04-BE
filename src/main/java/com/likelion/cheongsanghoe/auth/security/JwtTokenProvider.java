package com.likelion.cheongsanghoe.auth.security;

import com.likelion.cheongsanghoe.exception.InvalidTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    // 블랙리스트 저장소 - ConcurrentHashMap으로 성능 개선
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

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
            log.debug("토큰에서 이메일 추출 실패: {}", e.getMessage());
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
            log.debug("토큰에서 역할 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        // 먼저 블랙리스트 체크 - 예외 발생하지 않고 false 반환으로 변경
        if (isBlacklisted(token)) {
            log.debug("블랙리스트된 토큰입니다.");
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.debug("토큰 유효성 검사 실패: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.debug("토큰이 올바르지 않습니다: {}", e.getMessage());
            return false;
        }
    }

    public String createToken(String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String token = Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();

        log.debug("토큰 생성 완료 - 이메일: {}, 역할: {}", email, role);
        return token;
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
            log.debug("토큰 만료 여부 확인 실패: {}", e.getMessage());
            return true;
        }
    }

    public void addToBlacklist(String token) {
        if (token != null && !token.trim().isEmpty()) {
            blacklistedTokens.add(token);
            log.info("토큰이 블랙리스트에 추가되었습니다. 현재 블랙리스트 크기: {}", blacklistedTokens.size());
        }
    }

    public boolean isBlacklisted(String token) {
        return token != null && blacklistedTokens.contains(token);
    }

    public void invalidateToken(String token) {
        if (token != null && !token.trim().isEmpty()) {
            blacklistedTokens.add(token);
            log.info("토큰이 무효화되었습니다: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
        }
    }


    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }


    public void cleanupExpiredTokensFromBlacklist() {
        List<String> expiredTokens = new ArrayList<>();

        for (String token : blacklistedTokens) {
            if (isTokenExpired(token)) {
                expiredTokens.add(token);
            }
        }

        for (String expiredToken : expiredTokens) {
            blacklistedTokens.remove(expiredToken);
        }

        if (!expiredTokens.isEmpty()) {
            log.info("블랙리스트에서 만료된 토큰 {} 개 제거", expiredTokens.size());
        }
    }
}