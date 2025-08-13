package com.likelion.cheongsanghoe.chat.application;

import com.likelion.cheongsanghoe.chat.domain.ChatRoom;
import com.likelion.cheongsanghoe.chat.domain.repository.ChatRoomRepository;
import com.likelion.cheongsanghoe.exception.CustomException;
import com.likelion.cheongsanghoe.exception.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 단건 조회
    @Transactional(readOnly = true)
    public ChatRoom findRoomOrThrow(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));
    }
    // 같은 사람이면 기존 채팅방 사용, 없으면 새로 생성
    @Transactional
    public Long getOrCreateDirectRoom(Long myId, Long otherUserId, String roomName, Long postId){
        if(Objects.equals(myId, otherUserId)){
            throw new CustomException(ErrorStatus.NOT_ALLOWED_SELF_CHAT);
        }
        return chatRoomRepository
                .findRoomByMembersAndOptionalPostId(myId, otherUserId, postId)
                .map(ChatRoom::getId)
                .orElseGet(() -> {
                    ChatRoom room = ChatRoom.builder()
                            .name(roomName)
                            .creatorId(myId)
                            .participantId(otherUserId)
                            .postId(postId)
                            .build();
                    return chatRoomRepository.save(room).getId();
                });
    }

    // 나의 채팅방 조회
    @Transactional(readOnly = true)
    public List<ChatRoom> getRoomsByUser(Long userId){
        return chatRoomRepository.findRoomsByUserId(userId);
    }

    // 채팅방 멤버 권한 검사
    @Transactional(readOnly = true)
    public void checkUserInRoom(Long roomId, Long userId) {
        ChatRoom room = findRoomOrThrow(roomId);
        boolean isMember = Objects.equals(room.getCreatorId(), userId)
                || Objects.equals(room.getParticipantId(), userId);
        if(!isMember){
            throw new CustomException(ErrorStatus.NOT_CHAT_ROOM_MEMBER);
        }
    }

    // 방 삭제
    @Transactional
    public void delete(Long roomId) {
        chatRoomRepository.deleteById(roomId);
    }

}
