package com.likelion.cheongsanghoe.auth.api;

import com.likelion.cheongsanghoe.auth.application.AuthService;
import com.likelion.cheongsanghoe.auth.api.dto.request.RoleSelectionRequestDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.AuthResponseDto;
import com.likelion.cheongsanghoe.auth.api.dto.response.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login/oauth2")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 및 OAuth2 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Google OAuth2 콜백",
            description = "Google OAuth2 인증 후 콜백을 처리하고 로그인을 완료합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Google 로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 코드"),
            @ApiResponse(responseCode = "500", description = "OAuth2 처리 중 오류 발생")
    })
    @GetMapping("/code/google")
    public LoginResponseDto googleCallback(
            @Parameter(description = "Google OAuth2에서 전달받은 인증 코드",
                    required = true,
                    example = "4/0AX4XfWjrMqJZvHvCqJZvHvCqJZvHvCqJZvH")
            @RequestParam(name = "code") String code) {
        return authService.googleLogin(code);
    }

    @Operation(summary = "사용자 역할 선택",
            description = "로그인 후 사용자의 역할(멘토/멘티)을 선택합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "역할 선택 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
            @ApiResponse(responseCode = "403", description = "이미 역할이 설정된 사용자")
    })
    @PostMapping("/role")
    public ResponseEntity<AuthResponseDto> selectRole(
            @Parameter(description = "역할 선택 요청 데이터", required = true)
            @RequestBody RoleSelectionRequestDto requestDto,
            HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        AuthResponseDto response = authService.selectRole(token, requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃",
            description = "현재 로그인된 사용자를 로그아웃 처리합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = extractTokenFromHeader(request);
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "현재 사용자 정보 조회",
            description = "현재 로그인된 사용자의 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
    })
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

    @Operation(summary = "OAuth2 로그인 성공",
            description = "OAuth2 로그인 성공 시 호출되는 엔드포인트입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 상태 반환")
    })
    @GetMapping("/oauth2/success")
    public ResponseEntity<Map<String, String>> oauth2Success() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Google OAuth2 로그인 성공!");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "OAuth2 로그인 실패",
            description = "OAuth2 로그인 실패 시 호출되는 엔드포인트입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "로그인 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/oauth2/failure")
    public ResponseEntity<Map<String, String>> oauth2Failure(
            @Parameter(description = "OAuth2 에러 정보가 포함된 요청")
            HttpServletRequest request) {
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