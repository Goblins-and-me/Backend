package com.shop.SweetStory.catalog.decoration.controller;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.shop.SweetStory.catalog.decoration.dto.DecorationListResponse;
import com.shop.SweetStory.catalog.decoration.dto.DecorationResponse;
import com.shop.SweetStory.catalog.decoration.service.DecorationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/decorations")
public class DecorationController {
    private final DecorationService decorationService;

    public DecorationController(DecorationService decorationService){
        this.decorationService = decorationService;
    }

    @GetMapping
    public ResponseEntity<DecorationListResponse> getAllDecorations(){
        return ResponseEntity.ok(decorationService.getAllDecorations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DecorationResponse> getDecorationById(@PathVariable Long id){
        return ResponseEntity.ok(decorationService.getDecoration(id));
    }


}
