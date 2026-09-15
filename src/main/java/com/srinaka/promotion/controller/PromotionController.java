package com.srinaka.promotion.controller;

import com.srinaka.common.response.ApiResponse;
import com.srinaka.promotion.dto.PromotionResponse;
import com.srinaka.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ApiResponse<List<PromotionResponse>> list() {
        return ApiResponse.success(promotionService.listActive());
    }
}
