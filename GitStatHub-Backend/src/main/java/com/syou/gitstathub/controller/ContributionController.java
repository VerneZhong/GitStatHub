package com.syou.gitstathub.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.syou.gitstathub.service.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * github 贡献统计控制器
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
        Map<String, Object> result;
        try {
            result = gitHubService.getContributions(username);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/contributionsByYear")
    public Map<String, Object> getContributionsByYear(
            @RequestParam String username,
            @RequestParam(required = false) Integer year
    ) throws JsonProcessingException {
        if (year == null) {
            year = java.time.Year.now().getValue();
        }
        return gitHubService.getContributionsByYear(username, year);
    }
}
