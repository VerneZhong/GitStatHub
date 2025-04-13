package com.syou.gitstathub.util;

import com.syou.gitstathub.model.RepoInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Component
public class GitHubApiHelper {
    private final RestTemplate restTemplate;

    public GitHubApiHelper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepoInfo> getUserRepos(String username) {
        String url = "https://api.github.com/users/" + username + "/repos";
        RepoInfo[] repos = restTemplate.getForObject(url, RepoInfo[].class);
        return Arrays.asList(repos);
    }
}
