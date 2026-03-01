package com.example.henhacks.growth.controller;

import com.example.henhacks.growth.model.Plant;
import com.example.henhacks.growth.repository.PlantRepository;
import com.example.henhacks.growth.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:5173") // or your frontend port
public class ChatController {

    private final PlantRepository plantRepository;
    @Autowired
    private final ChatService chatService; // We'll create this next

    public ChatController(PlantRepository plantRepository, ChatService chatService) {
        this.plantRepository = plantRepository;
        this.chatService = chatService;
    }

    @PostMapping
    public Map<String, String> chatWithHurbee(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        // 1. Fetch all plants to give Hurbee "Eyes" on the garden
        List<Plant> plants = plantRepository.findAll();

        // 2. Create the "Identity" and "Context" for Gemini
        String gardenContext = "Your name is Hurbee. You are a friendly, pun-loving bee who is an expert gardener. " +
                "You help users manage their plants. Use garden emojis like 🌱, 🐝, and 🌻. Try to keep your responses a little on the shorter side no more than 100 words " +
                "Here is the current state of the user's garden: \n" +
                plants.stream()
                        .map(p -> String.format("- %s (Species: %s): Health Score %d/10, Growth Stage: %s",
                                p.getSpecies(), p.getNickname(), p.getHealthScore(), p.getGrowthStage()))
                        .collect(Collectors.joining("\n"));

        // 3. Send to Gemini via the Service
        String aiResponse = chatService.getAiResponse(userMessage, gardenContext);

        // 4. Return to Frontend
        return Map.of("response", aiResponse);
    }
}