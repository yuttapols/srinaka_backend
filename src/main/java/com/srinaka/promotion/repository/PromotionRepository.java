package com.srinaka.promotion.repository;

import com.srinaka.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @EntityGraph(attributePaths = "services")
    List<Promotion> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = "services")
    @Query("select p from Promotion p where p.active = true and p.startDate <= :today and p.endDate >= :today order by p.id desc")
    List<Promotion> findActive(@Param("today") LocalDate today);

    @EntityGraph(attributePaths = "services")
    Optional<Promotion> findById(Long id);

    boolean existsByIdAndServices_Id(Long promotionId, Long serviceId);
}
