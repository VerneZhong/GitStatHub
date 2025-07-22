package com.syou.gitstathub.config;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/07/15
 * @description
 */
public class Constants {
    public static final String JSON = "application/json";
    public static final String JWT_UNAUTHORIZED = "{\"error\": \"Unauthorized\"}";
    public static final String REQUEST_MATCHES = "/api/auth/**";
    public static final String WEB_URL = "http://localhost:5173";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";
    public static final List<String> ALLOWED_METHODS = List.of(GET, POST, PUT, DELETE, OPTIONS);
    public static final String GITHUB_USER_URL = "https://api.github.com/users/";
    public static final String GITHUB_REPO_LANG_URL = "https://api.github.com/repos/%s/%s/languages";
    public static final String GITHUB_GRAPHQL_URL = "https://api.github.com/graphql";
    public static final String UN_KNOWN = "Unknown";
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCEPT = "Accept";
    public static final String JSON_TYPE = "application/vnd.github+json";
    public static final String BEARER = "Bearer ";
}
