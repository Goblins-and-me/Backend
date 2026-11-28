package com.shop.SweetStory.order.entity;

import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.catalog.decoration.entity.Decoration;
import com.shop.SweetStory.catalog.filling.entity.Filling;
import com.shop.SweetStory.catalog.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filling_id")
    private Filling filling;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decoration_id")
    private Decoration decoration;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "order_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal orderPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
}