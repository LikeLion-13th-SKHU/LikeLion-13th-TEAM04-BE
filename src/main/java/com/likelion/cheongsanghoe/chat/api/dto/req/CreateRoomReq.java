package com.likelion.cheongsanghoe.chat.api.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateRoomReq {

    @Schema(description = "내 ID", example = "1")
    private Long myId;

    @Schema(description = "상대 ID", example = "2")
    private Long otherUserId;

    @Schema(description = "채팅방 이름", example = "지원합니다!")
    private String roomName;

    @Schema(description = "연관 게시글 ID", example = "1")
    private Long postId;
}
