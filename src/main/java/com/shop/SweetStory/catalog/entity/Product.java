package com.shop.SweetStory.catalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA-сущность товара каталога, отображаемая на таблицу {@code products}.
 * Хранит основные характеристики товара, цену, диапазоны веса и связанные списки
 * изображений, начинок и вариантов декора.
 */
@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    /**
     * Первичный ключ товара, генерируемый базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название товара (обязательное поле).
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Подробное описание товара в текстовом формате ({@code TEXT}).
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Базовая цена товара с точностью до 12 знаков всего и 2 знаков после запятой.
     */
    @Column(name = "cake_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal cakePrice;

    /**
     * Список ссылок или путей к изображениям товара из таблицы {@code product_images}.
     */
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    /**
     * Список доступных начинок товара из таблицы {@code product_fillings}.
     */
    @ElementCollection
    @CollectionTable(name = "product_fillings", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "filling")
    private List<String> fillings = new ArrayList<>();

    /**
     * Список доступных вариантов декора товара из таблицы {@code product_decorations}.
     */
    @ElementCollection
    @CollectionTable(name = "product_decorations", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "decoration")
    private List<String> decorations = new ArrayList<>();

    /**
     * Минимальный допустимый вес товара.
     */
    @Column(name = "min_weight")
    private Double minWeight;

    /**
     * Максимальный допустимый вес товара.
     */
    @Column(name = "max_weight")
    private Double maxWeight;

    /**
     * Возвращает URL первого изображения товара для превью или {@code null}, если список изображений пуст.
     *
     * @return URL превью-изображения или {@code null}
     */
    public String getPreviewImageUrl() {
        // Если список изображений не задан или пуст, превью отсутствует
        return (images == null || images.isEmpty()) ? null : images.get(0);
    }


}