package com.likelion.cheongsanghoe.chat.application;

import com.likelion.cheongsanghoe.chat.domain.ChatMessage;
import com.likelion.cheongsanghoe.chat.domain.MessageType;
import com.likelion.cheongsanghoe.chat.domain.repository.ChatMessageRepository;
import com.likelion.cheongsanghoe.chat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;

    // 메시지 저장
    @Transactional
    public ChatMessage save(Long roomId, Long senderId, MessageType type, String content) {
        chatRoomService.checkUserInRoom(roomId, senderId);
        return chatMessageRepository.save(new ChatMessage(roomId, senderId, type, content));
    }

    // 메시지 히스토리 조회 (무한스크롤 커서)
    @Transactional(readOnly = true)
    public Slice getHistory(Long roomId, Long cursor, int size, Long userId){
        chatRoomService.checkUserInRoom(roomId, userId);
        List<ChatMessage> list = chatMessageRepository.findSlice(roomId, cursor, PageRequest.of(0, size));
        Collections.reverse(list); // 오래된 -> 최신 순
        Long nextCursor = list.isEmpty() ? null : list.get(0).getId();
        boolean hasNext = list.size() == size;
        return new Slice(list, hasNext, nextCursor);
    }
    public record Slice(List<ChatMessage> content, boolean hasNext, Long nextCursor){}

}
