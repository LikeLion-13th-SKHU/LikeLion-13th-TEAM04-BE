package com.likelion.cheongsanghoe.auth.api.dto.response;

import com.likelion.cheongsanghoe.auth.domain.Role;
import com.likelion.cheongsanghoe.auth.domain.User;
import com.likelion.cheongsanghoe.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private Role role;

    // Member 관련 정보들
    private Long memberId;
    private String phoneNumber;
    private String address;
    private String bio;
    private String jobCategory;

    // User와 Member를 함께 받는 메서드
    public static AuthResponseDto from(User user, Member member) {
        AuthResponseDtoBuilder builder = AuthResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole());

        // Member 정보가 있을 경우 추가
        if (member != null) {
            builder.memberId(member.getId())
                    .nickname(member.getNickname())
                    .profileImageUrl(member.getProfileImageUrl())
                    .phoneNumber(member.getPhoneNumber())
                    .address(member.getAddress())
                    .bio(member.getBio())
                    .jobCategory(member.getJobCategory());
        }

        return builder.build();
    }

    // 기존 User만 받는 메서드
    @Deprecated
    public static AuthResponseDto from(User user) {
        return AuthResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}