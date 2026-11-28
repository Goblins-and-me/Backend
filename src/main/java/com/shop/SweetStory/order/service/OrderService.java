package com.shop.SweetStory.order.service;

import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.auth.repository.UserRepository;
import com.shop.SweetStory.catalog.decoration.entity.Decoration;
import com.shop.SweetStory.catalog.decoration.repository.DecorationRepository;
import com.shop.SweetStory.catalog.filling.entity.Filling;
import com.shop.SweetStory.catalog.filling.repository.FillingRepository;
import com.shop.SweetStory.catalog.generalDto.ActionMessageResponse;
import com.shop.SweetStory.catalog.product.entity.Product;
import com.shop.SweetStory.catalog.product.repository.ProductRepository;
import com.shop.SweetStory.order.dto.*;
import com.shop.SweetStory.order.entity.Order;
import com.shop.SweetStory.order.entity.OrderStatus;
import com.shop.SweetStory.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final FillingRepository fillingRepository;
    private final DecorationRepository decorationRepository;
    private final UserRepository userRepository;
    private final long maxOrdersPerDate;

    private static final BigDecimal MAX_WEIGHT_PER_DATE_KG = BigDecimal.valueOf(6.0);
    private final BigDecimal maxWeightPerDate;


    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            FillingRepository fillingRepository,
            DecorationRepository decorationRepository,
            UserRepository userRepository,
            @Value("${app.orders.max-per-date:3}") long maxOrdersPerDate,
            @Value("${app.orders.max-weight-per-date:6.0}") BigDecimal maxWeightPerDate
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.fillingRepository = fillingRepository;
        this.decorationRepository = decorationRepository;
        this.userRepository = userRepository;
        this.maxOrdersPerDate = maxOrdersPerDate;
        this.maxWeightPerDate = maxWeightPerDate != null ? maxWeightPerDate : MAX_WEIGHT_PER_DATE_KG;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        LocalDate deliveryDate = request.delivery_date();

        Long activeOrdersCount = orderRepository.countByDeliveryDateAndStatusNot(
                deliveryDate,
                OrderStatus.CANCELLED
        );
        if (activeOrdersCount != null && activeOrdersCount >= maxOrdersPerDate) {
            throw new IllegalStateException("DATE_LIMIT_EXCEEDED: Достигнут лимит заказов на дату " + deliveryDate);
        }

        User currentUser = resolveCurrentUser();
        Product product = productRepository.findById(request.product_id())
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + request.product_id()));

        validateWeight(product, request.weight());
        validateDailyWeightLimit(deliveryDate, request.weight());

        Filling filling = resolveAndValidateFilling(product, request.filling_id());
        Decoration decoration = resolveAndValidateDecoration(product, request.decoration_id());

        BigDecimal orderPrice = calculateOrderPrice(product, filling, decoration, request.weight());

        Order order = new Order();
        order.setDeliveryDate(deliveryDate);
        order.setProduct(product);
        order.setUser(currentUser);
        order.setFilling(filling);
        order.setDecoration(decoration);
        order.setWeight(request.weight());
        order.setOrderPrice(orderPrice);
        order.setStatus(OrderStatus.PENDING);

        Order saved = orderRepository.save(order);
        return new CreateOrderResponse(toCreatedItemResponse(saved));
    }

    private void validateDailyWeightLimit(LocalDate deliveryDate, Double newWeight) {
        if (newWeight == null || newWeight <= 0) {
            throw new IllegalArgumentException("Order weight must be positive");
        }

        Double rawCurrentWeight = orderRepository.sumWeightByDeliveryDateAndStatusNot(
                deliveryDate,
                OrderStatus.CANCELLED
        );
        BigDecimal currentWeight = rawCurrentWeight != null
                ? BigDecimal.valueOf(rawCurrentWeight)
                : BigDecimal.ZERO;
        BigDecimal requestedWeight = BigDecimal.valueOf(newWeight);

        if (currentWeight.add(requestedWeight).compareTo(maxWeightPerDate) > 0) {
            throw new IllegalStateException(
                    "WEIGHT_LIMIT_EXCEEDED: Превышен лимит веса (" + maxWeightPerDate + " кг) на дату " + deliveryDate
            );
        }
    }

    public MyOrderResponse getMyOrders() {
        User currentUser = resolveCurrentUser();
        List<MyOrderItemResponse> items = orderRepository.getAllByUserIdOrderByDeliveryDateDesc(currentUser.getId())
                .stream()
                .map(this::toMyOrderItemResponse)
                .toList();

        return new MyOrderResponse(items);
    }

    public AdminOrderResponse getAllOrders() {
        List<AdminOrderItemResponse> items = orderRepository.findAllByOrderByDeliveryDateAscIdAsc()
                .stream()
                .map(this::toAdminOrderItemResponse)
                .toList();

        return new AdminOrderResponse(items);
    }

    public GroupedOrdersResponse getOrdersGroupedByDate() {
        Map<LocalDate, List<GroupedOrderItemResponse>> grouped = orderRepository.findAllByOrderByDeliveryDateAscIdAsc()
                .stream()
                .collect(Collectors.groupingBy(
                        Order::getDeliveryDate,
                        TreeMap::new,
                        Collectors.mapping(this::toGroupedOrderItemResponse, Collectors.toList())
                ));

        return new GroupedOrdersResponse(grouped);
    }

    @Transactional
    public UpdateOrderStatusResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = findOrderOrThrow(orderId);
        OrderStatus newStatus = OrderStatus.fromApiValue(request.order_status());
        order.setStatus(newStatus);
        orderRepository.save(order);

        return new UpdateOrderStatusResponse(
                "success",
                order.getId(),
                newStatus.toApiValue()
        );
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.calculateTotalRevenue(OrderStatus.CANCELLED);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Transactional
    public ActionMessageResponse deleteOrder(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        orderRepository.delete(order);
        return new ActionMessageResponse("success", "Заказ удален");
    }

    private void validateWeight(Product product, Double weight) {
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Order weight must be positive");
        }
        if (product.getMinWeight() != null && weight < product.getMinWeight()) {
            throw new IllegalArgumentException("Weight " + weight + " is less than minWeight " + product.getMinWeight());
        }
        if (product.getMaxWeight() != null && weight > product.getMaxWeight()) {
            throw new IllegalArgumentException("Weight " + weight + " is greater than maxWeight " + product.getMaxWeight());
        }
    }

    private Filling resolveAndValidateFilling(Product product, Long fillingId) {
        if (fillingId == null) {
            return null;
        }
        Filling filling = fillingRepository.findById(fillingId)
                .orElseThrow(() -> new NoSuchElementException("Filling not found with id: " + fillingId));

        boolean isAvailable = product.getFillings() != null && product.getFillings().stream()
                .anyMatch(f -> f.getId().equals(fillingId));
        if (!isAvailable) {
            throw new IllegalArgumentException("Filling " + fillingId + " is not available for product " + product.getId());
        }
        return filling;
    }

    private Decoration resolveAndValidateDecoration(Product product, Long decorationId) {
        if (decorationId == null) {
            return null;
        }
        Decoration decoration = decorationRepository.findById(decorationId)
                .orElseThrow(() -> new NoSuchElementException("Decoration not found with id: " + decorationId));

        boolean isAvailable = product.getDecorations() != null && product.getDecorations().stream()
                .anyMatch(d -> d.getId().equals(decorationId));
        if (!isAvailable) {
            throw new IllegalArgumentException("Decoration " + decorationId + " is not available for product " + product.getId());
        }
        return decoration;
    }

    private BigDecimal calculateOrderPrice(
            Product product,
            Filling filling,
            Decoration decoration,
            Double weight
    ) {
        BigDecimal basePrice = product.getCakePrice();
        if (basePrice == null) {
            throw new IllegalStateException("Product cake_price is not set for product id: " + product.getId());
        }

        BigDecimal weightCoefficient = calculateWeightCoefficient(weight);
        BigDecimal cakeTotal = basePrice.multiply(weightCoefficient);

        BigDecimal fillingPrice = (filling != null && filling.getPrice() != null)
                ? filling.getPrice()
                : BigDecimal.ZERO;

        BigDecimal decorationPrice = (decoration != null && decoration.getPrice() != null)
                ? decoration.getPrice()
                : BigDecimal.ZERO;

        return cakeTotal
                .add(fillingPrice)
                .add(decorationPrice)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateWeightCoefficient(Double weight) {
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Order weight must be positive");
        }
        return BigDecimal.valueOf(weight);
    }

    private Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
    }

    private User resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new IllegalStateException("Authenticated user not found in security context");
        }
        String principalName = authentication.getName();
        return userRepository.findByEmail(principalName)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + principalName));
    }

    private String extractFillingName(Order order) {
        return order.getFilling() != null ? order.getFilling().getName() : null;
    }

    private String extractDecorationName(Order order) {
        return order.getDecoration() != null ? order.getDecoration().getName() : null;
    }

    private OrderCreatedItemResponse toCreatedItemResponse(Order order) {
        return new OrderCreatedItemResponse(
                order.getId(),
                order.getDeliveryDate(),
                order.getProduct().getName(),
                extractFillingName(order),
                extractDecorationName(order),
                order.getWeight(),
                order.getOrderPrice(),
                order.getStatus().toApiValue()
        );
    }

    private MyOrderItemResponse toMyOrderItemResponse(Order order) {
        Product product = order.getProduct();
        OrderProductSummary summary = new OrderProductSummary(
                product.getId(),
                product.getName(),
                product.getPreviewImageUrl()
        );

        return new MyOrderItemResponse(
                order.getId(),
                order.getDeliveryDate(),
                summary,
                extractFillingName(order),
                extractDecorationName(order),
                order.getWeight(),
                order.getOrderPrice(),
                order.getStatus().toApiValue()
        );
    }

    private AdminOrderItemResponse toAdminOrderItemResponse(Order order) {
        User user = order.getUser();
        OrderCustomerInfo customerInfo = new OrderCustomerInfo(
                user.getName(),
                user.getPhone(),
                user.getEmail()
        );

        return new AdminOrderItemResponse(
                order.getId(),
                order.getDeliveryDate(),
                order.getStatus().toApiValue(),
                customerInfo,
                order.getProduct().getName(),
                extractFillingName(order),
                extractDecorationName(order),
                order.getWeight(),
                order.getOrderPrice()
        );
    }

    private GroupedOrderItemResponse toGroupedOrderItemResponse(Order order) {
        User user = order.getUser();
        return new GroupedOrderItemResponse(
                order.getId(),
                order.getStatus().toApiValue(),
                user.getName(),
                user.getPhone(),
                order.getProduct().getName(),
                extractFillingName(order),
                extractDecorationName(order),
                order.getWeight(),
                order.getOrderPrice()
        );
    }
}