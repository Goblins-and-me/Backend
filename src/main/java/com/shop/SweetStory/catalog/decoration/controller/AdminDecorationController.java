package com.shop.SweetStory.catalog.decoration.controller;

import com.shop.SweetStory.catalog.decoration.dto.CreateDecorationRequest;
import com.shop.SweetStory.catalog.decoration.dto.CreateDecorationResponse;
import com.shop.SweetStory.catalog.decoration.dto.UpdateDecorationRequest;
import com.shop.SweetStory.catalog.decoration.service.DecorationService;
import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/decorations")
public class AdminDecorationController {

    private final DecorationService decorationService;

    public AdminDecorationController(DecorationService decorationService){
        this.decorationService = decorationService;
    }

    @PostMapping
    public ResponseEntity<CreateDecorationResponse> createDecoration(@Valid @RequestBody CreateDecorationRequest request){
        CreateDecorationResponse created = decorationService.createDecoration(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionMessageResponse> updateDecoration(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDecorationRequest request
            ){
        return ResponseEntity.ok(decorationService.updateDecoration(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionMessageResponse> deleteDecoration(@PathVariable Long id){
        return ResponseEntity.ok(decorationService.deleteDecoration(id));
    }


}
