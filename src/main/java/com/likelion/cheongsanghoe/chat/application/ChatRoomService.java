package com.likelion.cheongsanghoe.chat.application;

import com.likelion.cheongsanghoe.auth.domain.repository.UserRepository;
import com.likelion.cheongsanghoe.chat.api.dto.res.ChatParticipantProfileRes;
import com.likelion.cheongsanghoe.chat.api.dto.res.ChatRoomRes;
import com.likelion.cheongsanghoe.chat.domain.ChatRoom;
import com.likelion.cheongsanghoe.chat.domain.repository.ChatRoomRepository;
import com.likelion.cheongsanghoe.exception.CustomException;
import com.likelion.cheongsanghoe.exception.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 채팅방 단건 조회
    @Transactional(readOnly = true)
    public ChatRoom findRoomOrThrow(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorStatus.CHATROOM_NOT_FOUND));
    }

    // 챗봇 : 항상 새 방
    @Transactional
    public Long createBotRoom(Long myId, Long botUserId, String roomName){
        if(Objects.equals(myId, botUserId)){
            throw new CustomException(ErrorStatus.NOT_ALLOWED_SELF_CHAT);
        }
        ChatRoom room = ChatRoom.builder()
                .name(roomName)
                .creatorId(myId)
                .participantId(botUserId)
                .postId(null)
                .botUserId(botUserId) // 봇 방
                .build();
        return chatRoomRepository.save(room).getId();
    }

    // 사람 채팅방도 항상 새방이 필요할 때 사용
    @Transactional
    public Long createRoom(Long myId, Long otherUserId, String roomName, Long postId){
        if(Objects.equals(myId, otherUserId)){
            throw new CustomException(ErrorStatus.NOT_ALLOWED_SELF_CHAT);
        }
        ChatRoom room = ChatRoom.builder()
                .name(roomName)
                .creatorId(myId)
                .participantId(otherUserId)
                .postId(postId)
                .botUserId(null) // 일반 방
                .build();
        return chatRoomRepository.save(room).getId();
    }

    // 같은 조합의 사람이면 재사용, 없으면 생성
    @Transactional
    public Long getOrCreateDirectRoom(Long myId, Long otherUserId, String roomName, Long postId){
        if(Objects.equals(myId, otherUserId)){
            throw new CustomException(ErrorStatus.NOT_ALLOWED_SELF_CHAT);
        }
        return chatRoomRepository
                .findRoomByMembersAndOptionalPostId(myId, otherUserId, postId)
                .map(ChatRoom::getId)
                .orElseGet(() -> createRoom(myId, otherUserId, roomName, postId));
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

    // 참가자 프로필 목록 조회
    @Transactional(readOnly = true)
    public List<ChatRoomRes> getRoomsByUserWithParticipants(Long userId){
        List<ChatRoom> rooms = chatRoomRepository.findRoomsByUserId(userId);

        return rooms.stream().map(r -> {
            Long opponentUserId = Objects.equals(r.getCreatorId(), userId)
                    ? r.getParticipantId() : r.getCreatorId();

            ChatParticipantProfileRes profile = userRepository.findParticipantProfileById(opponentUserId)
                    .orElse(new ChatParticipantProfileRes(opponentUserId, "Unknown", null));

            return ChatRoomRes.from(r, profile);
        }).toList();
    }

    // 방 삭제
    @Transactional
    public void delete(Long roomId) {
        chatRoomRepository.deleteById(roomId);
    }

}
