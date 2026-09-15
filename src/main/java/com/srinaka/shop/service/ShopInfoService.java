package com.srinaka.shop.service;

import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import com.srinaka.shop.dto.ShopInfoRequest;
import com.srinaka.shop.dto.ShopInfoResponse;
import com.srinaka.shop.entity.ShopInfo;
import com.srinaka.shop.repository.ShopInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ShopInfoService {

    private static final Long SINGLETON_ID = 1L;

    private final ShopInfoRepository shopInfoRepository;

    @Transactional(readOnly = true)
    public ShopInfoResponse get() {
        return toResponse(getOrThrow());
    }

    @Transactional
    public ShopInfoResponse update(ShopInfoRequest request) {
        ShopInfo shopInfo = getOrThrow();
        shopInfo.setAddress(request.address());
        shopInfo.setPhone(request.phone());
        shopInfo.setEmail(request.email());
        shopInfo.setMapUrl(request.mapUrl());
        shopInfo.setBusinessHours(request.businessHours());
        shopInfo.setLineId(request.lineId());
        shopInfo.setFacebookUrl(request.facebookUrl());
        shopInfo.setInstagramUrl(request.instagramUrl());
        shopInfo.setUpdatedAt(Instant.now());
        return toResponse(shopInfo);
    }

    private ShopInfo getOrThrow() {
        return shopInfoRepository.findById(SINGLETON_ID)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_INFO_NOT_FOUND));
    }

    private ShopInfoResponse toResponse(ShopInfo shopInfo) {
        return new ShopInfoResponse(
                shopInfo.getAddress(),
                shopInfo.getPhone(),
                shopInfo.getEmail(),
                shopInfo.getMapUrl(),
                shopInfo.getBusinessHours(),
                shopInfo.getLineId(),
                shopInfo.getFacebookUrl(),
                shopInfo.getInstagramUrl());
    }
}
