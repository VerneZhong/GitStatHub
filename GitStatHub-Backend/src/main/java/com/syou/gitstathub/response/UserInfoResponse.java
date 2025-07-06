package com.syou.gitstathub.response;

import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/07/06
 * @description
 */
@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String gitAccount;
}
