package com.syou.gitstathub.dto;

import lombok.Data;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/05/04
 * @description
 */
@Data
public class ContributionWeek {
    private List<ContributionDay> contributionDays;
}
