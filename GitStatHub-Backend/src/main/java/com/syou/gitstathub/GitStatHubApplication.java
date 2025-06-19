package com.syou.gitstathub;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@SpringBootApplication
@EnableScheduling
public class GitStatHubApplication {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();
        // 注册到系统环境变量，供 Spring 使用
        System.setProperty("GITHUB_TOKEN", dotenv.get("GITHUB_TOKEN"));
        System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        SpringApplication.run(GitStatHubApplication.class, args);
    }
}
