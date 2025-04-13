package com.syou.gitstathub.task;

import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.repository.RepoInfoRepository;
import com.syou.gitstathub.service.GitHubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
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

    // 毎日午前3時に実行される例
    @Scheduled(cron = "0 0 3 * * *")
    public void syncGitHubData() {
        log.info("[SyncTask] GitHubデータの同期を開始...");

        List<RepoInfo> repos = gitHubService.fetchUserRepos();
        if (repos != null) {
            for (RepoInfo repo : repos) {
                // 根据名称或其他唯一标识符查找
                Optional<RepoInfo> existingRepo = repoInfoRepository.findByName(repo.getName());
                if (existingRepo.isPresent()) {
                    // 更新现有记录的属性
                    RepoInfo existing = existingRepo.get();
                    existing.setDescription(repo.getDescription());
                    existing.setLanguage(repo.getLanguage());
                    existing.setStargazersCount(repo.getStargazersCount());
                    existing.setUpdatedAt(repo.getUpdatedAt());
                    repoInfoRepository.save(existing);
                } else {
                    // 新记录
                    repo.setId(null); // 确保ID为空，让数据库生成
                    repoInfoRepository.save(repo);
                }
            }
        }
        log.info("[SyncTask] GitHubデータの同期が完了しました。");
    }
}
