package com.likelion.cheongsanghoe.chat.api.dto.res;

import com.likelion.cheongsanghoe.chat.domain.ChatMessage;
import com.likelion.cheongsanghoe.chat.domain.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ChatMessageRes(
        @Schema(description = "메시지 ID")
        Long id,

        @Schema(description = "채팅방 ID")
        Long roomId,

        @Schema(description = "보낸 사용자 ID")
        Long senderId,

        @Schema(description = "메시지 타입")
        MessageType type,

        @Schema(description = "내용")
        String content,

        @Schema(description = "생성 시각")
        LocalDateTime createdAt
) {

    public static ChatMessageRes from(ChatMessage chatMessage) {
        return new ChatMessageRes(
                chatMessage.getId(),
                chatMessage.getRoomId(),
                chatMessage.getSenderId(),
                chatMessage.getType(),
                chatMessage.getContent(),
                chatMessage.getCreatedAt()
        );
    }
}
