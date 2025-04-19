package com.syou.gitstathub.controller;

import com.syou.gitstathub.service.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author verne.zhong
 * @date 2025/04/17
 * @description
 */
@RestController
@RequestMapping("/api/github")
public class ContributionController {

    private final GitHubService gitHubService;

    public ContributionController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/contributions/{username}")
    public ResponseEntity<Map<String, Object>> getContributions(@PathVariable String username) {
        Map<String, Object> result = gitHubService.getContributions(username);
        return ResponseEntity.ok(result);
    }
}
