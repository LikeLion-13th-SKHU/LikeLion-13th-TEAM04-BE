package com.likelion.cheongsanghoe.auth.application;

import com.likelion.cheongsanghoe.auth.api.dto.response.LoginResponseDto;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TempAuthCodeStore {

    private static final long TTL_MILLIS = 60_000;
    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    public String save(LoginResponseDto dto){
        String code = UUID.randomUUID().toString();
        store.put(code, new Entry(dto, System.currentTimeMillis() + TTL_MILLIS));
        return code;
    }

    public LoginResponseDto consume(String code){
        Entry e = store.remove(code);
        if(e == null || System.currentTimeMillis() > e.expireAt){
            return null;
        }
        return e.payload;
    }

    private record Entry(LoginResponseDto payload, long expireAt) {}
}
