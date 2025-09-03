package com.syou.gitstathub.task;

import com.syou.gitstathub.service.RepoSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    private final RepoSyncService repoSyncService;

    public SyncTask(RepoSyncService repoSyncService) {
        this.repoSyncService = repoSyncService;
    }

    /**
     * 時間毎に実行される
     *
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledRepoSync() {
        log.info("[SyncTask] GitHubデータの同期を開始...");
        repoSyncService.syncUserRepos();
        log.info("[SyncTask] GitHubデータの同期が完了しました。");
    }
}
