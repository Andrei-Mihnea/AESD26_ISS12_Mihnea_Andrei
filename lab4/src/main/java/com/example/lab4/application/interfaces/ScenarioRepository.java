package com.example.lab4.application.interfaces;

import com.example.lab4.domain.entities.Scenario;
import java.util.List;
import java.util.Optional;

/** Persistence port implemented by the infrastructure JPA adapter. */
public interface ScenarioRepository {
    Optional<Scenario> findById(Long id);
    List<Scenario> findAll();
    <S extends Scenario> S save(S entity);
    <S extends Scenario> List<S> saveAll(Iterable<S> entities);
    void delete(Scenario entity);
    void deleteById(Long id);
    void flush();
    long count();

}
