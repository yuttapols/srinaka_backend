package com.srinaka.catalog.repository;

import com.srinaka.catalog.entity.SpaService;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaServiceRepository extends JpaRepository<SpaService, Long> {

    @EntityGraph(attributePaths = "category")
    List<SpaService> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = "category")
    List<SpaService> findByActiveTrueOrderByIdDesc();

    @EntityGraph(attributePaths = "category")
    Optional<SpaService> findByIdAndActiveTrue(Long id);

    List<SpaService> findByCategory_Id(Long categoryId);
}
