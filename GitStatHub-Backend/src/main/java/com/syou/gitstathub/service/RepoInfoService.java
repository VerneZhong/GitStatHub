package com.syou.gitstathub.service;

import com.syou.gitstathub.model.RepoInfo;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/04/14
 * @description
 */
public interface RepoInfoService {
    List<RepoInfo> getUserRepos();
}
