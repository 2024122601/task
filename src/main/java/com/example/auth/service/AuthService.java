package com.example.auth.service;

import com.example.auth.domain.Role;
import com.example.auth.domain.User;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.SignupRequest;
import com.example.auth.dto.TokenResponse;
import com.example.auth.dto.UserResponse;
import com.example.auth.exception.CustomException;
import com.example.auth.exception.ErrorCode;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // 회원가입
    public UserResponse signup(SignupRequest request) {
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = new User(request.getUsername(), request.getPassword(), request.getNickname());
        userRepository.save(user);

        return new UserResponse(user.getUsername(), user.getNickname(), user.getRoles().stream().toList());
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user);
        return new TokenResponse(token);
    }

    // 관리자 권한 부여
    public UserResponse grantAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.addRole(Role.ADMIN);
        return new UserResponse(user.getUsername(), user.getNickname(), user.getRoles().stream().toList());
    }
}
