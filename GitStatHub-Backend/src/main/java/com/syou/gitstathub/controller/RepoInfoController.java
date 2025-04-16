package com.syou.gitstathub.controller;

import com.syou.gitstathub.dto.RepoDto;
import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.response.ResultVo;
import com.syou.gitstathub.service.RepoInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/04/14
 * @description
 */
@RestController
@RequestMapping("/api/repo")
public class RepoInfoController {
    private final RepoInfoService repoInfoService;

    public RepoInfoController(RepoInfoService repoInfoService) {
        this.repoInfoService = repoInfoService;
    }

    @GetMapping("/repos")
    public ResultVo<List<RepoDto>> getRepos() {
        List<RepoInfo> userRepos = repoInfoService.getUserRepos();
        List<RepoDto> repoDtoList = userRepos.stream().map(RepoDto::buildDto).toList();
        return ResultVo.success(repoDtoList);
    }
}
