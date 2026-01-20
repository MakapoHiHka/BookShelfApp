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

    public void verify(String response) {
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Captcha response is empty");
        }
        RestTemplate restTemplate = new RestTemplate();
        String url = VERIFY_URL + "?secret=" + secret + "&response=" + response;
        Map<String, Object> body = restTemplate.postForObject(url, null, Map.class);
        if (body == null || !(Boolean) body.get("success")) {
            throw new RuntimeException("Captcha verification failed");
        }
    }
}