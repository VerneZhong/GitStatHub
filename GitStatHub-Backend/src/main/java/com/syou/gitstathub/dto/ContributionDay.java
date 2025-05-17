package com.syou.gitstathub.dto;

import lombok.Data;

/**
 * @author verne.zhong
 * @date 2025/05/04
 * @description
 */
@Data
public class ContributionDay {
    private String date;
    private int contributionCount;

    public ContributionDay(String date, int contributionCount) {
        this.date = date;
        this.contributionCount = contributionCount;
    }
}
