package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {
    @Schema(description = "사용자 이름", example = "user1")
    private String username;

    @Schema(description = "비밀번호", example = "password123")
    private String password;
}
