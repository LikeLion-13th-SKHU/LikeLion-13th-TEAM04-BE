package com.likelion.cheongsanghoe.auth.security.oauth2;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static com.likelion.cheongsanghoe.auth.security.oauth2.OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return new com.likelion.cheongsanghoe.auth.security.oauth2.GoogleOAuth2UserInfo(attributes);
        } else {
            throw new RuntimeException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        }
    }
}