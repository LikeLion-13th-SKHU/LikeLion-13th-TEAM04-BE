package com.likelion.cheongsanghoe.member.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequestDto {


    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9._-]+$", message = "닉네임은 한글, 영문, 숫자, 특수문자(._-)만 사용 가능합니다.")
    private String nickname;

    @Pattern(regexp = "^01[0-9]-?[0-9]{4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String phoneNumber;

    @Size(max = 100, message = "주소는 100자 이하로 입력해주세요.")
    private String address;

    @Size(max = 100, message = "상세주소는 100자 이하로 입력해주세요.")
    private String detailAddress;

    @Pattern(regexp = "^[0-9]{5}$", message = "우편번호는 5자리 숫자로 입력해주세요.")
    private String zipCode;

    @Size(max = 500, message = "자기소개는 500자 이하로 입력해주세요.")
    private String bio;

    // === Users 테이블에서 이동해올 필드들 ===
    @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
    private String name;

    @Pattern(regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$", message = "생년월일은 YYYY-MM-DD 형식으로 입력해주세요.")
    private String birthDate;

    @Pattern(regexp = "^(M|F|MALE|FEMALE)$", message = "성별은 M, F, MALE, FEMALE 중 하나를 입력해주세요.")
    private String gender;

    // === 포트폴리오 연관 필드들 ===
    @Size(max = 100, message = "직업군은 100자 이하로 입력해주세요.")
    private String jobCategory; // 개발자, 디자이너, 마케터 등

    @Size(max = 100, message = "희망 근무지역은 100자 이하로 입력해주세요.")
    private String preferredLocation;

    @Size(max = 500, message = "보유 스킬은 500자 이하로 입력해주세요.")
    private String skills; // JSON 또는 콤마 구분 형태

    @Min(value = 0, message = "경력 년수는 0년 이상이어야 합니다.")
    @Max(value = 50, message = "경력 년수는 50년 이하로 입력해주세요.")
    private Integer experienceYears;

    @Size(max = 255, message = "학력은 255자 이하로 입력해주세요.")
    private String education;

    @Min(value = 0, message = "희망 연봉은 0 이상이어야 합니다.")
    @Max(value = 999999, message = "희망 연봉은 999,999만원 이하로 입력해주세요.")
    private Integer salaryExpectation; // 만원 단위
}