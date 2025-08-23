package com.likelion.cheongsanghoe.member.api;

import com.likelion.cheongsanghoe.member.api.dto.request.MemberUpdateRequestDto;
import com.likelion.cheongsanghoe.member.api.dto.response.MemberInfoResponseDto;
import com.likelion.cheongsanghoe.member.application.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "회원 관리 API", description = "마이페이지 및 회원 정보 관련 CRUD API")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/profile")
    @Operation(summary = "프로필 생성", description = "로그인한 사용자의 프로필을 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 생성 성공",
                    content = @Content(schema = @Schema(implementation = MemberInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "409", description = "이미 프로필이 존재함")
    })
    public ResponseEntity<MemberInfoResponseDto> createProfile(
            @Parameter(description = "프로필 생성 요청 데이터", required = true)
            @Valid @RequestBody MemberUpdateRequestDto requestDto,
            Authentication authentication) {

        log.info("Creating profile for user: {}", authentication.getName());

        String email = authentication.getName();
        MemberInfoResponseDto response = memberService.createMemberProfileByEmail(email, requestDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = MemberInfoResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<MemberInfoResponseDto> getMyInfo(Authentication authentication) {
        log.info("Getting my info for user: {}", authentication.getName());

        String email = authentication.getName();
        MemberInfoResponseDto response = memberService.getMemberInfoByEmail(email);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = MemberInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<MemberInfoResponseDto> updateMyInfo(
            @Parameter(description = "회원 정보 수정 요청 데이터", required = true)
            @Valid @RequestBody MemberUpdateRequestDto requestDto,
            Authentication authentication) {
        log.info("Updating my info for user: {}", authentication.getName());
        String email = authentication.getName();
        MemberInfoResponseDto response = memberService.updateMemberByEmail(email, requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/profile-image")
    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공",
                    content = @Content(schema = @Schema(implementation = MemberInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "파일 업로드 실패"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public ResponseEntity<MemberInfoResponseDto> uploadProfileImage(
            @Parameter(description = "업로드할 프로필 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        log.info("Uploading profile image for user: {}", authentication.getName());

        try {
            Long userId = Long.valueOf(authentication.getName());

            String imageUrl = "/images/profile/default.jpg"; // 걍 임시로 넣은거

            MemberInfoResponseDto response = memberService.updateProfileImage(userId, imageUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error uploading profile image", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "탈퇴 처리 중 오류 발생")
    })
    public ResponseEntity<Map<String, String>> withdrawMember(Authentication authentication) {
        log.info("Withdrawing member for user: {}", authentication.getName());

        try {
            String email = authentication.getName();
            memberService.withdrawMemberByEmail(email);

            Map<String, String> response = new HashMap<>();
            response.put("message", "회원 탈퇴가 완료되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to withdraw member: {}, Error: {}", authentication.getName(), e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "회원 탈퇴 처리 중 오류가 발생했습니다.");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/check/nickname")
    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임의 사용 가능 여부를 확인합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 확인 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 닉네임 형식")
    })
    public ResponseEntity<Map<String, Object>> checkNickname(
            @Parameter(description = "확인할 닉네임", required = true, example = "홍길동")
            @RequestParam String nickname) {
        log.info("Checking nickname availability: {}", nickname);

        boolean isAvailable = memberService.isNicknameAvailable(nickname);

        Map<String, Object> response = new HashMap<>();
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "사용 가능한 닉네임입니다." : "이미 사용중인 닉네임입니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/phone")
    @Operation(summary = "전화번호 중복 확인", description = "입력한 전화번호의 사용 가능 여부를 확인합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 확인 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 전화번호 형식")
    })
    public ResponseEntity<Map<String, Object>> checkPhoneNumber(
            @Parameter(description = "확인할 전화번호", required = true, example = "010-1234-5678")
            @RequestParam String phoneNumber) {
        log.info("Checking phone number availability: {}", phoneNumber);

        boolean isAvailable = memberService.isPhoneNumberAvailable(phoneNumber);

        Map<String, Object> response = new HashMap<>();
        response.put("available", isAvailable);
        response.put("message", isAvailable ? "사용 가능한 전화번호입니다." : "이미 등록된 전화번호입니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "회원 검색", description = "닉네임으로 회원을 검색합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 검색 조건")
    })
    public ResponseEntity<Page<MemberInfoResponseDto>> searchMembers(
            @Parameter(description = "검색 키워드", required = true, example = "홍길동")
            @RequestParam String keyword,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "정렬 방향", example = "desc", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("Searching members with keyword: {}", keyword);

        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<MemberInfoResponseDto> response = memberService.searchMembersByKeyword(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-address")
    @Operation(summary = "지역별 회원 조회", description = "특정 지역의 회원들을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 지역 정보")
    })
    public ResponseEntity<Page<MemberInfoResponseDto>> getMembersByAddress(
            @Parameter(description = "검색할 주소", required = true, example = "서울특별시")
            @RequestParam String address,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "정렬 방향", example = "desc", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("Getting members by address: {}", address);

        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<MemberInfoResponseDto> response = memberService.getMembersByAddress(address, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-role")
    @Operation(summary = "역할별 회원 조회", description = "특정 역할(상인/청년)의 회원들을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 역할 정보")
    })
    public ResponseEntity<Page<MemberInfoResponseDto>> getMembersByRole(
            @Parameter(description = "회원 역할", required = true, example = "MERCHANT",
                    schema = @Schema(allowableValues = {"MERCHANT", "YOUTH"}))
            @RequestParam String role,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "정렬 기준", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "정렬 방향", example = "desc", schema = @Schema(allowableValues = {"asc", "desc"}))
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("Getting members by role: {}", role);

        try {
            com.likelion.cheongsanghoe.auth.domain.Role roleEnum =
                    com.likelion.cheongsanghoe.auth.domain.Role.valueOf(role.toUpperCase());

            Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
            Pageable pageable = PageRequest.of(page, size, sortObj);

            Page<MemberInfoResponseDto> response = memberService.getMembersByRole(roleEnum, pageable);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid role parameter: {}", role);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/count-with-role")
    @Operation(summary = "역할이 부여된 청년 수 조회", description = "Role이 있는 활성화된 회원 수를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<Map<String, Object>> countActiveMembersWithRole() {
        long count = memberService.countActiveMembersWithRole();
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}