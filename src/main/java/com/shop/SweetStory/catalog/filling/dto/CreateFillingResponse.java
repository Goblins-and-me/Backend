package com.shop.SweetStory.catalog.filling.dto;

import com.shop.SweetStory.catalog.decoration.dto.DecorationResponse;
import com.shop.SweetStory.catalog.decoration.entity.Decoration;

public record CreateFillingResponse(
        String status,
        FillingResponse filling
) {
}
