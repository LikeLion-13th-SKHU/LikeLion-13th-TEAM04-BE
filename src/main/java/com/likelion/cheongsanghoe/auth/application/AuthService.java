package com.likelion.cheongsanghoe.auth.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.cheongsanghoe.auth.api.dto.request.RoleSelectionRequestDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.AuthResponseDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.LoginResponseDto;
import com.likelion.cheongsanghoe.auth.domain.Role;
import com.likelion.cheongsanghoe.auth.domain.User;
import com.likelion.cheongsanghoe.auth.domain.repository.UserRepository;
import com.likelion.cheongsanghoe.auth.security.JwtTokenProvider;
import com.likelion.cheongsanghoe.exception.InvalidTokenException;
import com.likelion.cheongsanghoe.exception.MemberNotFoundException;
import com.likelion.cheongsanghoe.member.domain.Member;
import com.likelion.cheongsanghoe.member.domain.MemberStatus;
import com.likelion.cheongsanghoe.member.domain.repository.MemberRepository;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    /**
     * 구글 로그인
     */
    public LoginResponseDto googleLogin(String code) {
        try {
            String idToken = getGoogleIdToken(code);

            Map<String, Object> claims = parseIdToken(idToken);
            String email = (String) claims.get("email");
            String name = (String) claims.getOrDefault("name", "User");
            String picture = (String) claims.get("picture");

            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                User user = existingUser.get();

                if (!userRepository.existsById(user.getId())) {
                    throw new MemberNotFoundException("이미 탈퇴한 계정입니다: " + email);
                }

                Optional<Member> memberOpt = memberRepository.findByUser(user);

                String token = jwtTokenProvider.createToken(
                        user.getEmail(),
                        user.getRole() != null ? user.getRole().name() : null
                );

                return LoginResponseDto.builder()
                        .accessToken(token)
                        .isNewUser(false)
                        .hasRole(user.getRole() != null)
                        .user(AuthResponseDto.from(user, memberOpt.orElse(null)))
                        .build();
            } else {
                // 신규 회원 생성
                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .build();

                User savedUser = userRepository.save(newUser);

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
        } catch (Exception e) {
            log.error("OAuth login failed", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OAuth login failed: " + e.getMessage());
        }
    }


    @Transactional
    public void deleteUser(String token) {
        // 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidTokenException("유효하지 않은 토큰으로 회원 탈퇴를 시도했습니다.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // Member 삭제
        memberRepository.findByUser(user).ifPresent(memberRepository::delete);

        // User 삭제 (DB 물리적 삭제)
        userRepository.delete(user);

        // 사용 중인 토큰 블랙리스트 등록
        jwtTokenProvider.invalidateToken(token);

        log.info("User [{}] and related Member deleted successfully", email);
    }


    public AuthResponseDto selectRole(String token, RoleSelectionRequestDto requestDto) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다: " + email));

        user.updateRole(Role.valueOf(requestDto.getRole()));
        User updatedUser = userRepository.save(user);

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
                .orElseThrow(() -> new MemberNotFoundException("사용자를 찾을 수 없습니다: " + email));

        Optional<Member> member = memberRepository.findByUser(user);

        return AuthResponseDto.from(user, member.orElse(null));
    }


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

    private Map<String, Object> parseIdToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("Invalid id_token");

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readValue(payload, Map.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse id_token");
        }
    }

    private String getGoogleAccessToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        String body = String.format(
                "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
                googleClientId, googleClientSecret, code, googleRedirectUri
        );

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

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
