package com.example.lab4.service;

import com.example.lab4.entity.Depot;
import com.example.lab4.repository.DepotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DepotService {
    private final DepotRepository depotRepository;

    public DepotService(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }

    public List<Depot> findAll() { return depotRepository.findAll(); }
    public Optional<Depot> findById(Long id) { return depotRepository.findById(id); }
    public Optional<Depot> findByName(String name) { return depotRepository.findByName(name); }

    @Transactional
    public Depot save(Depot depot) { return depotRepository.save(depot); }

    @Transactional
    public void deleteById(Long id) { depotRepository.deleteById(id); }
}
