package com.example.henhacks.growth.controller;

import com.example.henhacks.growth.model.Plant;
import com.example.henhacks.growth.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants") // Plural nouns are standard for REST
@CrossOrigin(origins = "http://localhost:3000") // Matches standard React port
public class PlantController {

    @Autowired
    private PlantService plantService;

    // 1. GET ALL: Show the entire garden
    @GetMapping
    public ResponseEntity<List<Plant>> getAllPlants() {
        List<Plant> plants = plantService.findAllPlants();
        return ResponseEntity.ok(plants); // Returns 200 OK
    }

    // 2. GET ONE: Show a specific plant + its growth history chart data
    @GetMapping("/{id}")
    public ResponseEntity<Plant> getPlantById(@PathVariable Long id) {
        try {
            Plant plant = plantService.getPlantById(id);
            return ResponseEntity.ok(plant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Returns 404
        }
    }

    // 3. POST: Add a new plant to the garden
    @PostMapping("/garden/{gardenId}")
    public ResponseEntity<Plant> createPlant(@PathVariable Long gardenId, @RequestBody Plant plant) {
        // We pass the gardenId to the service to handle the association
        Plant savedPlant = plantService.savePlantToGarden(gardenId, plant);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        try {
            plantService.deletePlant(id);
            return ResponseEntity.noContent().build(); // 204 No Content is the standard success for Delete
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 if plant wasn't there
        }
    }
}