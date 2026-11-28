package com.shop.SweetStory.catalog.filling.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "fillings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Filling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description")
    String description;

    @Column(name = "price", precision = 12, scale = 2, nullable = false)
    BigDecimal price;


}
