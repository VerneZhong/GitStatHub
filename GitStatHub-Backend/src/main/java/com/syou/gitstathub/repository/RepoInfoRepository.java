package com.syou.gitstathub.repository;

import com.syou.gitstathub.model.RepoInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
public interface RepoInfoRepository extends JpaRepository<RepoInfo, Long> {

}
