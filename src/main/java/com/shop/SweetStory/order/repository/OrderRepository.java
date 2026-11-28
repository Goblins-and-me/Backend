package com.shop.SweetStory.order.repository;

import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.order.entity.Order;
import com.shop.SweetStory.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Long countByDeliveryDateAndStatusNot(LocalDate deliveryDate, OrderStatus status);

    @EntityGraph(attributePaths = {"product", "user"})
    List<Order> getAllByUserIdOrderByDeliveryDateDesc(Long userId);

    @EntityGraph(attributePaths = {"product", "user"})
    List<Order> findAllByOrderByDeliveryDateAscIdAsc();

    @Query("SELECT SUM(o.orderPrice) FROM Order o WHERE o.status != :cancelledStatus")
    BigDecimal calculateTotalRevenue(OrderStatus cancelledStatus);

    @Query("""
        SELECT COALESCE(SUM(o.weight), 0)
        FROM Order o
        WHERE o.deliveryDate = :deliveryDate
          AND o.status <> :excludedStatus
        """)
    Double sumWeightByDeliveryDateAndStatusNot(
            @Param("deliveryDate") LocalDate deliveryDate,
            @Param("excludedStatus") OrderStatus excludedStatus
    );


}
