package com.syou.gitstathub.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/05/17
 * @description
 */
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    @JsonProperty("account_non_expired")
    private int accountNonExpired;
    @JsonProperty("account_non_locked")
    private int accountNonLocked;
    @JsonProperty("credentials_non_expired")
    private int credentialsNonExpired;
    private int enabled;
    private String email;
    @JsonProperty("git_account")
    private String gitAccount;
}
