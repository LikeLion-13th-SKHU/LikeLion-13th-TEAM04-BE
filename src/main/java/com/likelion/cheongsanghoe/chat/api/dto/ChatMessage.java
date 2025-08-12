package com.likelion.cheongsanghoe.chat.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long senderId;
    private String receiverId;
    private String content;
}
