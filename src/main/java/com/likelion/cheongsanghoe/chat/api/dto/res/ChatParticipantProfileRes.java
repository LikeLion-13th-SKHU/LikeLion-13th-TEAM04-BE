package com.likelion.cheongsanghoe.chat.api.dto.res;

public record ChatParticipantProfileRes(
        Long userId,
        String name,
        String profileImage
) {
}
