package com.syou.gitstathub.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Entity
@Data
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String login;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    private String bio;
    private int followers;
    @JsonProperty("public_repos")
    private int publicRepos;
}
