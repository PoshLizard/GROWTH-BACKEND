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
public class PlantService {

    private final PlantRepository plantRepo;
    private final GrowthLogRepository logRepo;

    // Standard Constructor Injection
    public PlantService(PlantRepository plantRepo, GrowthLogRepository logRepo) {
        this.plantRepo = plantRepo;
        this.logRepo = logRepo;
    }

    // --- 1. PLANT CRUD OPERATIONS ---

    public List<Plant> findAllPlants() {
        return plantRepo.findAll();
    }

    public Plant savePlant(Plant plant) {
        return plantRepo.save(plant);
    }

    public Plant getPlantById(Long id) {
        return plantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found with ID: " + id));
    }

    @Transactional
    public void deletePlant(Long id) {
        if (!plantRepo.existsById(id)) {
            throw new RuntimeException("Cannot delete: Plant not found");
        }
        plantRepo.deleteById(id);
    }

}