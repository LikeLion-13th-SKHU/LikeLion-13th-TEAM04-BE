package com.likelion.cheongsanghoe.chatbot.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.cheongsanghoe.exception.CustomException;
import com.likelion.cheongsanghoe.exception.status.ErrorStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AiClient {

    // 백 -> AI(Flask) 호출 (Flask 서버의 /chatbot/ask로 HTTP POST)

    @Value("${ai.base-url}")
    private String baseUrl;

    private RestClient client;
    private final ObjectMapper om = new ObjectMapper();

    @PostConstruct
    public void init() {
        // 타임아웃 적용
        var f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(2000); // 연결 타임아웃
        f.setReadTimeout(30000); // 응답 타임아웃
        client = RestClient.builder().baseUrl(baseUrl).requestFactory(f).build();
    }

    // AI에 질문하고 answer 문자열 반환
    public String ask(Long roomId, Long humanMemberId, String text) {
        Map<String, Object> body = Map.of(
                "roomId", roomId,
                "userId", humanMemberId,
                "text", text
        );
        String json = toJson(body);

        try{
            var res = client.post()
                    .uri("/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            (req, r) -> {
                                // 잘못된 요청, 인증 실패
                                throw new CustomException(ErrorStatus.AI_CLIENT_ERROR, "AI 4xx: " + r.getStatusCode());
                            }).onStatus(HttpStatusCode::is5xxServerError, (req, r) -> {
                        // AI 서버에서 처리 실패
                        throw new CustomException(ErrorStatus.AI_SERVER_ERROR, "AI 5xx: " + r.getStatusCode());
                    }).body(Map.class);

            // answer키가 반드시 있어야 함
            Object answer = (res == null) ? null : res.get("answer");
            if (answer == null) {
                throw new CustomException(ErrorStatus.AI_BAD_RESPONSE, "AI 응답 포맷 오류");
            }
            return String.valueOf(answer);

        } catch(RestClientException e){
            // 네트워크, 타임아웃
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new CustomException(ErrorStatus.AI_TIMEOUT, "AI 응답 시간 초과: " + e.getMessage());
            }
            throw new CustomException(ErrorStatus.AI_CLIENT_ERROR, "AI 호출 실패: " + e.getMessage());
        }
    }

    private String toJson(Object o){
        try{
            return om.writeValueAsString(o);
        } catch (Exception e) {
            throw new CustomException(ErrorStatus.INTERNAL_SERVER_ERROR, "직렬화 실패");
        }
    }
}

