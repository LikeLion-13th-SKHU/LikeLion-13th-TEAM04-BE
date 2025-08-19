package com.likelion.cheongsanghoe.auth.api;

import com.likelion.cheongsanghoe.auth.application.AuthService;
import com.likelion.cheongsanghoe.auth.api.dto.request.RoleSelectionRequestDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.AuthResponseDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login/oauth2")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "소셜 로그인 및 사용자 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/code/google")
    @Operation(summary = "Google OAuth2 콜백", description = "Google OAuth2 인증 후 콜백을 처리하여 로그인을 완료합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 코드"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<LoginResponseDto> googleCallback(
            @Parameter(description = "Google OAuth2 인증 코드", required = true)
            @RequestParam(name = "code") String code,
            HttpServletResponse response) throws IOException {
        try {
            // 1. 백엔드에서 code -> JWT 발급
            LoginResponseDto login = authService.googleLogin(code);

            return ResponseEntity.ok(login);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Google OAuth2 로그인 실패: " + e.getMessage());
        }
    }

    @PostMapping("/role")
    @Operation(summary = "사용자 역할 선택", description = "로그인한 사용자의 역할(구직자/구인자)을 선택합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "역할 선택 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<AuthResponseDto> selectRole(
            @Parameter(description = "역할 선택 요청 정보", required = true)
            @RequestBody RoleSelectionRequestDto requestDto,
            HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        AuthResponseDto response = authService.selectRole(token, requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃시킵니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractTokenFromHeader(request);
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/me")
    @Operation(summary = "현재 사용자 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
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
    @Operation(summary = "OAuth2 로그인 성공", description = "OAuth2 로그인 성공 시 호출되는 엔드포인트")
    @ApiResponse(responseCode = "200", description = "로그인 성공 응답")
    public ResponseEntity<Map<String, String>> oauth2Success() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Google OAuth2 로그인 성공!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oauth2/failure")
    @Operation(summary = "OAuth2 로그인 실패", description = "OAuth2 로그인 실패 시 호출되는 엔드포인트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "로그인 실패 응답"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
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