package com.syou.gitstathub.repository;

import com.syou.gitstathub.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
}
