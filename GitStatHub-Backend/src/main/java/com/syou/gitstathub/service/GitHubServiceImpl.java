package com.syou.gitstathub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.util.GitHubApiHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GitHub service
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Service
public class GitHubServiceImpl implements GitHubService {
    @Value("${github.username}")
    private String username;

    @Value("${github.token}")
    private String githubToken;

    private final GitHubApiHelper gitHubApiHelper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GitHubServiceImpl(GitHubApiHelper gitHubApiHelper, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.gitHubApiHelper = gitHubApiHelper;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<RepoInfo> fetchUserRepos() {
        return gitHubApiHelper.getUserRepos(username);
    }

    @Override
    public Map<String, Object> getContributions(String username) throws JsonProcessingException {
        String url = "https://api.github.com/graphql";
        Map<String, Object> body = getStringObjectMap(username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode calendarNode = root.path("data")
                .path("user")
                .path("contributionsCollection")
                .path("contributionCalendar");

        JsonNode weeksNode = calendarNode.path("weeks");
        Integer totalContributions = calendarNode.path("totalContributions").asInt();

        List<Map<String, Object>> weeks = objectMapper.convertValue(weeksNode, new TypeReference<>() {
        });

        // Calculate current month contributions
        int currentMonthContributions = calculateCurrentMonthContributions(weeks);

        return Map.of(
                "weeks", weeks,
                "totalContributions", totalContributions,
                "currentMonthContributions", currentMonthContributions
        );
    }

    private static Map<String, Object> getStringObjectMap(String username) {
        String query = """
                    query($login: String!) {
                      user(login: $login) {
                        name
                        contributionsCollection {
                          contributionCalendar {
                            totalContributions
                            weeks {
                              contributionDays {
                                color
                                contributionCount
                                date
                                weekday
                              }
                            }
                          }
                        }
                      }
                    }
                """;

        Map<String, Object> variables = new HashMap<>();
        variables.put("login", username);

        Map<String, Object> body = new HashMap<>();
        body.put("query", query);
        body.put("variables", variables);
        return body;
    }

    private int calculateCurrentMonthContributions(List<Map<String, Object>> weeks) {
        int count = 0;
        Calendar currentCal = Calendar.getInstance();
        int currentMonth = currentCal.get(Calendar.MONTH);
        int currentYear = currentCal.get(Calendar.YEAR);

        for (Map<String, Object> week : weeks) {
            List<Map<String, Object>> days = (List<Map<String, Object>>) week.get("contributionDays");
            for (Map<String, Object> day : days) {
                String dateStr = (String) day.get("date");
                LocalDate date = LocalDate.parse(dateStr);

                if (date.getMonthValue() - 1 == currentMonth && date.getYear() == currentYear) {
                    count += ((Integer) day.get("contributionCount"));
                }
            }
        }

        return count;
    }

    /**
     * 根据年份获取贡献数据
     * @param username
     * @param year
     * @return
     * @throws JsonProcessingException
     */
    @Override
    public Map<String, Object> getContributionsByYear(String username, Integer year) throws JsonProcessingException {
        // 时间范围
        String from = year + "-01-01T00:00:00Z";
        String to = year + "-12-31T23:59:59Z";

        // GraphQL 查询语句
        String query = """
        query ($username: String!, $from: DateTime!, $to: DateTime!) {
          user(login: $username) {
            contributionsCollection(from: $from, to: $to) {
              contributionCalendar {
                totalContributions
                weeks {
                  contributionDays {
                    color
                    contributionCount
                    date
                    weekday
                  }
                }
              }
            }
          }
        }
        """;

        // 构建 GraphQL 请求体
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> request = new HashMap<>();
        request.put("query", query);
        request.put("variables", Map.of(
                "username", username,
                "from", from,
                "to", to
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(request), headers);

        // 请求 GitHub GraphQL
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                "https://api.github.com/graphql",
                entity,
                JsonNode.class
        );

        // 提取数据
        JsonNode root = response.getBody();
        JsonNode calendar = root
                .path("data")
                .path("user")
                .path("contributionsCollection")
                .path("contributionCalendar");

        JsonNode weeks = calendar.path("weeks");
        int total = calendar.path("totalContributions").asInt();

        // 本月贡献数
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        int thisMonthCount = 0;

        for (JsonNode week : weeks) {
            for (JsonNode day : week.path("contributionDays")) {
                LocalDate date = LocalDate.parse(day.path("date").asText());
                if (date.getYear() == currentYear && date.getMonthValue() == currentMonth) {
                    thisMonthCount += day.path("contributionCount").asInt();
                }
            }
        }
        return Map.of(
                "weeks", weeks,
                "totalContributions", total,
                "currentMonthContributions", thisMonthCount);
    }

}
