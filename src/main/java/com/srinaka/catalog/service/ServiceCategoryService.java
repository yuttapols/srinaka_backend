package com.srinaka.catalog.service;

import com.srinaka.catalog.dto.ServiceCategoryRequest;
import com.srinaka.catalog.dto.ServiceCategoryResponse;
import com.srinaka.catalog.entity.ServiceCategory;
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
public class ServiceCategoryService {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final SpaServiceRepository spaServiceRepository;

    @Transactional(readOnly = true)
    public List<ServiceCategoryResponse> listAll() {
        return serviceCategoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceCategoryResponse> listActive() {
        return serviceCategoryRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ServiceCategoryResponse create(ServiceCategoryRequest request) {
        ServiceCategory category = new ServiceCategory();
        category.setName(request.name());
        category.setSortOrder(request.sortOrder());
        category.setActive(request.active());
        serviceCategoryRepository.save(category);
        return toResponse(category);
    }

    @Transactional
    public ServiceCategoryResponse update(Long id, ServiceCategoryRequest request) {
        ServiceCategory category = getOrThrow(id);
        category.setName(request.name());
        category.setSortOrder(request.sortOrder());
        category.setActive(request.active());
        return toResponse(category);
    }

    @Transactional
    public void delete(Long id) {
        ServiceCategory category = getOrThrow(id);
        spaServiceRepository.findByCategory_Id(id).forEach(service -> service.setCategory(null));
        serviceCategoryRepository.delete(category);
    }

    private ServiceCategory getOrThrow(Long id) {
        return serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND));
    }

    private ServiceCategoryResponse toResponse(ServiceCategory category) {
        return new ServiceCategoryResponse(category.getId(), category.getName(), category.getSortOrder(), category.isActive());
    }
}
