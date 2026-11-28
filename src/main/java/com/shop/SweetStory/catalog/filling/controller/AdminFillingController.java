package com.shop.SweetStory.catalog.filling.controller;

import com.shop.SweetStory.catalog.filling.dto.CreateFillingRequest;
import com.shop.SweetStory.catalog.filling.dto.CreateFillingResponse;
import com.shop.SweetStory.catalog.filling.dto.UpdateFillingRequest;
import com.shop.SweetStory.catalog.filling.service.FillingService;
import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import com.shop.SweetStory.catalog.product.dto.CreateProductRequest;
import com.shop.SweetStory.catalog.product.dto.CreateProductResponse;
import com.shop.SweetStory.catalog.product.dto.UpdateProductRequest;
import com.shop.SweetStory.catalog.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/fillings")
public class AdminFillingController {
    private final FillingService fillingService;

    public AdminFillingController(FillingService fillingService) {
        this.fillingService = fillingService;
    }

    @PostMapping
    public ResponseEntity<CreateFillingResponse> createProduct(@Valid @RequestBody CreateFillingRequest request) {
        CreateFillingResponse created = fillingService.createFilling(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionMessageResponse> updateFilling(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFillingRequest request
    ) {
        return ResponseEntity.ok(fillingService.updateFilling(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionMessageResponse> deleteFilling(@PathVariable Long id) {
        return ResponseEntity.ok(fillingService.deleteFilling(id));
    }
}
