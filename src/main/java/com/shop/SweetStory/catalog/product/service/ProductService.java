package com.shop.SweetStory.catalog.product.service;

import com.shop.SweetStory.catalog.decoration.dto.DecorationResponse;
import com.shop.SweetStory.catalog.decoration.entity.Decoration;
import com.shop.SweetStory.catalog.decoration.repository.DecorationRepository;
import com.shop.SweetStory.catalog.filling.dto.FillingResponse;
import com.shop.SweetStory.catalog.filling.entity.Filling;
import com.shop.SweetStory.catalog.filling.repository.FillingRepository;
import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import com.shop.SweetStory.catalog.product.dto.*;
import com.shop.SweetStory.catalog.product.entity.Product;
import com.shop.SweetStory.catalog.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final FillingRepository fillingRepository;
    private final DecorationRepository decorationRepository;

    public ProductService(
            ProductRepository productRepository,
            FillingRepository fillingRepository,
            DecorationRepository decorationRepository
    ) {
        this.productRepository = productRepository;
        this.fillingRepository = fillingRepository;
        this.decorationRepository = decorationRepository;
    }

    public ProductListResponse getAllProducts() {
        List<ProductSummaryResponse> items = productRepository.findAll()
                .stream()
                .map(this::toSummaryResponse)
                .toList();

        return new ProductListResponse(items);
    }

    public ProductDetailResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return toDetailResponse(product);
    }

    @Transactional
    public CreateProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setImages(safeCopyList(request.images()));
        product.setFillings(resolveFillings(request.fillingIds()));
        product.setDecorations(resolveDecorations(request.decorationIds()));
        product.setMinWeight(request.minWeight());
        product.setMaxWeight(request.maxWeight());
        product.setCakePrice(request.cakePrice());

        Product saved = productRepository.save(product);
        return new CreateProductResponse("success", toDetailResponse(saved));
    }

    @Transactional
    public ActionMessageResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);

        if (request.name() != null && !request.name().isBlank()) {
            product.setName(request.name().trim());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.images() != null) {
            product.setImages(safeCopyList(request.images()));
        }
        if (request.fillingIds() != null) {
            product.setFillings(resolveFillings(request.fillingIds()));
        }
        if (request.decorationIds() != null) {
            product.setDecorations(resolveDecorations(request.decorationIds()));
        }
        if (request.minWeight() != null) {
            product.setMinWeight(request.minWeight());
        }
        if (request.maxWeight() != null) {
            product.setMaxWeight(request.maxWeight());
        }
        if (request.cakePrice() != null) {
            product.setCakePrice(request.cakePrice());
        }

        productRepository.save(product);
        return new ActionMessageResponse("success", "Товар успешно обновлен");
    }

    @Transactional
    public ActionMessageResponse deleteProduct(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
        return new ActionMessageResponse("success", "Товар удален");
    }

    private Set<Filling> resolveFillings(Set<Long> fillingIds) {
        if (fillingIds == null || fillingIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Filling> found = fillingRepository.findAllById(fillingIds);
        if (found.size() != fillingIds.size()) {
            Set<Long> foundIds = found.stream().map(Filling::getId).collect(Collectors.toSet());
            List<Long> missing = fillingIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new NoSuchElementException("Fillings not found with ids: " + missing);
        }
        return new HashSet<>(found);
    }

    private Set<Decoration> resolveDecorations(Set<Long> decorationIds) {
        if (decorationIds == null || decorationIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Decoration> found = decorationRepository.findAllById(decorationIds);
        if (found.size() != decorationIds.size()) {
            Set<Long> foundIds = found.stream().map(Decoration::getId).collect(Collectors.toSet());
            List<Long> missing = decorationIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new NoSuchElementException("Decorations not found with ids: " + missing);
        }
        return new HashSet<>(found);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + id));
    }

    private ProductSummaryResponse toSummaryResponse(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getPreviewImageUrl(),
                product.getDescription(),
                product.getCakePrice()
        );
    }

    private ProductDetailResponse toDetailResponse(Product product) {
        List<FillingResponse> fillings = product.getFillings() == null
                ? List.of()
                : product.getFillings().stream()
                .map(f -> new FillingResponse(f.getId(), f.getName(), f.getDescription(), f.getPrice()))
                .toList();

        List<DecorationResponse> decorations = product.getDecorations() == null
                ? List.of()
                : product.getDecorations().stream()
                .map(d -> new DecorationResponse(d.getId(), d.getName(), d.getDescription(), d.getPrice()))
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                safeCopyList(product.getImages()),
                product.getDescription(),
                fillings,
                decorations,
                product.getMinWeight(),
                product.getMaxWeight(),
                product.getCakePrice()
        );
    }

    private <T> List<T> safeCopyList(List<T> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }
}