package com.likelion.cheongsanghoe.chat.api.dto.req;

import com.likelion.cheongsanghoe.chat.domain.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRoomReq {

    @Schema(description = "방 타입 (일반, 챗봇)", example = "BOT")
    private RoomType type;

    @Schema(description = "상대 ID", example = "2")
    private Long otherUserId; // bot에는 null 허용

    @Schema(description = "채팅방 이름", example = "지원합니다!")
    private String roomName; // 선택

    @Schema(description = "연관 게시글 ID", example = "1")
    private Long postId; // 선택
}
