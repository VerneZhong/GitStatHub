package com.syou.gitstathub.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Data
public class UserInfo {
    private String login;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    private String bio;
    private int followers;
    @JsonProperty("public_repos")
    private int publicRepos;
}
