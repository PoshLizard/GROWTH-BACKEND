package com.example.henhacks.growth.controller;

import com.example.henhacks.growth.model.GrowthLog;
import com.example.henhacks.growth.service.ScanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/{plantId}")
    public ResponseEntity<GrowthLog> createScan(
            @PathVariable Long plantId,
            @RequestBody GrowthLog scanData) {

        GrowthLog savedLog = scanService.recordGrowthEntry(
                plantId,
                scanData.getHealthScore(),
                scanData.getAiAdvice()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLog);
    }

    /**
     * GET: Fetch just the history for one plant
     * URL: http://localhost:8080/api/scans/plant/{plantId}
     */
    @GetMapping("/plant/{plantId}")
    public ResponseEntity<List<GrowthLog>> getHistory(@PathVariable Long plantId) {
        List<GrowthLog> history = scanService.getPlantHistory(plantId);
        return ResponseEntity.ok(history);
    }
}