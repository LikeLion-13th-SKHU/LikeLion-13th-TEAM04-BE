package com.likelion.cheongsanghoe.chatbot.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AiChatReq(

        @Schema(description = "채팅방 ID", example = "1")
        Long roomId,

        @Schema(description = "유저 ID", example = "1")
        Long userId,

        @Schema(description = "채팅", example = "안녕하세요?")
        String text
) {
}
