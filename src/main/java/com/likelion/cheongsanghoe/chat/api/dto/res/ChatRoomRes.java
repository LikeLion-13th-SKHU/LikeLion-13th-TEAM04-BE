package com.likelion.cheongsanghoe.chat.api.dto.res;

import com.likelion.cheongsanghoe.chat.domain.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ChatRoomRes(
        @Schema(description = "채팅방 ID")
        Long id,

        @Schema(description = "채팅방 이름")
        String name,

        @Schema(description = "생성자 ID")
        Long creatorId,

        @Schema(description = "참가자 ID")
        Long participantId,

        @Schema(description = "연관 게시글 ID")
        Long postId,

        @Schema(description = "생성 시각")
        LocalDateTime createdAt,

        @Schema(description = "상대 프로필")
        ChatParticipantProfileRes participantProfile
) {
    public static ChatRoomRes from(ChatRoom chatRoom) {
        return new ChatRoomRes(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getCreatorId(),
                chatRoom.getParticipantId(),
                chatRoom.getPostId(),
                chatRoom.getCreatedAt(),
                null
        );
    }
    public static ChatRoomRes from(ChatRoom chatRoom, ChatParticipantProfileRes profile) {
        return new ChatRoomRes(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getCreatorId(),
                chatRoom.getParticipantId(),
                chatRoom.getPostId(),
                chatRoom.getCreatedAt(),
                profile
        );
    }
}
