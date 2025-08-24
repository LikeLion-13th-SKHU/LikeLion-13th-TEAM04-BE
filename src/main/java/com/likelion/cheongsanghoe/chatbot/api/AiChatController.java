package com.likelion.cheongsanghoe.chatbot.api;

import com.likelion.cheongsanghoe.chatbot.api.dto.AiChatReq;
import com.likelion.cheongsanghoe.chatbot.api.dto.AiMatchCard;
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

import java.util.List;
import java.util.Map;

@Tag(name = "AI 대화")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiChatController {

    private final AiClient aiClient;

    @Operation(summary = "AI 대화", description = "프->백 (/ai/chat) -> Flast(AI)(/chat)")
    @PostMapping("/chat")
    public ResponseEntity<Response<List<AiMatchCard>>> chat(@RequestBody AiChatReq request) {
        // 원문 받기
        Map<String, Object> ai = aiClient.askRaw(request.roomId(), request.userId(), request.text());
        // data.results 꺼내기
        @SuppressWarnings("unchecked")
        Map<String, Object> dat = (Map<String, Object>) ai.get("data");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) dat.get("results");

        List<AiMatchCard> cards = results.stream()
                .map(r -> AiMatchCard.builder()
                        .id((String) r.get("id"))
                        .name((String) r.get("name"))
                        .profile((String) r.get("profile"))
                        .score(toPercent(r.get("score")))
                        .type((String) r.get("type"))
                        .gender((String) r.get("gender"))
                        .job((String) r.get("job"))
                        .skills((String) r.get("skills"))
                        .build())
                .toList();

        return ResponseEntity.ok(Response.success(SuccessStatus.SUCCESS, cards));
    }

    private Integer toPercent(Object score) {
        if(score == null) return null;
        try{
            double v = Double.parseDouble(String.valueOf(score));
            return (int) Math.round(v * 100);
        } catch (Exception e) {
            return null;
        }
    }
}
