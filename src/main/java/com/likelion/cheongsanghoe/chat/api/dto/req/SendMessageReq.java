package com.likelion.cheongsanghoe.chat.api.dto.req;

import com.likelion.cheongsanghoe.chat.domain.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendMessageReq {

    @Schema(description = "메시지 타입", example = "TALK")
    private MessageType type;

    @Schema(description = "메시지 내용", example = "안녕하세요!")
    private String content;
}
