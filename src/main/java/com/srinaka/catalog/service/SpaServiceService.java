package com.srinaka.catalog.service;

import com.srinaka.catalog.dto.SpaServiceRequest;
import com.srinaka.catalog.dto.SpaServiceResponse;
import com.srinaka.catalog.entity.ServiceCategory;
import com.srinaka.catalog.entity.SpaService;
import com.srinaka.catalog.repository.ServiceCategoryRepository;
import com.srinaka.catalog.repository.SpaServiceRepository;
import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaServiceService {

    private final SpaServiceRepository spaServiceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Transactional(readOnly = true)
    public List<SpaServiceResponse> listAll() {
        return spaServiceRepository.findAllByOrderByIdDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SpaServiceResponse> listActive() {
        return spaServiceRepository.findByActiveTrueOrderByIdDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SpaServiceResponse getActiveById(Long id) {
        SpaService service = spaServiceRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERVICE_NOT_FOUND));
        return toResponse(service);
    }

    @Transactional
    public SpaServiceResponse create(SpaServiceRequest request) {
        SpaService service = new SpaService();
        applyRequest(service, request);
        spaServiceRepository.save(service);
        return toResponse(service);
    }

    @Transactional
    public SpaServiceResponse update(Long id, SpaServiceRequest request) {
        SpaService service = getOrThrow(id);
        applyRequest(service, request);
        return toResponse(service);
    }

    @Transactional
    public void delete(Long id) {
        spaServiceRepository.delete(getOrThrow(id));
    }

    private void applyRequest(SpaService service, SpaServiceRequest request) {
        ServiceCategory category = null;
        if (request.categoryId() != null) {
            category = serviceCategoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND));
        }
        service.setCategory(category);
        service.setType(request.type());
        service.setName(request.name());
        service.setDescription(request.description());
        service.setPrice(request.price());
        service.setDurationMinutes(request.durationMinutes());
        service.setActive(request.active());
    }

    private SpaService getOrThrow(Long id) {
        return spaServiceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERVICE_NOT_FOUND));
    }

    private SpaServiceResponse toResponse(SpaService service) {
        ServiceCategory category = service.getCategory();
        return new SpaServiceResponse(
                service.getId(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null,
                service.getType(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getImageUrl(),
                service.isActive());
    }
}
