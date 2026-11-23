// ProductListResponse.java
package com.shop.SweetStory.catalog.dto;

import java.util.List;

/**
 * Response DTO containing a list of catalog products.
 *
 * @param items list of product summary items
 */
public record ProductListResponse (
        List<ProductSummaryResponse> items
){
}