package com.example.henhacks.growth.service;

import com.example.henhacks.growth.model.GrowthLog;
import com.example.henhacks.growth.model.Plant;
import com.example.henhacks.growth.repository.GrowthLogRepository;
import com.example.henhacks.growth.repository.PlantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScanService {

    private final GrowthLogRepository logRepo;
    private final PlantRepository plantRepo; // Needed to link the log to a plant

    public ScanService(GrowthLogRepository logRepo, PlantRepository plantRepo) {
        this.logRepo = logRepo;
        this.plantRepo = plantRepo;
    }

    /**
     * Records a new growth entry (scan) for a specific plant.
     */
    @Transactional
    public GrowthLog recordGrowthEntry(Long plantId, int healthScore, String notes) {
        // 1. Find the plant this scan belongs to
        Plant plant = plantRepo.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found with ID: " + plantId));

        // 2. Create the log entry and link it
        GrowthLog newLog = new GrowthLog();
        newLog.setPlant(plant);
        newLog.setHealthScore(healthScore);
        newLog.setAiAdvice(notes);
        newLog.setScanDate(LocalDateTime.now());
        newLog.setGrowthStage(stage);

        // 3. Save to DB
        return logRepo.save(newLog);
    }

    /**
     * Fetches history for the React charts.
     */
    public List<GrowthLog> getPlantHistory(Long plantId) {
        return logRepo.findByPlantIdOrderByScanDateDesc(plantId);
    }
}
