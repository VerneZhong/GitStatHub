package com.syou.gitstathub.service;

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

    Map<String, Object> getContributions(String username);
}
