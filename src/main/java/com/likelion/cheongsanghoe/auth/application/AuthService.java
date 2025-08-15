package com.likelion.cheongsanghoe.auth.application;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Value("55848478880-rnds6ucgu8aov43ua89hd599i6gv3k5h.apps.googleusercontent.com")
    private String googleClientId;

    @Value("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")
    private String googleClientSecret;

    @Value("http://localhost:8080/login/oauth2/code/google")
    private String googleRedirectUri;


    public LoginResponseDto googleLogin(String code) {

        String accessToken = getGoogleAccessToken(code);

        // 구글 사용자 정보 조회
        Map<String, Object> userInfo = getGoogleUserInfo(accessToken);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String profileImage = (String) userInfo.get("picture");

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
                    .profileImageUrl(profileImage)
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
