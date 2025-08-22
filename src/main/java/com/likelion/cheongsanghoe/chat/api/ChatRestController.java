package com.likelion.cheongsanghoe.chat.api;

import com.likelion.cheongsanghoe.chat.api.dto.req.CreateRoomReq;
import com.likelion.cheongsanghoe.chat.api.dto.req.SendMessageReq;
import com.likelion.cheongsanghoe.chat.api.dto.res.ChatMessageRes;
import com.likelion.cheongsanghoe.chat.api.dto.res.ChatRoomRes;
import com.likelion.cheongsanghoe.chat.api.dto.res.CreateRoomRes;
import com.likelion.cheongsanghoe.chat.api.dto.res.MessageHistoryRes;
import com.likelion.cheongsanghoe.chat.application.ChatMessageService;
import com.likelion.cheongsanghoe.chat.application.ChatRoomService;
import com.likelion.cheongsanghoe.chat.domain.ChatMessage;
import com.likelion.cheongsanghoe.chat.domain.ChatRoom;
import com.likelion.cheongsanghoe.exception.Response;
import com.likelion.cheongsanghoe.exception.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "채팅 REST")
public class ChatRestController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅방 생성 또는 기존 채팅방 반환")
    @PostMapping("/rooms")
    public ResponseEntity<Response<CreateRoomRes>> getOrCreateRoom(
            @AuthenticationPrincipal(expression = "id") Long myId,
            @RequestBody CreateRoomReq req) {
        Long roomId = chatRoomService.getOrCreateDirectRoom(
                myId,
                req.getOtherUserId(),
                req.getRoomName(),
                req.getPostId()
        );
        return ResponseEntity.ok(Response.success(SuccessStatus.SUCCESS, new CreateRoomRes(roomId)));
    }

    @Operation(summary = "내가 속한 방 목록 조회")
    @GetMapping("/rooms")
    public ResponseEntity<Response<List<ChatRoomRes>>> myRooms(
            @AuthenticationPrincipal(expression = "id") Long myId
    ) {
        List<ChatRoom> rooms = chatRoomService.getRoomsByUser(myId);
        List<ChatRoomRes> body = rooms.stream().map(ChatRoomRes::from).toList();
        return ResponseEntity.ok(Response.success(SuccessStatus.SUCCESS, body));
    }

    @Operation(summary = "채팅 메시지 저장(전송)")
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Response<ChatMessageRes>> sendMessage(@PathVariable Long roomId,
                                                                @AuthenticationPrincipal(expression = "id") Long senderId,
                                                                @RequestBody SendMessageReq req){
        ChatMessage saved = chatMessageService.save(roomId, senderId, req.getType(), req.getContent());
        return ResponseEntity.ok(Response.success(SuccessStatus.SUCCESS, ChatMessageRes.from((saved))));
    }

    @Operation(summary = "메시지 히스토리 조회")
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Response<MessageHistoryRes>>history(@PathVariable Long roomId,
                                                              @RequestParam(required = false) Long cursor,
                                                              @RequestParam(defaultValue = "50") int size) {
        var slice = chatMessageService.getHistory(roomId, cursor, size);
        var content = slice.content().stream().map(ChatMessageRes::from).toList();
        var body = MessageHistoryRes.from(content, slice.hasNext(), slice.nextCursor());
        return ResponseEntity.ok(
                Response.success(SuccessStatus.SUCCESS, body)
        );
    }
}
