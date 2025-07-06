package com.syou.gitstathub.service;

import com.syou.gitstathub.model.User;
import com.syou.gitstathub.repository.UserRepository;
import com.syou.gitstathub.request.RegisterRequest;
import com.syou.gitstathub.response.UserInfoResponse;
import com.syou.gitstathub.util.JwtUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * ユーザログイン
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("ユーザーが見つかりません：" + username));
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtUtil.generateToken(user.getUsername());
    }

    /**
     * ユーザ登録
     * @param registerRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setAccountNonExpired(1);
        user.setAccountNonLocked(1);
        user.setEnabled(1);
        user.setGitAccount(registerRequest.getGitAccount());
        userRepository.save(user);
    }

    /**
     * ユーザ名の重複チェック
     * @param username ユーザ名
     * @return true　ユーザ名の重複なし false　ユーザ名の重複あり
     */
    public boolean checkUsername(String username) {
        return userRepository.findByUsername(username).isEmpty();
    }

    public UserInfoResponse getUserInfo(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        if (byUsername.isPresent()) {
            User user = byUsername.get();
            userInfoResponse.setId(user.getId());
            userInfoResponse.setUsername(user.getUsername());
            userInfoResponse.setGitAccount(user.getGitAccount());
        }
        return userInfoResponse;
    }
}
