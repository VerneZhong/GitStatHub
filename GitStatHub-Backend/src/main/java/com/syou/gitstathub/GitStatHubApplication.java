package com.syou.gitstathub;

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
        SpringApplication.run(GitStatHubApplication.class, args);
    }
}
