package com.syou.gitstathub.util;

import com.syou.gitstathub.model.RepoInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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
        List<RepoInfo> allRepos = new ArrayList<>();
        int page = 1;
        RepoInfo[] reposOnPage;

        do {
            String url = "https://api.github.com/users/" + username + "/repos?page=" + page + "&per_page=100";
            reposOnPage = restTemplate.getForObject(url, RepoInfo[].class);

            if (reposOnPage.length > 0) {
                allRepos.addAll(Arrays.asList(reposOnPage));
                page++;
            } else {
                break;
            }
        } while (reposOnPage.length == 100);

        return allRepos;
    }
}
