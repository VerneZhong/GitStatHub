package com.syou.gitstathub.service;

import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.model.RepositoryInfo;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/04/14
 * @description
 */
public interface RepoInfoService {
    /**
     * 从DB中获取所有仓库信息
     * @return
     */
    List<RepoInfo> getUserRepos();

    /**
     * 通过GitHub API查询指定用户的仓库信息
     * @param username 指定GitHub的用户名
     * @return
     */
    List<RepositoryInfo> queryRepoInfo(String username);
}
