package com.likelion.cheongsanghoe.member.application;

import com.likelion.cheongsanghoe.auth.domain.User;
import com.likelion.cheongsanghoe.auth.domain.repository.UserRepository;
import com.likelion.cheongsanghoe.exception.MemberNotFoundException;
import com.likelion.cheongsanghoe.exception.DuplicateNicknameException;
import com.likelion.cheongsanghoe.exception.DuplicatePhoneNumberException;
import com.likelion.cheongsanghoe.exception.MemberAlreadyExistException;
import com.likelion.cheongsanghoe.member.api.dto.request.MemberUpdateRequestDto;
import com.likelion.cheongsanghoe.member.api.dto.response.MemberInfoResponseDto;
import com.likelion.cheongsanghoe.member.domain.Member;
import com.likelion.cheongsanghoe.member.domain.MemberStatus;
import com.likelion.cheongsanghoe.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    // 🚨 [수정] : 기존 createMemberProfile 메소드를 아래 메소드로 완전히 교체합니다.
    @Transactional
    public MemberInfoResponseDto createMemberProfileByEmail(String email, MemberUpdateRequestDto requestDto) {
        log.info("Creating member profile for user email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 이미 프로필이 존재하는지 확인
        if (memberRepository.findByUser(user).isPresent()) {
            throw new MemberAlreadyExistException("회원이 이미 존재합니다. email: " + email);
        }

        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicateNicknameException(requestDto.getNickname());
        }

        // 전화번호 중복 체크
        if (requestDto.getPhoneNumber() != null &&
                memberRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
            throw new DuplicatePhoneNumberException(requestDto.getPhoneNumber());
        }

        Member member = Member.builder()
                .user(user)
                .nickname(requestDto.getNickname())
                .phoneNumber(requestDto.getPhoneNumber())
                .address(requestDto.getAddress())
                .detailAddress(requestDto.getDetailAddress())
                .zipCode(requestDto.getZipCode())
                .bio(requestDto.getBio())
                .jobCategory(requestDto.getJobCategory())
                .preferredLocation(requestDto.getPreferredLocation())
                .skills(requestDto.getSkills())
                .experienceYears(requestDto.getExperienceYears())
                .education(requestDto.getEducation())
                .salaryExpectation(requestDto.getSalaryExpectation())
                .status(MemberStatus.ACTIVE) // 🚨 [수정] : 회원의 초기 상태를 ACTIVE로 설정합니다.
                .build();

        Member savedMember = memberRepository.save(member);
        log.info("Member profile created successfully. MemberId: {}", savedMember.getId());

        return MemberInfoResponseDto.of(savedMember);
    }

    // 🚨 [수정] : 기존 updateMember 메소드를 아래 메소드로 완전히 교체합니다.
    @Transactional
    public MemberInfoResponseDto updateMemberByEmail(String email, MemberUpdateRequestDto requestDto) {
        log.info("Updating member info for user email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Member member = memberRepository.findByUser(user)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

        // 닉네임 변경시 중복 체크
        if (!member.getNickname().equals(requestDto.getNickname()) &&
                memberRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicateNicknameException(requestDto.getNickname());
        }

        // 전화번호 변경시 중복 체크
        if (requestDto.getPhoneNumber() != null &&
                !requestDto.getPhoneNumber().equals(member.getPhoneNumber()) &&
                memberRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
            throw new DuplicatePhoneNumberException(requestDto.getPhoneNumber());
        }

        member.updateProfile(
                requestDto.getNickname(),
                requestDto.getPhoneNumber(),
                requestDto.getAddress(),
                requestDto.getDetailAddress(),
                requestDto.getZipCode(),
                requestDto.getBio(),
                requestDto.getJobCategory(),
                requestDto.getPreferredLocation(),
                requestDto.getSkills(),
                requestDto.getExperienceYears(),
                requestDto.getEducation(),
                requestDto.getSalaryExpectation()
        );

        log.info("Member info updated successfully. MemberId: {}", member.getId());
        return MemberInfoResponseDto.of(member);
    }

    // 🚨 [수정] : 기존 withdrawMember 메소드를 아래 메소드로 완전히 교체합니다.
    @Transactional
    public void withdrawMemberByEmail(String email) {
        log.info("Withdrawing member for user email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Member member = memberRepository.findByUser(user)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

        member.withdraw();
        log.info("Member withdrawn successfully. MemberId: {}", member.getId());
    }

    // 🚨 [수정] : 기존 getMemberInfo 메소드를 아래 메소드로 완전히 교체합니다.
    @Transactional(readOnly = true)
    public MemberInfoResponseDto getMemberInfoByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Member member = memberRepository.findByUser(user)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));
        return MemberInfoResponseDto.of(member);
    }

    // --- 아래는 기존에 있던 다른 메소드들입니다. 그대로 유지하면 됩니다. ---

    public MemberInfoResponseDto getMemberById(Long memberId) {
        log.info("Getting member info by memberId: {}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        if (!member.isActive()) {
            throw new RuntimeException("비활성화된 회원입니다.");
        }

        return MemberInfoResponseDto.of(member);
    }

    @Transactional
    public MemberInfoResponseDto updateProfileImage(Long userId, String profileImageUrl) {
        log.info("Updating profile image for userId: {}", userId);

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new MemberNotFoundException("회원 정보를 찾을 수 없습니다."));

        member.updateProfileImage(profileImageUrl);
        return MemberInfoResponseDto.of(member);
    }

    public boolean isNicknameAvailable(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    public boolean isPhoneNumberAvailable(String phoneNumber) {
        return !memberRepository.existsByPhoneNumber(phoneNumber);
    }

    public Page<MemberInfoResponseDto> searchMembersByNickname(String keyword, Pageable pageable) {
        log.info("Searching members by nickname: {}", keyword);

        Page<Member> members = memberRepository.searchByNickname(keyword, pageable);
        return members.map(MemberInfoResponseDto::of);
    }

    public Page<MemberInfoResponseDto> getMembersByAddress(String address, Pageable pageable) {
        log.info("Getting members by address: {}", address);

        Page<Member> members = memberRepository.findByAddressContaining(address, pageable);
        return members.map(MemberInfoResponseDto::of);
    }

    public Page<MemberInfoResponseDto> getMembersByRole(com.likelion.cheongsanghoe.auth.domain.Role role, Pageable pageable) {
        log.info("Getting members by role: {}", role);

        Page<Member> members = memberRepository.findByUserRole(role, pageable);
        return members.map(MemberInfoResponseDto::of);
    }
}
