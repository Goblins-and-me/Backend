package com.shop.SweetStory.catalog.filling.dto;

import com.shop.SweetStory.catalog.filling.entity.Filling;

import java.util.List;

public record FillingListResponse(
        List<FillingResponse> fillings
) {
}
