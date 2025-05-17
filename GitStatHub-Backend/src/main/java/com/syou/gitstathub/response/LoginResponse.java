package com.syou.gitstathub.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
}
