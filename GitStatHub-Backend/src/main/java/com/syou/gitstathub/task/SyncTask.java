package com.syou.gitstathub.task;

import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.repository.RepoInfoRepository;
import com.syou.gitstathub.service.GitHubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * sync task
 *
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Component
@Slf4j
public class SyncTask {

    private final GitHubService gitHubService;
    private final RepoInfoRepository repoInfoRepository;

    public SyncTask(GitHubService gitHubService, RepoInfoRepository repoInfoRepository) {
        this.gitHubService = gitHubService;
        this.repoInfoRepository = repoInfoRepository;
    }

    /**
     * 時間毎に実行される
     *
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void syncGitHubData() {
        log.info("[SyncTask] GitHubデータの同期を開始...");

        List<RepoInfo> repos = gitHubService.fetchUserRepos();
        if (repos != null && !repos.isEmpty()) {
            // 数据库里已有的记录
            Map<String, RepoInfo> existingRepos = repoInfoRepository.findAll().stream().collect(Collectors.toMap(RepoInfo::getName, Function.identity()));
            List<RepoInfo> toSave = new ArrayList<>();
            for (RepoInfo repo : repos) {
                RepoInfo existing = existingRepos.get(repo.getName());
                if (existing != null) {
                    existing.setDescription(repo.getDescription());
                    existing.setLanguage(repo.getLanguage());
                    existing.setStargazersCount(repo.getStargazersCount());
                    existing.setUpdatedAt(repo.getUpdatedAt());
                    toSave.add(existing);
                } else {
                    // 新記録
                    repo.setId(null);
                    toSave.add(repo);
                }
            }
            // 一次性批量保存
            repoInfoRepository.saveAll(toSave);
        }
        log.info("[SyncTask] GitHubデータの同期が完了しました。");
    }
}
