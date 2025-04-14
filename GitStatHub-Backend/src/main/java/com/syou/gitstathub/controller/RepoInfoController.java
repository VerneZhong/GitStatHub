package com.syou.gitstathub.controller;

import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.service.RepoInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/04/14
 * @description
 */
@RestController
@RequestMapping("/api/repo")
public class RepoInfoController {
    private final RepoInfoService repoInfoService;

    public RepoInfoController(RepoInfoService repoInfoService) {
        this.repoInfoService = repoInfoService;
    }

    @GetMapping("/repos")
    public List<RepoInfo> getRepos() {
        return repoInfoService.getUserRepos();
    }
}
