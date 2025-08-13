package com.likelion.cheongsanghoe.member.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.cheongsanghoe.auth.domain.Role;
import com.likelion.cheongsanghoe.member.domain.Member;
import com.likelion.cheongsanghoe.member.domain.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberInfoResponseDto {

    private Long memberId;
    private Long userId;
    private String email;
    private String nickname;
    private String phoneNumber;
    private String address;
    private String detailAddress;
    private String zipCode;
    private String bio;
    private String profileImageUrl;
    private String role;
    private String roleDescription;
    private MemberStatus status;
    private String statusDescription;
    private Integer reportCount;

    // 🚨 JSON으로 변환될 때의 날짜/시간 형식을 지정하는 어노테이션
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static MemberInfoResponseDto of(Member member) {
        Role userRole = member.getUser().getRole();

        return MemberInfoResponseDto.builder()
                .memberId(member.getId())
                .userId(member.getUser().getId())
                .email(member.getUser().getEmail())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .address(member.getAddress())
                .detailAddress(member.getDetailAddress())
                .zipCode(member.getZipCode())
                .bio(member.getBio())
                .profileImageUrl(member.getProfileImageUrl())
                .role(userRole != null ? userRole.name() : null)
                .roleDescription(userRole != null ? userRole.getDescription() : null)
                .status(member.getStatus())
                .statusDescription(member.getStatus().getDescription())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    public static MemberInfoResponseDto ofSimple(Member member) {
        Role userRole = member.getUser().getRole();

        return MemberInfoResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .address(member.getAddress())
                .role(userRole != null ? userRole.name() : null)
                .roleDescription(userRole != null ? userRole.getDescription() : null)
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
