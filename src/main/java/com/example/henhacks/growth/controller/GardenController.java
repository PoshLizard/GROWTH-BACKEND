package com.example.henhacks.growth.controller;

import com.example.henhacks.growth.model.Garden;
import com.example.henhacks.growth.repository.GardenRepository;
import com.example.henhacks.growth.service.GardenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/gardens")
public class GardenController {

    private final GardenService gardenService;

    public GardenController(GardenService gardenService) {
        this.gardenService = gardenService;
    }

    @GetMapping
    public List<Garden> getAll() {
        return gardenService.getAllGardens();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Garden> createGarden(
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        Garden garden = new Garden();
        garden.setName(name);
        garden.setLocation(location);

        // Pass to service to save the entity and the file
        Garden savedGarden = gardenService.createGardenWithImage(garden, file);
        return ResponseEntity.ok(savedGarden);
    }

    @PostMapping("/{id}/image")
    public Garden uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return gardenService.updateGardenImage(id, file);
    }

    @PutMapping("/{id}")
    public Garden update(@PathVariable Long id, @RequestBody Garden garden) {
        return gardenService.updateGarden(id, garden);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gardenService.deleteGarden(id);
        return ResponseEntity.noContent().build();
    }
}