package com.syou.gitstathub.service;

import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.util.GitHubApiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Service
public class GitHubServiceImpl implements GitHubService {
    @Value("${github.username}")
    private String username;

    @Autowired
    private GitHubApiHelper gitHubApiHelper;

    @Override
    public List<RepoInfo> fetchUserRepos() {
        return gitHubApiHelper.getUserRepos(username);
    }
}
