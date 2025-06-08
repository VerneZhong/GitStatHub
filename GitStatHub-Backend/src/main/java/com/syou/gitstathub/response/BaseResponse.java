package com.syou.gitstathub.response;

import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/06/08
 * @description
 */
@Data
public class BaseResponse {
    private int code;
    private String message;

    public BaseResponse() {
        this.code = 200;
        this.message = "success";
    }
}
