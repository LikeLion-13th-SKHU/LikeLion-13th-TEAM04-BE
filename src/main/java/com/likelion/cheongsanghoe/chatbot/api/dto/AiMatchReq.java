package com.likelion.cheongsanghoe.chatbot.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiMatchReq(
        @JsonProperty("user_type") String userType,
        String query
) {
}
