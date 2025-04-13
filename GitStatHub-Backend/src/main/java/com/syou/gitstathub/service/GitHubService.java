package com.syou.gitstathub.service;

import com.syou.gitstathub.model.RepoInfo;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
public interface GitHubService {
    List<RepoInfo> fetchUserRepos();
}
