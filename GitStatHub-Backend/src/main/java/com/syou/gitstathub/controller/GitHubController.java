package com.syou.gitstathub.controller;

import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.service.GitHubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@RestController
@RequestMapping("/api/github")
public class GitHubController {
    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/repos")
    public List<RepoInfo> getRepos() {
        return gitHubService.fetchUserRepos();
    }
}
