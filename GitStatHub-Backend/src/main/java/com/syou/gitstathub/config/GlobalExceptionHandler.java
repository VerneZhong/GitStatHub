package com.syou.gitstathub.config;

import com.syou.gitstathub.response.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * global exception handler
 * @author verne.zhong
 * @date 2025/06/15
 * @description
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo<?> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: ", ex);
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ResultVo.error(400, errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResultVo<?> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: ", ex);
        return ResultVo.error(400, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResultVo<?> handleException(Exception ex) {
        log.error("Exception: ", ex);
        return ResultVo.error(500, "システムエラーが発生しました：" + ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResultVo<String> handleAccessDenied(AccessDeniedException ex) {
        log.error("AccessDeniedException: ", ex);
        return ResultVo.error(403, "アクセス権限がありません");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResultVo<String> handleResourceAccessException(ResourceAccessException ex) {
        log.error("ResourceAccessException: ", ex);
        return ResultVo.error(HttpStatus.GATEWAY_TIMEOUT.value(), ex.getMessage());
    }
}
