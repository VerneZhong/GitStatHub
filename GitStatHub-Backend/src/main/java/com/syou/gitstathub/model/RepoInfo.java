package com.syou.gitstathub.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Data
public class RepoInfo {
    private String name;
    private String description;
    private String language;
    @JsonProperty("stargazers_count")
    private int stargazersCount;
    @JsonProperty("updated_at")
    private String updatedAt;
}
