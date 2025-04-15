package com.syou.gitstathub.response;

import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/04/14
 * @description
 */
@Data
public class ResultVo<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ResultVo<T> success(T data) {
        ResultVo<T> resultVo = new ResultVo<>();
        resultVo.setCode(200);
        resultVo.setMessage("success");
        resultVo.setData(data);
        return resultVo;
    }
}
