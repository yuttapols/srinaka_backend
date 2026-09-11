package com.srinaka.menu.controller;

import com.srinaka.common.response.ApiResponse;
import com.srinaka.menu.dto.MenuResponse;
import com.srinaka.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ApiResponse<List<MenuResponse>> myMenu() {
        return ApiResponse.success(menuService.getMenuTreeForCurrentUser());
    }
}
