package com.likelion.cheongsanghoe.auth.api;

import com.likelion.cheongsanghoe.auth.application.AuthService;
import com.likelion.cheongsanghoe.auth.api.dto.request.RoleSelectionRequestDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.AuthResponseDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login/oauth2")  // 경로를 첫 번째 예시와 동일하게 수정
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Google OAuth2 콜백 처리 (GET 방식으로 변경, 경로도 수정)
    @GetMapping("/code/google")
    public LoginResponseDto googleCallback(@RequestParam(name = "code") String code) {
        return authService.googleLogin(code);
    }

    @PostMapping("/role")
    public ResponseEntity<AuthResponseDto> selectRole(
            @RequestBody RoleSelectionRequestDto requestDto,
            HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        AuthResponseDto response = authService.selectRole(token, requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")  // 경로 수정
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractTokenFromHeader(request);
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/me")
    public ResponseEntity<AuthResponseDto> getCurrentUser(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        AuthResponseDto response = authService.getCurrentUser(token);
        return ResponseEntity.ok(response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<Map<String, String>> oauth2Success() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Google OAuth2 로그인 성공!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<Map<String, String>> oauth2Failure(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "failure");
        response.put("message", "Google OAuth2 로그인 실패");

        String error = request.getParameter("error");
        if (error != null) {
            response.put("error", error);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}