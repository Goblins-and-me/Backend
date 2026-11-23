package com.shop.SweetStory.catalog.controller;

import com.shop.SweetStory.catalog.dto.ProductDetailResponse;
import com.shop.SweetStory.catalog.dto.ProductListResponse;
import com.shop.SweetStory.catalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Публичный REST-контроллер просмотра товаров каталога по пути {@code /api/products}.
 * Обрабатывает запросы на получение списка всех товаров и детальной информации о конкретном товаре,
 * делегируя выполнение сервису {@link ProductService}.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Сервис работы с товарами каталога
    private final ProductService productService;

    /**
     * Создаёт контроллер каталога товаров с внедрённым сервисом.
     *
     * @param productService сервис товаров каталога
     */
    public ProductController(ProductService productService) {
        // Сохраняем ссылку на сервис товаров
        this.productService = productService;
    }

    /**
     * Возвращает список всех доступных товаров каталога.
     *
     * @return ответ {@code 200 OK} со списком товаров
     */
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts() {
        // Запрашиваем полный список товаров у сервиса и возвращаем HTTP 200 OK
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Возвращает подробную информацию о товаре по его идентификатору.
     *
     * @param id идентификатор товара из пути запроса
     * @return ответ {@code 200 OK} с детальными данными товара
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable Long id) {
        // Получаем карточку товара по id через сервис и возвращаем HTTP 200 OK
        return ResponseEntity.ok(productService.getProductById(id));
    }
}