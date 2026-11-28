package com.shop.SweetStory.catalog.product.entity;

import com.shop.SweetStory.catalog.decoration.entity.Decoration;
import com.shop.SweetStory.catalog.filling.entity.Filling;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cake_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal cakePrice;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_fillings",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "filling_id")
    )
    private Set<Filling> fillings = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_decorations",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "decoration_id")
    )
    private Set<Decoration> decorations = new HashSet<>();

    @Column(name = "min_weight")
    private Double minWeight;

    @Column(name = "max_weight")
    private Double maxWeight;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "reviews_count", nullable = false)
    private Integer reviewsCount = 0;

    public String getPreviewImageUrl() {
        return (images == null || images.isEmpty()) ? null : images.get(0);
    }
}