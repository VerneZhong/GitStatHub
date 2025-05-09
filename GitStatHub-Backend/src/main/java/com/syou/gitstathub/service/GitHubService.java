package com.syou.gitstathub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.syou.gitstathub.model.RepoInfo;

import java.util.List;
import java.util.Map;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
public interface GitHubService {
    List<RepoInfo> fetchUserRepos();

    Map<String, Object> getContributions(String username) throws JsonProcessingException;

    Map<String, Object> getContributionsByYear(String username, Integer year) throws JsonProcessingException;
}
