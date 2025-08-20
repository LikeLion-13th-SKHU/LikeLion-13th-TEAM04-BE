package com.likelion.cheongsanghoe.chatbot.application;

import com.likelion.cheongsanghoe.auth.domain.User;
import com.likelion.cheongsanghoe.auth.domain.repository.UserRepository;
import com.likelion.cheongsanghoe.exception.CustomException;
import com.likelion.cheongsanghoe.exception.status.ErrorStatus;
import com.likelion.cheongsanghoe.member.domain.BotType;
import com.likelion.cheongsanghoe.member.domain.Member;
import com.likelion.cheongsanghoe.member.domain.MemberStatus;
import com.likelion.cheongsanghoe.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BotUserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    // 여러 스레드에서 봐도 최신값이도록 (volatile 사용)
    private volatile Long cachedBotMemberId;

    // 봇 유저가 없으면 생성, 있으면 그 ID 반환
    @Transactional
    public Long ensureBotUser() {
        if (cachedBotMemberId != null) { return cachedBotMemberId; }

            User botUser = userRepository.findByEmail("bot@system")
                    .orElseGet(() -> userRepository.save(
                            User.createBot("bot@system", "Cheongsanghoe Bot")
                    ));

            Member botMember = memberRepository.findByUser(botUser)
                    .orElseGet(() -> memberRepository.save(
                            Member.builder()
                                    .user(botUser)
                                    .nickname("Seoulmate Bot")
                                    .status(MemberStatus.ACTIVE)
                                    .botType(BotType.BOT)
                                    .build()
                    ));

            return cachedBotMemberId = botMember.getId();
        }

        public Long getBotMemberIdOrThrow() {
        if(cachedBotMemberId == null)
            throw new CustomException(ErrorStatus.BOT_MEMBER_NOT_INITIALIZED);
        return cachedBotMemberId;
        }
}
