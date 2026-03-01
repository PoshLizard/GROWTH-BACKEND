package com.example.henhacks.growth.controller;

import com.example.henhacks.growth.model.GrowthLog;
import com.example.henhacks.growth.service.ScanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/scans")
@CrossOrigin(origins = "http://localhost:3000") // Allows your React app to talk to it
public class ScanController {

    private final ScanService scanService;

    // Constructor injection is better practice than @Autowired on fields
    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    /**
     * POST: Add a new growth log to a specific plant
     * URL: http://localhost:8080/api/scans/{plantId}
     */
    @PostMapping("/{plantId}/image")
    public ResponseEntity<GrowthLog> uploadImage(
            @PathVariable Long plantId,
            @RequestParam("file") MultipartFile file) throws IOException {

        GrowthLog savedLog = scanService.recordGrowthEntry(plantId, file.getBytes());
        return ResponseEntity.ok(savedLog);
    }

    /**
     * GET: Fetch just the history for one plant
     * URL: http://localhost:8080/api/scans/plant/{plantId}
     */
    @GetMapping("/log/{plantId}")
    public ResponseEntity<List<GrowthLog>> getHistory(@PathVariable Long plantId) {
        List<GrowthLog> history = scanService.getPlantHistory(plantId);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteScanLog(@PathVariable Long logId) {
        scanService.deleteLog(logId);
        return ResponseEntity.noContent().build();
        // .noContent() returns a 204 status, which is the standard "Success, nothing to show" response
    }
}