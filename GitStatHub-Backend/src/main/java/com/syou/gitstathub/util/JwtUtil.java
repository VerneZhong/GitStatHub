package com.syou.gitstathub.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * jwt util
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
@Component
public class JwtUtil {

    // 密钥必须足够长（建议至少256位：32字符）
    @Value("${jwt.secret}")
    private String secret;

    private static final SecureDigestAlgorithm<SecretKey, ?> ALGORITHM = Jwts.SIG.HS256;

    // token 有效时间（毫秒）=> 1 小时
    private static final long EXPIRATION_TIME = 120 * 60 * 1000;

    // 创建签名 key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 生成 Token
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), ALGORITHM)
                .compact();
    }

    // 解析 Token
    public Claims parseToken(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 从 Token 中获取用户名
    public String getUsernameFromToken(String token) {
        try {
            return parseToken(token).getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    // 判断 token 是否过期
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
