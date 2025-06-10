package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "에러 응답 DTO")
public class ErrorResponse {
    private Error error;

    @Data
    @AllArgsConstructor
    @Schema(description = "에러 상세 정보")
    public static class Error {
        @Schema(description = "에러 코드", example = "INVALID_REQUEST")
        private String code;

        @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
        private String message;
    }
}
