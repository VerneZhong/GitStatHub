package com.syou.gitstathub.service;

import com.syou.gitstathub.model.User;
import com.syou.gitstathub.repository.UserRepository;
import com.syou.gitstathub.request.RegisterRequest;
import com.syou.gitstathub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;

/**
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
@Service
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("ユーザーが見つかりません：" + username));
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return JwtUtil.generateToken(user.getUsername());
    }

    public void register(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setAccountNonExpired(1);
        user.setAccountNonLocked(1);
        user.setEnabled(1);
        userRepository.save(user);
    }
}
