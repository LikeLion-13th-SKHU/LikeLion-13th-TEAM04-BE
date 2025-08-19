package com.likelion.cheongsanghoe.exception.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    SUCCESS(HttpStatus.OK, "COMMON200", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON201", "리소스가 성공적으로 생성되었습니다."),


    LOGIN_SUCCESS(HttpStatus.OK, "AUTH200", "로그인이 성공적으로 처리되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "AUTH200", "로그아웃이 성공적으로 처리되었습니다."),
    USER_INFO_SUCCESS(HttpStatus.OK, "AUTH200", "사용자 정보를 성공적으로 조회했습니다."),

    MEMBER_CREATED(HttpStatus.CREATED, "MEMBER201", "회원이 성공적으로 생성되었습니다."),
    MEMBER_INFO_SUCCESS(HttpStatus.OK, "MEMBER200", "회원 정보를 성공적으로 조회했습니다."),
    MEMBER_UPDATED(HttpStatus.OK, "MEMBER200", "회원 정보가 성공적으로 수정되었습니다."),
    MEMBER_ACTIVATED(HttpStatus.OK, "MEMBER200", "회원이 성공적으로 활성화되었습니다."),
    MEMBER_SUSPENDED(HttpStatus.OK, "MEMBER200", "회원이 성공적으로 정지되었습니다."),

    POST_CREATED(HttpStatus.CREATED, "POST201", "공고가 성공적으로 생성되었습니다."),
    POST_SUCCESS(HttpStatus.OK, "POST200", "공고를 성공적으로 조회했습니다."),
    POST_UPDATED(HttpStatus.OK, "POST200", "공고가 성공적으로 수정되었습니다."),
    POST_DELETE(HttpStatus.OK, "POST200", "공고가 성공적으로 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
