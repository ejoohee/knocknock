package com.knocknock.global.exception;

import com.knocknock.global.dto.MessageDto;
import com.knocknock.global.exception.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 401, 404 제외한 모든 Exception BAD_REQUEST(400)
 */
@RestControllerAdvice(annotations = RestController.class)
public class RestControllerExceptionHandler {

    /**
     * 토큰 관련 에러 처리(401)
     * @param tokenException
     */
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<MessageDto> handleTokenException(TokenException tokenException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                .body(MessageDto.message(tokenException.getMessage()));
    }

    /**
     * 토큰 인증은 됐으나 해당 유저에게 해당 메서드 사용 권한이 없을 떄 처리(401)
     * (일반적으로 관리자 전용 메서드를 일반 유저가 사용할 떄)
     * @param authenticationException
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<MessageDto> handleAuthenticationException(AuthenticationException authenticationException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // 401
                .body(MessageDto.message(authenticationException.getMessage()));
    }

    /**
     * 원하는 내용을 찾지 못했을 때 처리(404)
     * @param notFoundException
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageDto> handleNotFoundException(NotFoundException notFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
                .body(MessageDto.message(notFoundException.getMessage()));
    }

    /**
     * 그 외 처리 -> BAD_REQUEST(400)
     * @param badRequestException
     * @return
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<MessageDto> handleBadRequestException(BadRequestException badRequestException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 아래꺼랑 동일한건데 따로 작성
                .body(MessageDto.message(badRequestException.getMessage()));
    }

    /**
     * (혹시몰라 작성)
     * @param illegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageDto> handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400
                .body(MessageDto.message(illegalArgumentException.getMessage()));
    }

}
