package com.example.henhacks.growth.repository;

import com.example.henhacks.growth.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {
    // That's it! This gives you save(), findAll(), delete(), etc.
}
