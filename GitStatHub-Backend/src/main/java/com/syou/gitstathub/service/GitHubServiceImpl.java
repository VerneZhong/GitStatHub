package com.syou.gitstathub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syou.gitstathub.Constants;
import com.syou.gitstathub.model.RepoInfo;
import com.syou.gitstathub.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    /**
     * 获取指定 GitHub 用户的所有公共仓库信息，并为每个仓库附加主要编程语言。
     *
     * <p>此方法通过调用 GitHub REST API 完成以下任务：
     * <ul>
     *   <li>分页获取用户的仓库列表（每页最多 100 个仓库）</li>
     *   <li>为每个仓库调用 /languages 接口获取语言使用情况</li>
     *   <li>处理异常情况，如被 DMCA 封锁的仓库或 API 请求失败等</li>
     * </ul>
     *
     * <p>注意事项：
     * <ul>
     *   <li>本方法默认已通过 {@code createHeaders()} 方法添加了认证头（建议使用 Personal Access Token）</li>
     *   <li>未实现 API Rate Limit（速率限制）处理，如有需要建议加入重试或降速机制</li>
     * </ul>
     *
     * @return 包含所有仓库信息的 {@link RepoInfo} 列表，每个仓库附带语言信息
     */
    @Override
    public List<RepoInfo> fetchUserRepos() {
        List<RepoInfo> allRepos = new ArrayList<>();
        int page = 1;
        RepoInfo[] reposOnPage;
        HttpEntity<String> entity = new HttpEntity<>(HttpUtil.createHeaders(githubToken));

        do {
            String url = Constants.GITHUB_USER_URL + username + "/repos?page=" + page + "&per_page=100";
            ResponseEntity<RepoInfo[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, RepoInfo[].class);
            reposOnPage = response.getBody();
            if (reposOnPage.length > 0) {
                allRepos.addAll(Arrays.asList(reposOnPage));
                page++;
            } else {
                break;
            }
        } while (reposOnPage.length == 100);

        allRepos.forEach(repoInfo -> {
            String languagesUrl = String.format(Constants.GITHUB_REPO_LANG_URL, username, repoInfo.getName());
            try {
                ResponseEntity<Map> langResponse = restTemplate.exchange(languagesUrl, HttpMethod.GET, entity, Map.class);
                Map<String, Object> languages = langResponse.getBody();

                if (!languages.isEmpty()) {
                    String firstLang = languages.keySet().iterator().next();
                    repoInfo.setLanguage(firstLang);
                } else {
                    repoInfo.setLanguage(Constants.UN_KNOWN);
                    log.debug("No languages found for repo: {}", repoInfo.getName());
                }
            } catch (HttpClientErrorException.Forbidden e) {
                repoInfo.setLanguage("Blocked");
                log.warn("Access blocked for repo: {} - {}", repoInfo.getName(), e.getMessage());
            } catch (HttpClientErrorException.NotFound e) {
                repoInfo.setLanguage("Not Found");
                log.warn("Repo not found: {} - {}", repoInfo.getName(), e.getMessage());
            } catch (Exception e) {
                repoInfo.setLanguage("Error");
                log.error("Failed to fetch language for repo: {} - {}", repoInfo.getName(), e.getMessage());
            }
        });

        return allRepos;
    }

    @Override
    public Map<String, Object> getContributions(String username) throws JsonProcessingException {
        Map<String, Object> body = getStringObjectMap(username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(Constants.GITHUB_GRAPHQL_URL, HttpMethod.POST, entity, String.class);

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

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                Constants.GITHUB_GRAPHQL_URL,
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
