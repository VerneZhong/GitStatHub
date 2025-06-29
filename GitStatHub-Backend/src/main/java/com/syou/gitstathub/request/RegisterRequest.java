package com.syou.gitstathub.request;

import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/06/08
 * @description
 */
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String gitAccount;
}
