package com.srinaka.catalog.repository;

import com.srinaka.catalog.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {

    List<ServiceCategory> findAllByOrderBySortOrderAsc();

    List<ServiceCategory> findByActiveTrueOrderBySortOrderAsc();
}
