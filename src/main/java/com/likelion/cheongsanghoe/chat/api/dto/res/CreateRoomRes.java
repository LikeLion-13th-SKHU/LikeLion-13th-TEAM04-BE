package com.likelion.cheongsanghoe.chat.api.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateRoomRes(
        @Schema(description = "생성or재사용된 채팅방 ID", example = "1")
        Long roomId
) {
}
