package com.example.henhacks.growth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    @Value("${GEMINI_API_KEY}")
    private String apiKey;
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private final RestTemplate restTemplate = new RestTemplate();

    public String getAiResponse(String userMessage, String gardenContext) {
        String fullPrompt = gardenContext + "\nUser asks: " + userMessage;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", fullPrompt))
                ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL + "?key=" + apiKey, entity, Map.class);
            // Use your existing extractText helper logic here
            return extractText(response.getBody());
        } catch (Exception e) {

            System.err.println("Gemini API Error: " + e.getMessage());
            e.printStackTrace();

            return "Oops! My garden brain is a bit fuzzy right now. Try again?";
        }
    }

    // ... insert your extractText method from ScanService ...
    private String extractText(Map responseBody) {
        // Gemini 3 response path: candidates[0].content.parts[0].text
        try {
            List candidates = (List) responseBody.get("candidates");
            Map content = (Map) ((Map) candidates.get(0)).get("content");
            List parts = (List) content.get("parts");
            return (String) ((Map) parts.get(0)).get("text");
        } catch (Exception e) {
            return "Could not parse AI response.";
        }
    }

}