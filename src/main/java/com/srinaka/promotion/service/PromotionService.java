package com.srinaka.promotion.service;

import com.srinaka.catalog.entity.SpaService;
import com.srinaka.catalog.repository.SpaServiceRepository;
import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import com.srinaka.promotion.dto.PromotionRequest;
import com.srinaka.promotion.dto.PromotionResponse;
import com.srinaka.promotion.entity.Promotion;
import com.srinaka.promotion.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final SpaServiceRepository spaServiceRepository;

    @Transactional(readOnly = true)
    public List<PromotionResponse> listAll() {
        return promotionRepository.findAllByOrderByIdDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PromotionResponse> listActive() {
        return promotionRepository.findActive(LocalDate.now()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PromotionResponse create(PromotionRequest request) {
        Promotion promotion = new Promotion();
        applyRequest(promotion, request);
        promotionRepository.save(promotion);
        return toResponse(promotion);
    }

    @Transactional
    public PromotionResponse update(Long id, PromotionRequest request) {
        Promotion promotion = getOrThrow(id);
        applyRequest(promotion, request);
        return toResponse(promotion);
    }

    @Transactional
    public void delete(Long id) {
        promotionRepository.delete(getOrThrow(id));
    }

    private void applyRequest(Promotion promotion, PromotionRequest request) {
        Set<SpaService> services = new HashSet<>(spaServiceRepository.findAllById(request.serviceIds()));
        if (services.size() != request.serviceIds().size()) {
            throw new BusinessException(ErrorCode.SERVICE_NOT_FOUND);
        }

        promotion.setTitle(request.title());
        promotion.setDescription(request.description());
        promotion.setDiscountType(request.discountType());
        promotion.setDiscountValue(request.discountValue());
        promotion.setStartDate(request.startDate());
        promotion.setEndDate(request.endDate());
        promotion.setActive(request.active());
        promotion.setServices(services);
    }

    private Promotion getOrThrow(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMOTION_NOT_FOUND));
    }

    private PromotionResponse toResponse(Promotion promotion) {
        List<Long> serviceIds = promotion.getServices().stream().map(SpaService::getId).toList();
        return new PromotionResponse(
                promotion.getId(),
                promotion.getTitle(),
                promotion.getDescription(),
                promotion.getDiscountType(),
                promotion.getDiscountValue(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.isActive(),
                serviceIds);
    }
}
