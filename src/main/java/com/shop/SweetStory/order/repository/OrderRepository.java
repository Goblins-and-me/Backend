package com.shop.SweetStory.order.repository;

import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.order.entity.Order;
import com.shop.SweetStory.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for working with {@link Order} entities
 * and executing queries against the {@code orders} table.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Counts orders for the given delivery date whose status differs from the specified one.
     *
     * @param deliveryDate order delivery date
     * @param status excluded order status {@link OrderStatus}
     * @return count of matching orders
     */
    Long countByDeliveryDateAndStatusNot(LocalDate deliveryDate, OrderStatus status);

    /**
     * Returns all orders for the specified user ordered by delivery date descending,
     * eagerly fetching associated {@code product} and {@code user} entities via an entity graph.
     *
     * @param userId user identifier
     * @return list of the user's orders sorted by {@code deliveryDate} descending
     */
    @EntityGraph(attributePaths = {"product", "user"})
    List<Order> getAllByUserIdOrderByDeliveryDateDesc(Long userId);

    /**
     * Returns all orders sorted by delivery date ascending and then by identifier ascending,
     * eagerly fetching associated {@code product} and {@code user} entities via an entity graph.
     *
     * @return sorted list of all orders
     */
    @EntityGraph(attributePaths = {"product", "user"})
    List<Order> findAllByOrderByDeliveryDateAscIdAsc();
}