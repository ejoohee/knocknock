package com.knocknock.global.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDto {

    String message;

    public static MessageDto message(String message) {
        return MessageDto.builder()
                .message(message)
                .build();
    }
}
