package com.likelion.cheongsanghoe.chatbot.application;

import com.likelion.cheongsanghoe.chat.api.dto.res.DeliverDto;
import com.likelion.cheongsanghoe.chat.application.ChatMessageService;
import com.likelion.cheongsanghoe.chat.domain.ChatMessage;
import com.likelion.cheongsanghoe.chat.domain.ChatRoom;
import com.likelion.cheongsanghoe.chat.domain.MessageType;
import com.likelion.cheongsanghoe.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// 챗봇이면 AI에 질문 후 봇 멤버 id로 저장 및 전송
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final AiClient aiClient;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final BotUserService botUserService;

    // 이 방이 챗봇인지 참가자 id로 판별
    @Async("aiExecutor")
    public void handleIfBotRoomAsync(ChatRoom room, ChatMessage userMsg){
        // 챗봇 방이 아니면
        if(!room.isBotRoom()) return;

        // 봇 멤버 ID
        Long botId = (room.getBotUserId() != null)
                ? room.getBotUserId()
                : botUserService.ensureBotUser();

        // 무한루프 방지
        if(botId.equals(userMsg.getSenderId())) return;

        // 사람 식별
        Long humanId = userMsg.getSenderId();

        try{
            // Flask 호출
            String answer = aiClient.ask(room.getId(), humanId, userMsg.getContent());

            // 챗봇 답변을 DB 저장 (senderId = 봇)
            ChatMessage botMsg = chatMessageService.save(room.getId(), botId, MessageType.TALK, answer);

            // 사용자에게 웹소켓 전송
            messagingTemplate.convertAndSend("/queue/user/"+ humanId, DeliverDto.from(botMsg));

        } catch (CustomException e){
            log.warn("AI chat error [{}:{}] room={}, userMsgId={}, detail={}",
                    e.getErrorCode().name(), e.getErrorCode().getCode(), room.getId(), userMsg.getId(), e.getMessage());

            String friendly = switch (e.getErrorCode()){
                case AI_TIMEOUT      -> "답변이 지연되고 있어요. 잠시 후 다시 시도해 주세요.";
                case AI_SERVER_ERROR -> "AI 서버에 문제가 있어요. 잠시 후 다시 시도해 주세요.";
                case AI_BAD_RESPONSE -> "AI 응답 처리 중 문제가 발생했어요.";
                case AI_CLIENT_ERROR -> "AI 요청 중 오류가 발생했어요.";
                default              -> "지금은 답변을 준비하는 데 문제가 발생했어요. 잠시 후 다시 시도해주세요.";
            };

            ChatMessage botMsg = chatMessageService.save(
                    room.getId(), botId, MessageType.TALK, friendly
            );
            messagingTemplate.convertAndSend("/queue/user/" + humanId, DeliverDto.from(botMsg));
        }
    }
}