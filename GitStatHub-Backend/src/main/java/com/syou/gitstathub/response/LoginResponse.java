package com.syou.gitstathub.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
@Data
public class LoginResponse extends BaseResponse {
    private String token;

    public LoginResponse(String token) {
        super();
        this.token = token;
    }
}
