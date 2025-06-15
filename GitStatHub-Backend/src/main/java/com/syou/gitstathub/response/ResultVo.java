package com.syou.gitstathub.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author verne.zhong
 * @date 2025/04/14
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVo<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ResultVo<T> success(T data) {
        return new ResultVo<>(200, "success", data);
    }

    public static <T> ResultVo<T> error(String message) {
        return new ResultVo<>(500, message, null);
    }

    public static <T> ResultVo<T> error(int code, String message) {
        return new ResultVo<>(code, message, null);
    }
}