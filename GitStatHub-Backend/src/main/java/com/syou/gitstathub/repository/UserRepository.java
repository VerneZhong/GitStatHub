package com.syou.gitstathub.repository;

import com.syou.gitstathub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
