package com.syou.gitstathub.dto;

import com.syou.gitstathub.model.RepoInfo;
import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/04/14
 * @description
 */
@Data
public class RepoDto {
    private Long id;
    private String name;
    private String description;
    private String language;
    private int stargazersCount;
    private String updatedAt;

    public static RepoDto buildDto(RepoInfo repoInfo) {
        RepoDto repoDto = new RepoDto();
        repoDto.id = repoInfo.getId();
        repoDto.name = repoInfo.getName();
        repoDto.description = repoInfo.getDescription();
        repoDto.language = repoInfo.getLanguage();
        repoDto.stargazersCount = repoInfo.getStargazersCount();
        repoDto.updatedAt = repoInfo.getUpdatedAt();
        return repoDto;
    }
}
