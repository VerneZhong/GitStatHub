package com.syou.gitstathub.dto;

import lombok.Data;

import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/05/04
 * @description
 */
@Data
public class ContributionStats {
    private int totalContributions;
    private int currentMonthContributions;
    private List<ContributionWeek> weeks;
}
