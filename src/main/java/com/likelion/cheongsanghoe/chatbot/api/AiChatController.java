package com.likelion.cheongsanghoe.chatbot.api;

import com.likelion.cheongsanghoe.chatbot.api.dto.AiChatReq;
import com.likelion.cheongsanghoe.chatbot.application.AiClient;
import com.likelion.cheongsanghoe.exception.Response;
import com.likelion.cheongsanghoe.exception.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI 대화")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiChatController {

    private final AiClient aiClient;

    @Operation(summary = "AI 대화", description = "프->백 (/ai/chat) -> Flast(AI)(/chat)")
    @PostMapping("/chat")
    public ResponseEntity<Response<String>> chat(@RequestBody AiChatReq request) {
        String answer = aiClient.ask(request.roomId(), request.userId(), request.text());
        return ResponseEntity.ok(Response.success(SuccessStatus.SUCCESS, answer));
    }
}
