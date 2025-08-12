package com.likelion.cheongsanghoe.chat.api;

import com.likelion.cheongsanghoe.chat.api.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send") // /app/chat.send로 들어온 메시지 처리
    public void sendMessage(ChatMessage message) {
        // 받는 사람 개인 큐로 전송
        messagingTemplate.convertAndSend("/queue/user/" + message.getReceiverId(), message);
    }
}
