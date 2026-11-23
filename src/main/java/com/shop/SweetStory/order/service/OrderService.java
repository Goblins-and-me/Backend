package com.shop.SweetStory.order.service;

import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.auth.repository.UserRepository;
import com.shop.SweetStory.catalog.dto.ActionMessageResponse;
import com.shop.SweetStory.catalog.entity.Product;
import com.shop.SweetStory.catalog.repository.ProductRepository;
import com.shop.SweetStory.order.dto.AdminOrderItemResponse;
import com.shop.SweetStory.order.dto.AdminOrderResponse;
import com.shop.SweetStory.order.dto.CreateOrderRequest;
import com.shop.SweetStory.order.dto.CreateOrderResponse;
import com.shop.SweetStory.order.dto.GroupedOrderItemResponse;
import com.shop.SweetStory.order.dto.GroupedOrdersResponse;
import com.shop.SweetStory.order.dto.MyOrderItemResponse;
import com.shop.SweetStory.order.dto.MyOrderResponse;
import com.shop.SweetStory.order.dto.OrderCreatedItemResponse;
import com.shop.SweetStory.order.dto.OrderCustomerInfo;
import com.shop.SweetStory.order.dto.OrderProductSummary;
import com.shop.SweetStory.order.dto.UpdateOrderStatusRequest;
import com.shop.SweetStory.order.dto.UpdateOrderStatusResponse;
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

/**
 * Business logic service for managing user and administrative orders.
 * Handles delivery date order limit checks, weight-based price calculation,
 * order creation, retrieval, date grouping, status updates, and deletion of {@link Order} entities,
 * as well as resolving the currently authenticated user from the security context.
 * Defaults to {@code @Transactional(readOnly = true)}, while mutating operations
 * run inside read-write transactions ({@code @Transactional}).
 */
@Service
@Transactional(readOnly = true)
public class OrderService {

    // Repository for managing orders in the database
    private final OrderRepository orderRepository;
    // Repository for fetching catalog products
    private final ProductRepository productRepository;
    // Repository for looking up users by email
    private final UserRepository userRepository;
    // Maximum allowed number of active (non-cancelled) orders for a single delivery date
    private final long maxOrdersPerDate;

