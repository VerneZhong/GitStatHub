package com.syou.gitstathub.util;

import com.syou.gitstathub.config.Constants;
import org.springframework.http.*;

import java.util.Map;

/**
 * @author verne.zhong
 * @date 2025/05/24
 * @description
 */
public class HttpUtil {
    /**
     * 构造用于调用 GitHub API 的请求头，包含认证信息。
     * 通常需要在请求中加入 GitHub 的个人访问令牌（Personal Access Token）。
     *
     * @return 包含认证信息的 HttpHeaders 对象
     */
    public static HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Constants.AUTHORIZATION, Constants.BEARER + token);
        headers.set(Constants.ACCEPT, Constants.JSON_TYPE);
        return headers;
    }

    public static HttpEntity<Map<String, Object>> createHttpEntity(String token, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(body, headers);
    }
}
