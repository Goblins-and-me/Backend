package com.shop.SweetStory.catalog.decoration.dto;

import com.shop.SweetStory.catalog.decoration.entity.Decoration;

import java.util.List;

public record DecorationListResponse (
        List<DecorationResponse> decorations
) {
}
