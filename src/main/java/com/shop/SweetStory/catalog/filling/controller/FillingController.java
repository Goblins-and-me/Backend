package com.shop.SweetStory.catalog.filling.controller;

import com.shop.SweetStory.catalog.filling.dto.FillingListResponse;
import com.shop.SweetStory.catalog.filling.dto.FillingResponse;
import com.shop.SweetStory.catalog.filling.service.FillingService;
import com.shop.SweetStory.catalog.product.dto.ProductDetailResponse;
import com.shop.SweetStory.catalog.product.dto.ProductListResponse;
import com.shop.SweetStory.catalog.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fillings")
public class FillingController {

    private final FillingService fillingService;

    public FillingController(FillingService fillingService) {
        this.fillingService = fillingService;
    }

    @GetMapping
    public ResponseEntity<FillingListResponse> getAllFillings() {
        return ResponseEntity.ok(fillingService.getAllFillings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FillingResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(fillingService.getFilling(id));
    }
}