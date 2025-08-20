package com.likelion.cheongsanghoe.auth.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.cheongsanghoe.auth.api.dto.request.RoleSelectionRequestDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.AuthResponseDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.LoginResponseDto;
import com.likelion.cheongsanghoe.auth.security.JwtTokenProvider;
import com.likelion.cheongsanghoe.auth.domain.repository.UserRepository;
import com.likelion.cheongsanghoe.auth.domain.Role;
import com.likelion.cheongsanghoe.auth.domain.User;
import com.likelion.cheongsanghoe.member.domain.repository.MemberRepository;
import com.likelion.cheongsanghoe.member.domain.Member;
import com.likelion.cheongsanghoe.member.domain.MemberStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final TempAuthCodeStore tempAuthCodeStore; // 1회용 코드 저장소

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginResponseDto googleLogin(String code) {
        try {
            // 1. code로 id_token 받아오기
            String idToken = getGoogleIdToken(code); // 구글 교환

            // 2. id_token 파싱
            Map<String, Object> claims = parseIdToken(idToken);
            String email = (String) claims.get("email");
            String name = (String) claims.getOrDefault("name", "User");
            String picture = (String) claims.get("picture");

            // 기존 사용자 확인 또는 새 사용자 생성
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                // Member 정보 조회
                Optional<Member> memberOpt = memberRepository.findByUser(user);

                String token = jwtTokenProvider.createToken(user.getEmail(),
                        user.getRole() != null ? user.getRole().name() : null);

                return LoginResponseDto.builder()
                        .accessToken(token)
                        .isNewUser(false)
                        .hasRole(user.getRole() != null)
                        .user(AuthResponseDto.from(user, memberOpt.orElse(null)))
                        .build();
            } else {
                // 새 User 생성
                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .build();

                User savedUser = userRepository.save(newUser);
                // 새 Member 생성-그 구글에서 받은 기본 정보로 생성
                Member newMember = Member.builder()
                        .user(savedUser)
                        .nickname(name)
                        .status(MemberStatus.ACTIVE)
                        .profileImageUrl(picture)
                        .build();

                Member savedMember = memberRepository.save(newMember);

                String token = jwtTokenProvider.createToken(savedUser.getEmail(), null);

                return LoginResponseDto.builder()
                        .accessToken(token)
                        .isNewUser(true)
                        .hasRole(false)
                        .user(AuthResponseDto.from(savedUser, savedMember))
                        .build();
            }
            // 코드 추가
        } catch (Exception e) {
            log.error("OAuth login failed", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OAuth login failed: " + e.getMessage());
        }
    }

//    // 구글 토큰 엔드포인트에서 id_token 가져오기
    private String getGoogleIdToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = String.format(
                "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                googleClientId, googleClientSecret, code, googleRedirectUri
        );

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || responseBody.get("id_token") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No id_token in response");
        }

        return (String) responseBody.get("id_token");
    }

    // id_token의 payload 파싱
    private Map<String, Object> parseIdToken(String idToken) {
        try{
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("Invalid id_token");

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

            return objectMapper.readValue(payload, Map.class);
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse id_token");
        }
    }


    public AuthResponseDto selectRole(String token, RoleSelectionRequestDto requestDto) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.updateRole(Role.valueOf(requestDto.getRole()));
        User updatedUser = userRepository.save(user);

        // Member 정보도 함께 조회
        Optional<Member> member = memberRepository.findByUser(user);

        return AuthResponseDto.from(updatedUser, member.orElse(null));
    }

    public void logout(String token) {
        jwtTokenProvider.invalidateToken(token);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto getCurrentUser(String token) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));


        Optional<Member> member = memberRepository.findByUser(user);

        return AuthResponseDto.from(user, member.orElse(null));
    }

    private String getGoogleAccessToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        String body = String.format(
                "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                googleClientId, googleClientSecret, code, googleRedirectUri
        );

        // 로그 추가
        log.info("=== Google OAuth2 Token Request ===");
        log.info("client_id      = {}", googleClientId);
        log.info("client_secret  = {}", googleClientSecret.substring(0, 5) + "*****"); // 시크릿은 앞 일부만
        log.info("code           = {}", code);
        log.info("redirect_uri   = {}", googleRedirectUri);
        log.info("tokenUrl       = {}", tokenUrl);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        // 로그 추가
        log.info("=== Google OAuth2 Token Response ===");
        log.info("status         = {}", response.getStatusCode());
        log.info("body           = {}", response.getBody());

        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> getGoogleUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }
}
