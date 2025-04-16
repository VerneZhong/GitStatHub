package com.syou.gitstathub.service;

import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.repository.RepoInfoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/04/14
 * @description
 */
@Service
public class RepoInfoServiceImpl implements RepoInfoService {

    private final RepoInfoRepository repoInfoRepository;

    public RepoInfoServiceImpl(RepoInfoRepository repoInfoRepository) {
        this.repoInfoRepository = repoInfoRepository;
    }

    @Override
    public List<RepoInfo> getUserRepos() {
        return repoInfoRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
    }
}
