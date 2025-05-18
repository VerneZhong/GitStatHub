package com.syou.gitstathub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syou.gitstathub.model.RepoInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

/**
 * GitHub service
 * @author verne.zhong
 * @date 2025/04/13
 * @description
 */
@Service
@Slf4j
public class GitHubServiceImpl implements GitHubService {
    @Value("${github.username}")
    private String username;

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GitHubServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubToken);
        headers.set("Accept", "application/vnd.github+json");
        return headers;
    }

    @Override
    public List<RepoInfo> fetchUserRepos() {
        List<RepoInfo> allRepos = new ArrayList<>();
        int page = 1;
        RepoInfo[] reposOnPage;

        do {
            String url = "https://api.github.com/users/" + username + "/repos?page=" + page + "&per_page=100";
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<RepoInfo[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    RepoInfo[].class
            );
            reposOnPage = response.getBody();
            if (reposOnPage.length > 0) {
                allRepos.addAll(Arrays.asList(reposOnPage));
                page++;
            } else {
                break;
            }
        } while (reposOnPage.length == 100);

        // language set
        for (RepoInfo repoInfo : allRepos) {
            String languagesUrl = "https://api.github.com/repos/" + username + "/" + repoInfo.getName() + "/languages";
            try {
                HttpEntity<String> entity = new HttpEntity<>(createHeaders());
                ResponseEntity<Map> langResponse = restTemplate.exchange(
                        languagesUrl,
                        HttpMethod.GET,
                        entity,
                        Map.class
                );
                Map<String, Object> languages = langResponse.getBody();
                if (languages != null && !languages.isEmpty()) {
                    repoInfo.setLanguage(languages.keySet().iterator().next());
                } else {
                    repoInfo.setLanguage("Unknown");
                }
            } catch (Exception e) {
                // 处理异常仓库，如被 DMCA 封锁
                repoInfo.setLanguage("Blocked or Error");
                log.error("Failed to fetch language for repo: {} - {}", repoInfo.getName(), e.getMessage());
            }
        }

        return allRepos;
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
