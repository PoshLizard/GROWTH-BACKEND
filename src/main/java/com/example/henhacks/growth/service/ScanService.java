package com.example.henhacks.growth.service;

import com.example.henhacks.growth.model.GrowthLog;
import com.example.henhacks.growth.model.Plant;
import com.example.henhacks.growth.repository.GrowthLogRepository;
import com.example.henhacks.growth.repository.PlantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class ScanService {

    private final GrowthLogRepository logRepo;
    private final PlantRepository plantRepo; // Needed to link the log to a plant
    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();


    private final String apiKey;
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent";

    public ScanService(GrowthLogRepository logRepo, PlantRepository plantRepo, @Value("${GEMINI_API_KEY}") String apiKey) {
        this.logRepo = logRepo;
        this.plantRepo = plantRepo;
        this.apiKey = apiKey;

    }

    /**
     * Records a new growth entry (scan) for a specific plant.
     */
    @Transactional
    public GrowthLog recordGrowthEntry(Long plantId, byte[] imageBytes) {
        Plant plant = plantRepo.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with ID: " + plantId));

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                                "parts", List.of(
                                        Map.of("inline_data", Map.of(
                                                "mime_type", "image/jpeg",
                                                "data", base64Image
                                        )),
                                        Map.of("text", "Analyze this plant. Return ONLY a raw JSON object with these keys: " +
                                                "\"healthScore\" (integer 1-10), \"growthStage\" (string), \"advice\" (string). " +
                                                "Do not include markdown formatting or backticks."))
                        )
                ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL, entity, Map.class);
            String aiRawResult = extractText(response.getBody());

            GrowthLog log = new GrowthLog();
            log.setPlant(plant);
            log.setScanDate(LocalDateTime.now());

            // --- NEW PARSING LOGIC START ---
            try {
                // 1. Clean the string (AI often wraps JSON in ```json ... ```)
                String cleanedJson = aiRawResult.replaceAll("```json", "")
                        .replaceAll("```", "")
                        .trim();

                // 2. Parse into a JsonNode
                com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(cleanedJson);

                // 3. Extract and set fields
                log.setHealthScore(root.path("healthScore").asInt(5));
                log.setGrowthStage(root.path("growthStage").asText("Unknown"));
                log.setAiAdvice(root.path("advice").asText(aiRawResult));

            } catch (Exception parseError) {
                // Fallback: if AI returns garbage, put everything in the advice field
                log.setAiAdvice("AI Output (Parse Error): " + aiRawResult);
                log.setHealthScore(0);
                log.setGrowthStage("Error");
            }
            // --- NEW PARSING LOGIC END ---

            return logRepo.save(log);

        } catch (Exception e) {
            throw new RuntimeException("Gemini API Error: " + e.getMessage());
        }
    }

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

    /**
     * Fetches history for the React charts.
     */
    public List<GrowthLog> getPlantHistory(Long plantId) {
        return logRepo.findByPlantIdOrderByScanDateDesc(plantId);
    }

    @Transactional
    public void deleteLog(Long logId) {
        if (!logRepo.existsById(logId)) {
            throw new RuntimeException("Log not found with ID: " + logId);
        }
        logRepo.deleteById(logId);
    }
}
