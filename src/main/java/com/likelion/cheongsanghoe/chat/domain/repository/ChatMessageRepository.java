package com.likelion.cheongsanghoe.chat.domain.repository;

import com.likelion.cheongsanghoe.chat.domain.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 커서 기반 페이지네이션 (마지막 메시지 id를 기억해 그 id보다 작은 데이터 불러옴)
    @Query("""
        select m from ChatMessage m
        where m.roomId = :roomId
          and (:cursor is null or m.id < :cursor)
        order by m.id desc
        """)
    List<ChatMessage> findSlice(@Param("roomId") Long roomId,
                                @Param("cursor") Long cursor,
                                Pageable pageable);


    // 채팅방 목록, 각 채팅방의 마지막 메시지 1건 보여줌
    @Query(value = """
    SELECT m.* FROM chat_message m
    JOIN (
        SELECT room_id, MAX(id) AS max_id
        FROM chat_message
        GROUP BY room_id
    ) t ON t.room_id = m.room_id AND t.max_id = m.id
    WHERE m.room_id IN (:roomIds)
    ORDER BY m.created_at DESC
""", nativeQuery = true)
    List<ChatMessage> findLastByRoomIds(@Param("roomIds") List<Long> roomIds);
}
