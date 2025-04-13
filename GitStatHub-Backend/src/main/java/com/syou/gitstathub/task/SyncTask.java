package com.syou.gitstathub.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Component
public class SyncTask {
    // 毎日午前3時に実行される例
    @Scheduled(cron = "0 0 3 * * *")
    public void syncGitHubData() {
        // TODO: GitHub APIを呼び出してDBに同期する処理を追加
        System.out.println("[SyncTask] GitHubデータの同期を実行しました。");
    }
}
