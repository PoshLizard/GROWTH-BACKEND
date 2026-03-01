package com.example.henhacks.growth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class GrowthLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int healthScore; // 1-10

    private String diseaseDetected; // e.g., "Leaf Spot", "None"

    @Column(columnDefinition = "TEXT")
    private String aiAdvice;

    private LocalDateTime scanDate = LocalDateTime.now();

    private String growthStage;

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    @ManyToOne
    @JoinColumn(name = "plant_id")
    @JsonIgnore // Prevents infinite loops when converting to JSON for React
    private Plant plant;

    public int getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(int healthScore) {
        this.healthScore = healthScore;
    }

    public String getDiseaseDetected() {
        return diseaseDetected;
    }

    public void setDiseaseDetected(String diseaseDetected) {
        this.diseaseDetected = diseaseDetected;
    }

    public String getAiAdvice() {
        return aiAdvice;
    }

    public void setAiAdvice(String aiAdvice) {
        this.aiAdvice = aiAdvice;
    }

    public LocalDateTime getScanDate() {
        return scanDate;
    }

    public void setScanDate(LocalDateTime scanDate) {
        this.scanDate = scanDate;
    }

    public String getGrowthStage() {
        return growthStage;
    }

    public void setGrowthStage(String growthStage) {
        this.growthStage = growthStage;
    }

    public Long getId() {
        return id;
    }
}
