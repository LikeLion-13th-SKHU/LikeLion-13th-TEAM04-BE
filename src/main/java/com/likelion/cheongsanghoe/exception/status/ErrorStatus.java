package com.likelion.cheongsanghoe.exception.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus {
    // COMMON 4XX
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON400", "파라미터가 올바르지 않습니다."),
    INVALID_BODY(HttpStatus.BAD_REQUEST, "COMMON400", "요청 본문이 올바르지 않습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "찾을 수 없는 리소스입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "사용자를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "허용되지 않는 HTTP Method입니다."),

    // AUTH 관련 에러(로그인관련 에러)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH401", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401", "만료된 토큰입니다."),
    OAUTH2_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH400", "OAuth2 공급자로부터 이메일을 찾을 수 없습니다."),
    UNSUPPORTED_OAUTH2_PROVIDER(HttpStatus.BAD_REQUEST, "AUTH400", "지원하지 않는 OAuth2 공급자입니다."),
    OAUTH2_AUTHENTICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH500", "OAuth2 인증 처리 중 오류가 발생했습니다."),

    // MEMBER 관련 에러(마이페이지 관련 에러)
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "회원을 찾을 수 없습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER409", "이미 존재하는 회원입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER409", "이미 사용 중인 닉네임입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "MEMBER409", "이미 사용 중인 전화번호입니다."),
    MEMBER_SUSPENDED(HttpStatus.FORBIDDEN, "MEMBER403", "정지된 회원입니다."),//정지된 회원을 넣은 이유는 구인구직 사이트에서 사기 치는 유저가 생길 가능성을 생각해서 넣었습니다.
    MEMBER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "MEMBER403", "활성화되지 않은 회원입니다."),

    //chat
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT404", "존재하지 않는 채팅방입니다."),
    NOT_CHAT_ROOM_MEMBER(HttpStatus.FORBIDDEN, "CHAT403", "해당 방의 참여자가 아닙니다."),
    NOT_ALLOWED_SELF_CHAT(HttpStatus.BAD_REQUEST, "CHAT400", "자기 자신과는 채팅방을 생성할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}