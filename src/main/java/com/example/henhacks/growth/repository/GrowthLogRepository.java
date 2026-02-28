package com.example.henhacks.growth.repository;

import com.example.henhacks.growth.model.GrowthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrowthLogRepository extends JpaRepository<GrowthLog, Long> {

    // Custom query to find all logs for a specific plant, sorted by the newest first
    List<GrowthLog> findByPlantIdOrderByScanDateDesc(Long plantId);
}