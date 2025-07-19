package com.syou.gitstathub.service;

import com.syou.gitstathub.config.Constants;
import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.model.RepositoryInfo;
import com.syou.gitstathub.repository.RepoInfoRepository;
import com.syou.gitstathub.util.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 仓库信息服务实现类
 * @author verne.zhong
 * @date 2025/04/14
 * @description
 */
@Service
public class RepoInfoServiceImpl implements RepoInfoService {

    @Value("${github.token}")
    private String githubToken;

    private final RepoInfoRepository repoInfoRepository;

    private final RestTemplate restTemplate;

    public RepoInfoServiceImpl(RepoInfoRepository repoInfoRepository, RestTemplate restTemplate) {
        this.repoInfoRepository = repoInfoRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * 从db中获取仓库信息
     * @return
     */
    @Override
    public List<RepoInfo> getUserRepos() {
        return repoInfoRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
    }

    @Override
    public List<RepositoryInfo> queryRepoInfo(String username) {
        List<RepositoryInfo> allRepos = new ArrayList<>();
        int page = 1;
        RepositoryInfo[] reposOnPage;
        HttpEntity<String> entity = new HttpEntity<>(HttpUtil.createHeaders(githubToken));

        do {
            String url = Constants.GITHUB_USER_URL + username + "/repos?page=" + page + "&per_page=100";
            ResponseEntity<RepositoryInfo[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, RepositoryInfo[].class);
            reposOnPage = response.getBody();
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
