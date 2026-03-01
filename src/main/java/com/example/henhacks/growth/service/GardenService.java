package com.example.henhacks.growth.service;

import com.example.henhacks.growth.model.Garden;
import com.example.henhacks.growth.repository.GardenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class GardenService {

    private final GardenRepository gardenRepo;

    public GardenService(GardenRepository gardenRepo) {
        this.gardenRepo = gardenRepo;
    }

    public List<Garden> getAllGardens() {
        return gardenRepo.findAll();
    }

    public Garden getGardenById(Long id) {
        return gardenRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Garden not found with ID: " + id));
    }

    @Transactional
    public Garden updateGardenImage(Long id, MultipartFile file) throws IOException {
        Garden garden = getGardenById(id);

        // In a real app, you'd save this to S3 or a local folder.
        // For a hackathon, you can convert to Base64 or save to 'src/main/resources/static/uploads'
        String fileName = "garden_" + id + "_" + file.getOriginalFilename();
        Path path = Paths.get("uploads/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());

        garden.setImageUrl("/uploads/" + fileName);
        return gardenRepo.save(garden);
    }

    @Transactional
    public Garden createGardenWithImage(Garden garden, MultipartFile file) throws IOException {
        // 1. Save the garden first to get an ID
        Garden savedGarden = gardenRepo.save(garden);

        if (file != null && !file.isEmpty()) {
            // 2. Define storage path (creates an 'uploads' folder in your project root)
            String fileName = "garden_" + savedGarden.getId() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            // 3. Update the URL and save again
            savedGarden.setImageUrl("/uploads/" + fileName);
            return gardenRepo.save(savedGarden);
        }

        return savedGarden;
    }

    @Transactional
    public Garden updateGarden(Long id, Garden updatedGarden) {
        Garden existing = getGardenById(id);
        existing.setName(updatedGarden.getName());
        existing.setLocation(updatedGarden.getLocation());
        return gardenRepo.save(existing);
    }

    @Transactional
    public void deleteGarden(Long id) {
        if (!gardenRepo.existsById(id)) {
            throw new RuntimeException("Cannot delete: Garden not found");
        }
        gardenRepo.deleteById(id);
    }
}
