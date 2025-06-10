package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT 토큰 응답 DTO")
public class TokenResponse {
    @Schema(description = "발급된 JWT 액세스 토큰")
    private String accessToken;
}
