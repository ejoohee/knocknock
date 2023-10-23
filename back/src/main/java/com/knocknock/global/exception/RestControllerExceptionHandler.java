package com.knocknock.global.exception;

import com.knocknock.global.dto.MessageDto;
import com.knocknock.global.exception.exception.TokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class RestControllerExceptionHandler {

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<MessageDto> handleTokenException(TokenException tokenException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(MessageDto.message(tokenException.getMessage()));
    }

}
