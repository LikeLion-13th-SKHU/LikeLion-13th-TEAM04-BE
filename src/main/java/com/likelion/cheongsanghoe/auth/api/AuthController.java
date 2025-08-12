package com.likelion.cheongsanghoe.auth.api;

import com.likelion.cheongsanghoe.auth.application.AuthService;
import com.likelion.cheongsanghoe.auth.api.dto.request.RoleSelectionRequestDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.AuthResponseDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google/callback")
    public ResponseEntity<LoginResponseDto> googleLogin(@RequestParam String code) {
        LoginResponseDto response = authService.googleLogin(code);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/role")
    public ResponseEntity<AuthResponseDto> selectRole(
            @RequestBody RoleSelectionRequestDto requestDto,
            HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        AuthResponseDto response = authService.selectRole(token, requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractTokenFromHeader(request);
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
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
}