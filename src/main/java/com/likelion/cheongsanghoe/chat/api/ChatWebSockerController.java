package com.likelion.cheongsanghoe.chat.api;

import com.likelion.cheongsanghoe.chat.api.dto.req.ChatSendPayload;
import com.likelion.cheongsanghoe.chat.api.dto.res.DeliverDto;
import com.likelion.cheongsanghoe.chat.application.ChatMessageService;
import com.likelion.cheongsanghoe.chat.application.ChatRoomService;
import com.likelion.cheongsanghoe.chat.domain.ChatMessage;
import com.likelion.cheongsanghoe.chat.domain.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class ChatWebSockerController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
//    private final AiChatService aiChatService;

    // /app/chat.send로 들어온 메시지 처리
    // STOMP 클라이언트가 이 경로로 send 요청 보낼 때 호출
    @MessageMapping("/chat.send")
    public void send(ChatSendPayload payload){
        // DB에 메시지 저장
        ChatMessage saved = chatMessageService.save(
                payload.getRoomId(),
                payload.getSenderId(),
                payload.getType(),
                payload.getContent()
        );

        // 수신자 ID 구하기
        ChatRoom room = chatRoomService.findRoomOrThrow(payload.getRoomId());
        Long receiverId = Objects.equals(room.getCreatorId(), payload.getSenderId())
                ? room.getParticipantId()
                : room.getCreatorId();

        // 수신자의 개인 큐로 메시지 전송
        messagingTemplate.convertAndSend("/queue/user/" + receiverId,
                new DeliverDto(
                        saved.getId(),
                        saved.getRoomId(),
                        saved.getSenderId(),
                        saved.getType(),
                        saved.getContent(),
                        saved.getCreatedAt()
                ));

        // 챗봇 채팅방이면 AI 호출 트리거
//        aiChatService.handleIfBotRoomAsync(room, saved);
    }
}

