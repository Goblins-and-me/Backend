package com.shop.SweetStory.catalog.review.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReviewListResponse (

        List<ReviewResponse> reviews,
        BigDecimal averageRating,
        int totalCount
){
}
