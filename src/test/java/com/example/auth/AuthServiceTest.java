package com.example.auth;

import com.example.auth.domain.Role;
import com.example.auth.domain.User;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.SignupRequest;
import com.example.auth.repository.InMemoryUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.clear();
    }

    @Test
    void signup_Success() throws Exception {
        SignupRequest request = new SignupRequest("user1", "pass", "nick1");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void signup_DuplicateUsername() throws Exception {
        SignupRequest request = new SignupRequest("user1", "pass", "nick1");

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("USER_ALREADY_EXISTS"));
    }

    @Test
    void login_Success() throws Exception {
        SignupRequest signup = new SignupRequest("loginUser", "pass", "nick");
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("loginUser", "pass");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void login_InvalidPassword() throws Exception {
        SignupRequest signup = new SignupRequest("userX", "pass", "nick");
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("userX", "wrong");
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void grantAdminRole_Success() throws Exception {
        // 1. 회원가입
        SignupRequest signup = new SignupRequest("adminuser", "password", "admin");
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        // 2. 회원가입 직후 메모리 저장소에서 사용자 꺼내 ADMIN 역할 추가
        User adminUser = userRepository.findByUsername("adminuser").orElseThrow();
        adminUser.getRoles().add(Role.ADMIN);
        userRepository.save(adminUser);

        // 3. 로그인해서 JWT 토큰 받기
        LoginRequest login = new LoginRequest("adminuser", "password");
        String tokenJson = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(tokenJson).get("accessToken").asText();

        // 4. ADMIN 권한이 필요한 엔드포인트 호출 테스트
        mockMvc.perform(patch("/admin/users/1/roles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[*].role", hasItem("ADMIN")));
    }

    @Test
    void grantAdminRole_UnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(patch("/admin/users/1/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void grantAdminRole_ForbiddenForNormalUser() throws Exception {
        // 1. 일반 사용자 회원가입 및 로그인
        SignupRequest signup = new SignupRequest("normalUser", "password", "normal");
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("normalUser", "password");
        String tokenJson = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(tokenJson).get("accessToken").asText();

        // 2. ADMIN 권한 API 호출 시도 -> 403 Forbidden 예상
        mockMvc.perform(patch("/admin/users/1/roles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void grantAdminRole_NonExistentUser() throws Exception {
        // 1. 관리자 권한 사용자 회원가입, 권한 부여, 로그인 (준비)
        SignupRequest signup = new SignupRequest("adminUser", "password", "admin");
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signup)))
                .andExpect(status().isCreated());

        User adminUser = userRepository.findByUsername("adminUser").orElseThrow();
        adminUser.getRoles().add(Role.ADMIN);
        userRepository.save(adminUser);

        LoginRequest login = new LoginRequest("adminUser", "password");
        String tokenJson = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(tokenJson).get("accessToken").asText();

        // 2. 존재하지 않는 사용자 ID로 ADMIN 권한 부여 시도 -> 404 Not Found 혹은 적절한 에러 예상
        mockMvc.perform(patch("/admin/users/999/roles")  // 999 같은 없는 유저 ID
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"));
    }
}
