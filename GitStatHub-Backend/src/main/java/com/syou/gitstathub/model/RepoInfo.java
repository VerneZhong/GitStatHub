package com.syou.gitstathub.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Data
@Entity
public class RepoInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String language;
    @JsonProperty("stargazers_count")
    private int stargazersCount;
    @JsonProperty("updated_at")
    private String updatedAt;
    @Version
    private Long version;
}
