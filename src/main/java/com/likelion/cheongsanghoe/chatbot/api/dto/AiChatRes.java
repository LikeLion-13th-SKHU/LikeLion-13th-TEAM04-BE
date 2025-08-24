package com.likelion.cheongsanghoe.chatbot.api.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AiChatRes(
        String reply,
        List<AiMatchCard> results
) {
}
