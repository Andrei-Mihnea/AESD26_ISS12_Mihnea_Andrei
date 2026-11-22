package com.example.lab4.infrastructure.persistence;

import com.example.lab4.domain.entities.Scenario;
import com.example.lab4.application.interfaces.ScenarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaScenarioRepository extends JpaRepository<Scenario, Long>, ScenarioRepository {
}
