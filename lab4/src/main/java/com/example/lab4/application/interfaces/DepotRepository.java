package com.example.lab4.application.interfaces;

import com.example.lab4.domain.entities.Depot;
import java.util.List;
import java.util.Optional;

/** Persistence port implemented by the infrastructure JPA adapter. */
public interface DepotRepository {
    Optional<Depot> findById(Long id);
    List<Depot> findAll();
    <S extends Depot> S save(S entity);
    <S extends Depot> List<S> saveAll(Iterable<S> entities);
    void delete(Depot entity);
    void deleteById(Long id);
    void flush();
    long count();
    Optional<Depot> findFirstByOrderByIdAsc();
    Optional<Depot> findByName(String name);
}
