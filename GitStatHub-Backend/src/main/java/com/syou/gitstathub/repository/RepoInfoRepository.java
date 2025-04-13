package com.syou.gitstathub.repository;

import com.syou.gitstathub.model.RepoInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
public interface RepoInfoRepository extends JpaRepository<RepoInfo, Long> {
    Optional<RepoInfo> findByName(String name);

    List<RepoInfo> findByLanguage(String language);
    List<RepoInfo> findByStargazersCountGreaterThan(int count);
}
