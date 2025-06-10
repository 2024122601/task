package com.example.auth.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> createErrorResponse(String code, String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        errorResponse.put("error", error);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        return createErrorResponse(ex.getReason(), getErrorMessage(ex.getReason()), (HttpStatus) ex.getStatusCode());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return createErrorResponse("ACCESS_DENIED", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({JwtException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleJwtException(Exception ex, HttpServletRequest request) {
        return createErrorResponse("INVALID_TOKEN", "유효하지 않은 인증 토큰입니다.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return createErrorResponse(errorCode.name(), errorCode.getMessage(), errorCode.getStatus());
    }

    private String getErrorMessage(String code) {
        return switch (code) {
            case "USER_ALREADY_EXISTS" -> "이미 가입된 사용자입니다.";
            case "INVALID_CREDENTIALS" -> "아이디 또는 비밀번호가 올바르지 않습니다.";
            case "ACCESS_DENIED" -> "접근 권한이 없습니다.";
            case "INVALID_TOKEN" -> "유효하지 않은 인증 토큰입니다.";
            case "USER_NOT_FOUND" -> "사용자를 찾을 수 없습니다.";
            default -> "예상치 못한 오류가 발생했습니다.";
        };
    }
}

