package com.syou.gitstathub.util;

import org.springframework.http.HttpHeaders;

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
        headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/vnd.github+json");
        return headers;
    }
}