    /**
     * Creates an order management service with injected repositories and a daily order limit setting.
     *
     * @param orderRepository order repository
     * @param productRepository catalog product repository
     * @param userRepository user repository
     * @param maxOrdersPerDate maximum number of orders allowed per delivery date (default {@code 3})
     */
    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            @Value("${app.orders.max-per-date:3}") long maxOrdersPerDate
    ) {
        // Store dependencies and the per-date order limit setting
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.maxOrdersPerDate = maxOrdersPerDate;
    }

    /**
     * Creates a new order for the currently authenticated user.
     * Checks the daily order limit for the selected date (excluding cancelled orders),
     * looks up the product and user, calculates the total price, and persists the order in {@link OrderStatus#PENDING} status.
     *
     * @param request order creation request payload
     * @return response DTO containing created order details
     * @throws IllegalStateException if the daily order limit is reached or the product has no base price set
     * @throws NoSuchElementException if the product or user is not found
     * @throws IllegalArgumentException if the item weight is invalid
     */
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        // Count orders for the requested date, excluding cancelled ones
        Long activeOrdersCount = orderRepository.countByDeliveryDateAndStatusNot(
                request.delivery_date(),
                OrderStatus.CANCELLED
        );
        // Verify that the allowed order limit for the chosen delivery date has not been exceeded
        if (activeOrdersCount != null && activeOrdersCount >= maxOrdersPerDate) {
            throw new IllegalStateException("DATE_LIMIT_EXCEEDED: Достигнут лимит заказов на дату " + request.delivery_date());
        }

        // Resolve the current user and load the selected product from the catalog
        User currentUser = resolveCurrentUser();
        Product product = productRepository.findById(request.product_id())
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + request.product_id()));

        // Verify the product has a base price before calculating the order total
        BigDecimal basePrice = product.getCakePrice();
        if (basePrice == null) {
            throw new IllegalStateException("Product cake_price is not set for product id: " + product.getId());
        }

        // Compute the weight multiplier and total order price rounded to two decimal places
        BigDecimal weightCoefficient = calculateWeightCoefficient(request.weight());
        BigDecimal orderPrice = basePrice
                .multiply(weightCoefficient)
                .setScale(2, RoundingMode.HALF_UP);

        // Build the new order entity and populate its fields
        Order order = new Order();
        order.setDeliveryDate(request.delivery_date());
        order.setProduct(product);
        order.setUser(currentUser);
        order.setFilling(request.filling());
        order.setDecor(request.decor());
        order.setWeight(request.weight());
        order.setOrderPrice(orderPrice);
        order.setStatus(OrderStatus.PENDING);

        // Persist the order in the database and return the DTO with created order details
        Order saved = orderRepository.save(order);
        return new CreateOrderResponse(toCreatedItemResponse(saved));
    }

    /**
     * Returns the list of orders for the currently authenticated user,
     * sorted by delivery date descending.
     *
     * @return DTO containing the user's orders
     * @throws IllegalStateException if no authenticated user is present in the security context
     * @throws NoSuchElementException if the user from the security context is not found in the database
     */
    public MyOrderResponse getMyOrders() {
        // Retrieve the current user and load their orders in reverse chronological delivery order
        User currentUser = resolveCurrentUser();
        List<MyOrderItemResponse> items = orderRepository.getAllByUserIdOrderByDeliveryDateDesc(currentUser.getId())
                .stream()
                .map(this::toMyOrderItemResponse)
                .toList();

        // Wrap the list items into the user order response DTO
        return new MyOrderResponse(items);
    }

    /**
     * Returns the full list of all orders for administrators,
     * sorted by delivery date ascending and order identifier ascending.
     *
     * @return DTO containing all orders in administrative view format
     */
    public AdminOrderResponse getAllOrders() {
        // Load all orders with the required sort order and map each to an administrative DTO
        List<AdminOrderItemResponse> items = orderRepository.findAllByOrderByDeliveryDateAscIdAsc()
                .stream()
                .map(this::toAdminOrderItemResponse)
                .toList();

        // Return the response object with the order list
        return new AdminOrderResponse(items);
    }

    /**
     * Returns orders grouped by delivery date in chronological order of dates ({@link TreeMap}).
     * Within each date, orders preserve the order returned by the repository.
     *
     * @return DTO mapping each delivery date to the orders scheduled for that date
     */
    public GroupedOrdersResponse getOrdersGroupedByDate() {
        // Group orders by delivery date into a TreeMap so date keys are sorted ascending
        Map<LocalDate, List<GroupedOrderItemResponse>> grouped = orderRepository.findAllByOrderByDeliveryDateAscIdAsc()
                .stream()
                .collect(Collectors.groupingBy(
                        Order::getDeliveryDate,
                        TreeMap::new,
                        Collectors.mapping(this::toGroupedOrderItemResponse, Collectors.toList())
                ));

        // Return the grouped orders in the response DTO
        return new GroupedOrdersResponse(grouped);
    }

    /**
     * Updates the status of the specified order to the new value provided in the request.
     *
     * @param orderId identifier of the order to update
     * @param request request containing the new order status
     * @return DTO with the operation result and current order status
     * @throws NoSuchElementException if no order with the given identifier exists
     * @throws IllegalArgumentException if an invalid status value is provided
     */
    @Transactional
    public UpdateOrderStatusResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        // Find the order by ID or throw if it does not exist
        Order order = findOrderOrThrow(orderId);
        // Convert the status string from the request into an OrderStatus constant
        OrderStatus newStatus = OrderStatus.fromApiValue(request.order_status());
        // Update the order status and persist changes to the database
        order.setStatus(newStatus);
        orderRepository.save(order);

        // Build the response with the order ID and updated status in API format
        return new UpdateOrderStatusResponse(
                "success",
                order.getId(),
                newStatus.toApiValue()
        );
    }

    /**
     * Deletes an order from the database by its identifier.
     *
     * @param orderId identifier of the order to delete
     * @return DTO with a confirmation message for the deleted order
     * @throws NoSuchElementException if no order with the given identifier exists
     */
    @Transactional
    public ActionMessageResponse deleteOrder(Long orderId) {
        // Verify the order exists and remove it from the repository
        Order order = findOrderOrThrow(orderId);
        orderRepository.delete(order);
        // Return a deletion confirmation message
        return new ActionMessageResponse("success", "Заказ удален");
    }

    /**
     * Calculates the price multiplier based on item weight.
     *
     * @param weight item weight
     * @return weight coefficient as a {@link BigDecimal}
     * @throws IllegalArgumentException if weight is null or less than or equal to zero
     */
    private BigDecimal calculateWeightCoefficient(Double weight) {
        // Validate weight before performing calculations
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Order weight must be positive");
        }
        // Replace with a custom weight coefficient formula if needed
        return BigDecimal.valueOf(weight);
    }

    /**
     * Finds an order by identifier or throws {@link NoSuchElementException}.
     *
     * @param orderId order identifier
     * @return matching {@link Order} entity
     * @throws NoSuchElementException if the order is not found
     */
    private Order findOrderOrThrow(Long orderId) {
        // Look up the order by primary key and throw if missing
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
    }

    /**
     * Extracts the current user from {@link SecurityContextHolder} by email (principal name)
     * and loads the corresponding {@link User} entity from the repository.
     *
     * @return currently authenticated {@link User} entity
     * @throws IllegalStateException if no authenticated user is present in the security context
     * @throws NoSuchElementException if no user with that email is found in the database
     */
    private User resolveCurrentUser() {
        // Obtain the current authentication object from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            throw new IllegalStateException("Authenticated user not found in security context");
        }
        // Use the principal name (email) to look up the user record in the database
        String principalName = authentication.getName();
        return userRepository.findByEmail(principalName)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + principalName));
    }

    /**
     * Maps a persisted {@link Order} entity to a created-order DTO {@link OrderCreatedItemResponse}.
     *
     * @param order order entity
     * @return DTO with created order details
     */
    private OrderCreatedItemResponse toCreatedItemResponse(Order order) {
        return new OrderCreatedItemResponse(
                order.getId(),
                order.getDeliveryDate(),
                order.getProduct().getName(),
                order.getFilling(),
                order.getDecor(),
                order.getWeight(),
                order.getOrderPrice(),
                order.getStatus().toApiValue()
        );
    }

    /**
     * Maps an {@link Order} entity to a user order list item DTO {@link MyOrderItemResponse}.
     *
     * @param order order entity
     * @return user order DTO with summary product information
     */
    private MyOrderItemResponse toMyOrderItemResponse(Order order) {
        // Build summary product details including preview image URL
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
                order.getFilling(),
                order.getDecor(),
                order.getWeight(),
                order.getOrderPrice(),
                order.getStatus().toApiValue()
        );
    }

    /**
     * Maps an {@link Order} entity to an administrative order list DTO {@link AdminOrderItemResponse}.
     *
     * @param order order entity
     * @return order DTO with customer contact details and product name
     */
    private AdminOrderItemResponse toAdminOrderItemResponse(Order order) {
        // Collect customer contact details from the associated User entity
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
                order.getFilling(),
                order.getDecor(),
                order.getWeight(),
                order.getOrderPrice()
        );
    }

    /**
     * Maps an {@link Order} entity to a date-grouped order item DTO {@link GroupedOrderItemResponse}.
     *
     * @param order order entity
     * @return order DTO with key customer, product, weight, and price fields
     */
    private GroupedOrderItemResponse toGroupedOrderItemResponse(Order order) {
        // Extract customer name and phone for display in the grouped list
        User user = order.getUser();
        return new GroupedOrderItemResponse(
                order.getId(),
                order.getStatus().toApiValue(),
                user.getName(),
                user.getPhone(),
                order.getProduct().getName(),
                order.getFilling(),
                order.getDecor(),
                order.getWeight(),
                order.getOrderPrice()
        );
    }
}