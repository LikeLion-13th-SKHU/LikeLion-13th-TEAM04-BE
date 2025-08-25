package com.likelion.cheongsanghoe.chat.domain.repository;

import com.likelion.cheongsanghoe.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 두 사용자와 postId로 채팅방 조회
    @Query("""
        select c from ChatRoom c
        where ((c.creatorId = :userId1 and c.participantId = :userId2)
            or  (c.creatorId = :userId2 and c.participantId = :userId1))
          and (:postId is null or c.postId = :postId)
        """)
    Optional<ChatRoom> findRoomByMembersAndOptionalPostId(Long userId1, Long userId2, Long postId);

    // 특정 사용자가 참여 중인 모든 채팅방 조회 (최신순)
    @Query("""
        select c from ChatRoom c
        where c.botUserId is not null and c.creatorId = :userId or c.participantId = :userId
        order by c.createdAt desc
        """)
    public List<ChatRoom> findRoomsByUserId(Long userId);

    // 전체 채팅방 조회 (최신순)
    @Query("""
        select c from ChatRoom c
        order by c.createdAt desc
        """)
    List<ChatRoom> findAllRooms();
}
