package com.syou.gitstathub.config;

import com.syou.gitstathub.model.ContributionDay;
import com.syou.gitstathub.model.ContributionStats;
import com.syou.gitstathub.model.ContributionWeek;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author verne.zhong
 * @date 2025/05/04
 * @description
 */
@Component
public class ContributionResolver {
    @QueryMapping
    public ContributionStats contributions(@Argument String username) {
        // 模拟获取过去一年的贡献数据（按周分组）
        List<ContributionWeek> weeks = new ArrayList<>();

        LocalDate start = LocalDate.now().minusWeeks(52).with(java.time.DayOfWeek.SUNDAY);
        for (int i = 0; i < 53; i++) {
            ContributionWeek week = new ContributionWeek();
            List<ContributionDay> days = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                LocalDate date = start.plusDays(i * 7L + j);
                int count = (int) (Math.random() * 10); // 可替换为 GitHub 查询数据
                days.add(new ContributionDay(date.toString(), count));
            }
            week.setContributionDays(days);
            weeks.add(week);
        }

        // 总和计算
        int total = weeks.stream()
                .flatMap(w -> w.getContributionDays().stream())
                .mapToInt(ContributionDay::getContributionCount)
                .sum();

        int currentMonth = weeks.stream()
                .flatMap(w -> w.getContributionDays().stream())
                .filter(d -> LocalDate.parse(d.getDate()).getMonthValue() == LocalDate.now().getMonthValue())
                .mapToInt(ContributionDay::getContributionCount)
                .sum();

        ContributionStats stats = new ContributionStats();
        stats.setWeeks(weeks);
        stats.setTotalContributions(total);
        stats.setCurrentMonthContributions(currentMonth);
        return stats;
    }
}
