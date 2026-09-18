package com.srinaka.catalog.service;

import com.srinaka.catalog.dto.SpaServiceRequest;
import com.srinaka.catalog.dto.SpaServiceResponse;
import com.srinaka.catalog.entity.ServiceCategory;
import com.srinaka.catalog.entity.SpaService;
import com.srinaka.catalog.repository.ServiceCategoryRepository;
import com.srinaka.catalog.repository.SpaServiceRepository;
import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import com.srinaka.common.storage.FileStorageService;
import com.srinaka.common.storage.FileValidator;
import com.srinaka.common.storage.StoredFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaServiceService {

    private static final String IMAGE_FOLDER = "srinaka/services";

    private final SpaServiceRepository spaServiceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final FileStorageService fileStorageService;

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
        SpaService service = getOrThrow(id);
        deleteExistingImage(service);
        spaServiceRepository.delete(service);
    }

    @Transactional
    public SpaServiceResponse uploadImage(Long id, MultipartFile file) {
        FileValidator.validateImage(file);
        SpaService service = getOrThrow(id);

        deleteExistingImage(service);

        StoredFile stored = fileStorageService.upload(file, IMAGE_FOLDER, false);
        service.setImagePublicId(stored.publicId());
        service.setImageUrl(stored.secureUrl());

        return toResponse(service);
    }

    @Transactional
    public SpaServiceResponse deleteImage(Long id) {
        SpaService service = getOrThrow(id);
        deleteExistingImage(service);
        service.setImagePublicId(null);
        service.setImageUrl(null);
        return toResponse(service);
    }

    private void deleteExistingImage(SpaService service) {
        if (service.getImagePublicId() != null) {
            fileStorageService.delete(service.getImagePublicId(), "image", false);
        }
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
