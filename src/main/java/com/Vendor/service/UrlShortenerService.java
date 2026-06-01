package com.Vendor.service;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UrlShortenerService {

    private final String BITLY_TOKEN = "237c2dcc57991c58e48d1f39ce9a65d6d8d39bd3";

    public String shortenUrl(String longUrl) {

        String apiUrl = "https://api-ssl.bitly.com/v4/shorten";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + BITLY_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("long_url", longUrl);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

        return response.getBody().get("link").toString();
    }
}