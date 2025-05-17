package com.syou.gitstathub.request;

import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
