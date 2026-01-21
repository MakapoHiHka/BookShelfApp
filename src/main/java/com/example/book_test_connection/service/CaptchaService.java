package com.example.book_test_connection.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CaptchaService {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Value("${recaptcha.secret}")
    private String secret;

    public void verify(String responseToken) {
        if (responseToken == null || responseToken.isEmpty()) {
            throw new RuntimeException("Captcha response is required");
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = VERIFY_URL + "?secret=" + secret + "&response=" + responseToken;
        Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);

        if (response == null || !(Boolean) response.get("success")) {
            throw new RuntimeException("Captcha verification failed");
        }
    }
}