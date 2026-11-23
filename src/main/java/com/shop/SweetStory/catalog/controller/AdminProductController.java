package com.shop.SweetStory.catalog.controller;

import com.shop.SweetStory.catalog.dto.ActionMessageResponse;
import com.shop.SweetStory.catalog.dto.CreateProductRequest;
import com.shop.SweetStory.catalog.dto.CreateProductResponse;
import com.shop.SweetStory.catalog.dto.UpdateProductRequest;
import com.shop.SweetStory.catalog.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер административного управления товарами каталога по пути {@code /api/admin/products}.
 * Обрабатывает запросы на создание, полное обновление и удаление товаров,
 * делегируя бизнес-логику сервису {@link ProductService}.
 */
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    // Сервис управления товарами каталога
    private final ProductService productService;

    /**
     * Создаёт административный контроллер товаров с внедрённым сервисом каталога.
     *
     * @param productService сервис работы с товарами
     */
    public AdminProductController(ProductService productService) {
        // Сохраняем ссылку на сервис товаров
        this.productService = productService;
    }

    /**
     * Создаёт новый товар в каталоге на основе проверенных входных данных.
     *
     * @param request данные нового товара, проходящие валидацию через Jakarta Bean Validation
     * @return ответ со статусом {@code 201 Created} и данными созданного товара
     */
    @PostMapping
    public ResponseEntity<CreateProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        // Передаём запрос в сервис для создания товара и получаем результат
        CreateProductResponse created = productService.createProduct(request);
        // Возвращаем HTTP 201 Created с телом ответа
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Обновляет данные существующего товара по его идентификатору.
     *
     * @param id идентификатор обновляемого товара из пути запроса
     * @param request новые данные товара, проходящие валидацию
     * @return ответ {@code 200 OK} с сообщением о результате обновления
     */
    @PutMapping("/{id}")
    public ResponseEntity<ActionMessageResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        // Вызываем обновление товара по id и возвращаем HTTP 200 OK с ответом сервиса
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    /**
     * Удаляет товар из каталога по его идентификатору.
     *
     * @param id идентификатор удаляемого товара из пути запроса
     * @return ответ {@code 200 OK} с сообщением о результате удаления
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ActionMessageResponse> deleteProduct(@PathVariable Long id) {
        // Удаляем товар по id через сервис и возвращаем HTTP 200 OK с результатом операции
        return ResponseEntity.ok(productService.deleteProduct(id));
    }
}