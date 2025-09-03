package com.syou.gitstathub.service;

import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.repository.RepoInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * sync implementation
 * @author verne.zhong
 * @date 2025/09/03
 * @description
 */
@Service
public class RepoSyncService {
    private final GitHubService gitHubService;
    private final RepoInfoRepository repoInfoRepository;

    public RepoSyncService(GitHubService gitHubService, RepoInfoRepository repoInfoRepository) {
        this.gitHubService = gitHubService;
        this.repoInfoRepository = repoInfoRepository;
    }

    @Transactional
    public void syncUserRepos() {
        List<RepoInfo> repos = gitHubService.fetchUserRepos();
        if (repos != null && !repos.isEmpty()) {
            // 数据库里已有的记录
            Map<String, RepoInfo> existingRepos = repoInfoRepository.findAll().stream().collect(Collectors.toMap(RepoInfo::getName, Function.identity()));
            for (RepoInfo repo : repos) {
                RepoInfo existing = existingRepos.get(repo.getName());
                if (existing != null) {
                    existing.setDescription(repo.getDescription());
                    existing.setLanguage(repo.getLanguage());
                    existing.setStargazersCount(repo.getStargazersCount());
                    existing.setUpdatedAt(repo.getUpdatedAt());
                } else {
                    // 新記録
                    repo.setId(null);
                    repoInfoRepository.save(repo);
                }
            }
        }
    }
}
