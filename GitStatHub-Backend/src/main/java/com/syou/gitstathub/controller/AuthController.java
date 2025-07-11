package com.syou.gitstathub.controller;

import com.syou.gitstathub.request.LoginRequest;
import com.syou.gitstathub.request.RegisterRequest;
import com.syou.gitstathub.response.BaseResponse;
import com.syou.gitstathub.response.LoginResponse;
import com.syou.gitstathub.response.ResultVo;
import com.syou.gitstathub.response.UserInfoResponse;
import com.syou.gitstathub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * login 控制器
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return ResponseEntity.ok(new BaseResponse());
    }

    @PostMapping("/checkUsername")
    public ResponseEntity<ResultVo<Boolean>> checkUsername(String username) {
        return ResponseEntity.ok(ResultVo.success(authService.checkUsername(username)));
    }

    @GetMapping("/getUserInfo/{username}")
    public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(authService.getUserInfo(username));
    }
}
