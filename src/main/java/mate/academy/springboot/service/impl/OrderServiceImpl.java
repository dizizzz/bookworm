package mate.academy.springboot.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.order.OrderItemResponseDto;
import mate.academy.springboot.dto.order.OrderRequestDto;
import mate.academy.springboot.dto.order.OrderResponseDto;
import mate.academy.springboot.dto.order.OrderUpdateRequestDto;
import mate.academy.springboot.exception.EntityNotFoundException;
import mate.academy.springboot.mapper.OrderItemMapper;
import mate.academy.springboot.mapper.OrderMapper;
import mate.academy.springboot.model.CartItem;
import mate.academy.springboot.model.Order;
import mate.academy.springboot.model.OrderItem;
import mate.academy.springboot.model.ShoppingCart;
import mate.academy.springboot.model.Status;
import mate.academy.springboot.model.User;
import mate.academy.springboot.repository.cart.ShoppingCartRepository;
import mate.academy.springboot.repository.order.OrderItemRepository;
import mate.academy.springboot.repository.order.OrderRepository;
import mate.academy.springboot.repository.user.UserRepository;
import mate.academy.springboot.service.OrderService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemsMapper;
    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public OrderResponseDto addOrder(Long userId, OrderRequestDto requestDto) {
        User user = getUser(userId);

        Order order = createOrder(user, requestDto);

        ShoppingCart shoppingCart = getShoppingCart(userId);

        Set<CartItem> cartItems = shoppingCart.getCartItems();

        BigDecimal total = countTotal(cartItems);
        order.setTotal(total);

        Set<OrderItem> orderItems = createOrderItems(order, cartItems);
        order.setOrderItems(orderItems);

        saveOrderAndOrderItems(order, orderItems);
        clearShoppingCart(shoppingCart);

        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponseDto> findAll(Long userId) {
        return orderRepository.findAllByUserId(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId,
                                              OrderUpdateRequestDto requestDto) {
        Order order = getOrder(orderId);
        Status status = Status.valueOf(requestDto.status().toUpperCase());
        order.setStatus(status);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public List<OrderItemResponseDto> findAllOrderItems(Long userId, Long orderId) {
        Order order = getOrder(userId, orderId);
        return orderItemRepository.findAllByOrder(order).stream()
                .map(orderItemsMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemResponseDto findOrderItemByIdAndOrderId(Long userId,
                                                            Long orderId,
                                                            Long itemId) {
        getOrder(userId, orderId);
        OrderItem orderItem = getOrderItem(itemId, orderId);
        return orderItemsMapper.toDto(orderItem);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find user by id" + userId
                ));
    }

    private Order createOrder(User user, OrderRequestDto requestDto) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.NEW);
        order.setOrderDate(LocalDateTime.now());
        order.setShoppingAddress(requestDto.getShoppingAddress());
        return order;
    }

    private ShoppingCart getShoppingCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find shopping cart by user id" + userId
                ));

        if (shoppingCart.getCartItems().isEmpty()) {
            throw new IllegalStateException("Shopping cart is empty");
        }

        return shoppingCart;
    }

    private BigDecimal countTotal(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> cartItem.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<OrderItem> createOrderItems(Order order, Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private void saveOrderAndOrderItems(Order order, Set<OrderItem> orderItems) {
        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
    }

    private void clearShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.clearCart();
        shoppingCartRepository.save(shoppingCart);
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find order by id" + orderId
                ));
    }

    private Order getOrder(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(userId, orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find order by id" + orderId + " or by user id" + userId
                ));
    }

    private OrderItem getOrderItem(Long itemId, Long orderId) {
        return orderItemRepository.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find orderItem by id " + itemId
                ));
    }
}
