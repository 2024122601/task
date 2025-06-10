package com.example.auth.dto;

import com.example.auth.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Schema(description = "사용자 응답 DTO")
public class UserResponse {

    @Schema(description = "사용자 이름", example = "johndoe")
    private String username;

    @Schema(description = "사용자 닉네임", example = "존도우")
    private String nickname;

    @Schema(description = "역할 목록")
    private List<RoleDto> roles;

    public UserResponse(String username, String nickname, List<Role> roles) {
        this.username = username;
        this.nickname = nickname;
        this.roles = roles.stream()
                .map(role -> new RoleDto(role.name()))
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    @Schema(description = "역할 DTO")
    public static class RoleDto {
        @Schema(description = "역할 이름", example = "ROLE_ADMIN")
        private String role;
    }
}

