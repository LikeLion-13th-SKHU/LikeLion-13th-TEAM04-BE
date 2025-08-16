package com.likelion.cheongsanghoe.chat.api.dto.res;

import java.util.List;

public record MessageHistoryRes(
        List<ChatMessageRes> content,
        boolean hasNext,
        Long nextCursor
) {
    public static MessageHistoryRes from(List<ChatMessageRes> content, boolean hasNext, Long nextCursor) {
        return new MessageHistoryRes(content, hasNext, nextCursor);
    }
}
