package com.likelion.cheongsanghoe.auth.security.oauth2;

import com.likelion.cheongsanghoe.auth.domain.User;
import com.likelion.cheongsanghoe.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        com.likelion.cheongsanghoe.auth.security.oauth2.OAuth2UserInfo userInfo = com.likelion.cheongsanghoe.auth.security.oauth2.OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        User user = getOrCreateUser(userInfo);

        return com.likelion.cheongsanghoe.auth.security.oauth2.UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User getOrCreateUser(com.likelion.cheongsanghoe.auth.security.oauth2.OAuth2UserInfo userInfo) {
        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 기존 사용자 정보 업데이트
            user.updateProfile(userInfo.getName(), userInfo.getImageUrl());
            return userRepository.save(user);
        } else {
            // 새 사용자 생성
            User newUser = User.builder()
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .profileImage(userInfo.getImageUrl())
                    .build();
            return userRepository.save(newUser);
        }
    }
}