package com.syou.gitstathub.service;

import com.syou.gitstathub.model.User;
import com.syou.gitstathub.repository.UserRepository;
import com.syou.gitstathub.util.JwtUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("ユーザーが見つかりません：" + username));
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }
        return JwtUtil.generateToken(user.getUsername());
    }
}
