package com.likelion.cheongsanghoe.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "post_id")
    private Long postId; // 게시글과 연계

    @Builder
    public ChatRoom(String name, Long creatorId, Long participantId, Long postId) {
        this.name = name;
        this.creatorId = creatorId;
        this.participantId = participantId;
        this.postId = postId;
    }
}
