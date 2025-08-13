package com.likelion.cheongsanghoe.chat.api.dto.req;

import com.likelion.cheongsanghoe.chat.domain.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatSendPayload {

    @Schema(description = "채팅방 ID", example = "1")
    private Long roomId;

    @Schema(description = "보낸 사용자 ID", example = "1")
    private Long senderId;

    @Schema(description = "메시지 타입", example = "TALK")
    private MessageType type;

    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;
}
