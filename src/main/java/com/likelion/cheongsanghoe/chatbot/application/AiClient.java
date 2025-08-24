package com.likelion.cheongsanghoe.chatbot.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.cheongsanghoe.exception.CustomException;
import com.likelion.cheongsanghoe.exception.status.ErrorStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiClient {

    // 백 -> AI(Flask) 호출 (Flask 서버의 /chat/ask로 HTTP POST)

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
        log.info("[AI-INIT] baseUrl={}", baseUrl);
    }

    // AI에 질문하고 answer 문자열 반환
    public String ask(Long roomId, Long humanMemberId, String text) {
        Map<String, Object> body = Map.of(
                "roomId", roomId,
                "userId", humanMemberId,
                "text", text
        );
        String json = toJson(body);
        log.info("[AI-REQ]  POST {}/chat body={}", baseUrl, json);

        try{
            Map<String, Object> res = client.post()
                    .uri("/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            (req, r) -> {
                                log.warn("[AI-RES] 4xx status={}", r.getStatusCode());
                                // 잘못된 요청, 인증 실패
                                throw new CustomException(ErrorStatus.AI_CLIENT_ERROR, "AI 4xx: " + r.getStatusCode());
                            }).onStatus(HttpStatusCode::is5xxServerError, (req, r) -> {
                        log.error("[AI-RES] 5xx status={}", r.getStatusCode());
                        // AI 서버에서 처리 실패
                        throw new CustomException(ErrorStatus.AI_SERVER_ERROR, "AI 5xx: " + r.getStatusCode());
                    }).body(Map.class);

            log.info("[AI-RES]  POST {}/chat map={}", baseUrl, res);

            // 이전 포맷
            Object answer = res.get("answer");
            if(answer instanceof String s && !s.isBlank()){
                return s;
            }
            // 현재 포맷
            Object success = res.get("success");
            if(Boolean.FALSE.equals(success)){
                String msg = String.valueOf(res.getOrDefault("message", "AI 처리 실패"));
                throw new CustomException(ErrorStatus.AI_CLIENT_ERROR, msg);
            }

            Object data = res.get("data");
            if(data instanceof Map<?, ?> d){
                Object reply = d.get("reply");
                if(reply != null && !String.valueOf(reply).isBlank()){
                    return String.valueOf(reply);
                }
            }
            throw new CustomException(ErrorStatus.AI_BAD_RESPONSE, "AI 응답 포맷 오류");

        } catch(RestClientException e){
            log.error("[AI-ERR] 요청 실패: {}", e.getMessage(), e);
            // 네트워크, 타임아웃
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new CustomException(ErrorStatus.AI_TIMEOUT, "AI 응답 시간 초과: " + e.getMessage());
            }
            throw new CustomException(ErrorStatus.AI_CLIENT_ERROR, "AI 호출 실패: " + e.getMessage());
        }
    }

    public Map askRaw(Long roomId, Long humanMemberId, String text) {
        Map<String, Object> body = Map.of(
                "roomId", roomId,
                "userId", humanMemberId,
                "text", text
        );
        String json = toJson(body);
        log.info("[AI-REQ-RAW] POST {}/chat body={}", baseUrl, json);

        try{
            @SuppressWarnings("unchecked")
            Map<String, Object> res = client.post()
                    .uri("/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, r) -> {
                        log.warn("[AI-RES-RAW] 4xx status={}", r.getStatusCode());
                        throw new CustomException(ErrorStatus.AI_CLIENT_ERROR, "AI 4xx: " + r.getStatusCode());
                            })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, r) -> {
                        log.error("[AI-RES-RAW] 5xx status={}", r.getStatusCode());
                        throw new CustomException(ErrorStatus.AI_SERVER_ERROR, "AI 5xx: " + r.getStatusCode());
                    })
                    .body(Map.class);

            log.info("[AI-RES-RAW] POST {}/chat map={}", baseUrl, res);
            return res;

        } catch (RestClientException e){
            log.error("[AI-ERR] 요청 실패: {}", e.getMessage(), e);
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

