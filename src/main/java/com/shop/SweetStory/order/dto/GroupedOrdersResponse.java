// GroupedOrdersResponse.java
package com.shop.SweetStory.order.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Response DTO containing orders grouped by delivery or creation date.
 *
 * @param grouped_orders map associating each date with the list of orders for that date
 */
public record GroupedOrdersResponse(
        Map<LocalDate, List<GroupedOrderItemResponse>> grouped_orders
) {
}