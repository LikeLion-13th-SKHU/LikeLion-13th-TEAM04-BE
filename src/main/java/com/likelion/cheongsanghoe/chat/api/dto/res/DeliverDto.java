package com.likelion.cheongsanghoe.chat.api.dto.res;

import com.likelion.cheongsanghoe.chat.domain.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record DeliverDto (

        @Schema(description = "메시지 ID", example = "1")
        Long id,

        @Schema(description = "채팅방 ID", example = "1")
        Long roomId,

        @Schema(description = "보낸 사용자 ID", example = "1")
        Long senderId,

        @Schema(description = "메시지 타입", example = "TALK")
        MessageType type,

        @Schema(description = "메시지 내용", example = "안녕하세요~")
        String content,

        @Schema(description = "생성 시각", example = "2025-08-12T17:42:10")
        LocalDateTime createdAt
){
}
