package com.srinaka.shop.controller;

import com.srinaka.common.response.ApiResponse;
import com.srinaka.shop.dto.ShopInfoResponse;
import com.srinaka.shop.service.ShopInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shop-info")
@RequiredArgsConstructor
public class ShopInfoController {

    private final ShopInfoService shopInfoService;

    @GetMapping
    public ApiResponse<ShopInfoResponse> get() {
        return ApiResponse.success(shopInfoService.get());
    }
}
