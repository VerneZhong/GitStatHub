package com.syou.gitstathub.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author verne.zhong
 * @date 2025/04/17
 * @description
 */
@RestController
@RequestMapping("/api/github")
public class ContributionController {

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate;

    public ContributionController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/contributions")
    public ResponseEntity<?> getContributions(@RequestParam String username) {
        String graphqlQuery = """
            query {
              user(login: "%s") {
                contributionsCollection {
                  contributionCalendar {
                    totalContributions
                    weeks {
                      contributionDays {
                        date
                        contributionCount
                        color
                      }
                    }
                  }
                }
              }
            }
        """.formatted(username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", graphqlQuery);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.github.com/graphql",
                    requestEntity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            Object calendar = ((Map)((Map)((Map)body.get("data")).get("user")).get("contributionsCollection")).get("contributionCalendar");

            return ResponseEntity.ok(calendar);
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getResponseBodyAsString());
        }
    }
}
