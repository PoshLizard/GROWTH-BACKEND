package com.example.henhacks.growth.repository;

import com.example.henhacks.growth.model.Garden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GardenRepository extends JpaRepository<Garden, Long> {
}
