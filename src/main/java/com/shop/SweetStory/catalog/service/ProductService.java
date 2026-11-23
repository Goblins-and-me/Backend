package com.shop.SweetStory.catalog.service;

import com.shop.SweetStory.catalog.dto.ActionMessageResponse;
import com.shop.SweetStory.catalog.dto.CreateProductRequest;
import com.shop.SweetStory.catalog.dto.CreateProductResponse;
import com.shop.SweetStory.catalog.dto.ProductDetailResponse;
import com.shop.SweetStory.catalog.dto.ProductListResponse;
import com.shop.SweetStory.catalog.dto.ProductSummaryResponse;
import com.shop.SweetStory.catalog.dto.UpdateProductRequest;
import com.shop.SweetStory.catalog.entity.Product;
import com.shop.SweetStory.catalog.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business logic service for managing catalog products.
 * Handles listing products and fetching product details, creating, updating, and deleting
 * {@link Product} entities via {@link ProductRepository}, mapping entities to response DTOs,
 * and safely copying list collections.
 * Marked as {@code @Transactional(readOnly = true)} by default, while mutating methods
 * explicitly declare {@code @Transactional}.
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    // Repository for accessing product entities in the database
    private final ProductRepository productRepository;

    /**
     * Creates a product management service with the injected repository.
     *
     * @param productRepository catalog product repository
     */
    public ProductService(ProductRepository productRepository) {
        // Store reference to the product repository
        this.productRepository = productRepository;
    }

    /**
     * Returns all catalog products as summary cards.
     *
     * @return DTO containing a list of product summaries
     */
    public ProductListResponse getAllProducts() {
        // Load all product entities from the database, map each to a summary DTO, and collect into a list
        List<ProductSummaryResponse> items = productRepository.findAll()
                .stream()
                .map(this::toSummaryResponse)
                .toList();

        // Wrap the summary items in the catalog list response DTO
        return new ProductListResponse(items);
    }

    /**
     * Returns detailed information about a product by its identifier.
     *
     * @param id product identifier
     * @return DTO containing detailed product data
     * @throws NoSuchElementException if no product with the given identifier exists
     */
    public ProductDetailResponse getProductById(Long id) {
        // Find the product by ID or throw an exception if it does not exist
        Product product = findProductOrThrow(id);
        // Map the entity to a detailed response DTO
        return toDetailResponse(product);
    }

    /**
     * Creates a new product in the catalog and returns the save result.
     * Runs inside a read-write transaction, trims the product name, and safely copies
     * image, filling, and decoration lists.
     *
     * @param request data for creating the product
     * @return response DTO with the operation status and detailed data of the created product
     */
    @Transactional
    public CreateProductResponse createProduct(CreateProductRequest request) {
        // Create a new product entity and populate its fields from the request
        Product product = new Product();
        // Trim leading and trailing whitespace from the product name
        product.setName(request.name().trim());
        // Set the product description
        product.setDescription(request.description());
        // Copy lists into new collections to avoid shared references and null values
        product.setImages(safeCopy(request.images()));
        product.setFillings(safeCopy(request.fillings()));
        product.setDecorations(safeCopy(request.decorations()));
        // Set weight bounds and base product price
        product.setMinWeight(request.minWeight());
        product.setMaxWeight(request.maxWeight());
        product.setCakePrice(request.cakePrice());

        // Persist the product in the database and obtain the saved entity with its generated ID
        Product saved = productRepository.save(product);
        // Build the response with creation confirmation and product details
        return new CreateProductResponse("success", toDetailResponse(saved));
    }

    /**
     * Updates an existing product's details by its identifier.
     * Modifies only the fields provided in the request, persists changes to the database,
     * and returns an update confirmation message.
     *
     * @param id identifier of the product to update
     * @param request new product field values
     * @return DTO with the result of the update operation
     * @throws NoSuchElementException if no product with the given identifier exists
     */
    @Transactional
    public ActionMessageResponse updateProduct(Long id, UpdateProductRequest request) {
        // Find the existing product in the database or throw an exception
        Product product = findProductOrThrow(id);

        // Update name only when a non-blank string is provided
        if (request.name() != null && !request.name().isBlank()) {
            product.setName(request.name().trim());
        }
        // Update description when explicitly provided in the request
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        // When a new image list is provided, replace the current list with a safe copy
        if (request.images() != null) {
            product.setImages(safeCopy(request.images()));
        }
        // When a new filling list is provided, update available product fillings
        if (request.fillings() != null) {
            product.setFillings(safeCopy(request.fillings()));
        }
        // When a new decoration list is provided, update available decoration options
        if (request.decorations() != null) {
            product.setDecorations(safeCopy(request.decorations()));
        }
        // Update minimum weight when specified
        if (request.minWeight() != null) {
            product.setMinWeight(request.minWeight());
        }
        // Update maximum weight when specified
        if (request.maxWeight() != null) {
            product.setMaxWeight(request.maxWeight());
        }
        // Update price when provided
        if (request.cakePrice() != null) {
            product.setCakePrice(request.cakePrice());
        }

        // Persist the updated product entity to the database
        productRepository.save(product);
        // Return confirmation of a successful update (response string left unchanged)
        return new ActionMessageResponse("success", "Товар успешно обновлен");
    }

    /**
     * Deletes a product from the catalog by its identifier.
     *
     * @param id identifier of the product to delete
     * @return DTO with the result of the deletion operation
     * @throws NoSuchElementException if no product with the given identifier exists
     */
    @Transactional
    public ActionMessageResponse deleteProduct(Long id) {
        // Verify that the product exists before deleting it
        Product product = findProductOrThrow(id);
        // Remove the product entity from the database
        productRepository.delete(product);
        // Return confirmation of a successful deletion (response string left unchanged)
        return new ActionMessageResponse("success", "Товар удален");
    }

    /**
     * Finds a product entity by identifier or throws {@link NoSuchElementException}.
     *
     * @param id product identifier
     * @return found {@link Product} entity
     * @throws NoSuchElementException if the product is not found
     */
    private Product findProductOrThrow(Long id) {
        // Look up the product by primary key and throw an exception if absent
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    /**
     * Maps a {@link Product} entity to a {@link ProductSummaryResponse} DTO.
     *
     * @param product product entity
     * @return summary representation of the product for the catalog list
     */
    private ProductSummaryResponse toSummaryResponse(Product product) {
        // Build a summary card with the preview image
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getPreviewImageUrl(),
                product.getDescription(),
                product.getCakePrice()
        );
    }

    /**
     * Maps a {@link Product} entity to a {@link ProductDetailResponse} DTO,
     * creating defensive copies of collection lists.
     *
     * @param product product entity
     * @return detailed representation of the product
     */
    private ProductDetailResponse toDetailResponse(Product product) {
        // Pass list copies to avoid leaking internal entity state
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                safeCopy(product.getImages()),
                product.getDescription(),
                safeCopy(product.getFillings()),
                safeCopy(product.getDecorations()),
                product.getMinWeight(),
                product.getMaxWeight(),
                product.getCakePrice()
        );
    }

    /**
     * Returns a new {@link ArrayList} containing elements from the source list,
     * or an empty list if the source list is {@code null}.
     *
     * @param source source list of strings
     * @return safe copy of the string list
     */
    private List<String> safeCopy(List<String> source) {
        // Return an empty list when null, otherwise create a new ArrayList
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }
}