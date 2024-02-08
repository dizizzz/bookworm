package mate.academy.springboot.service;

import java.util.List;
import mate.academy.springboot.dto.order.OrderItemResponseDto;
import mate.academy.springboot.dto.order.OrderRequestDto;
import mate.academy.springboot.dto.order.OrderResponseDto;
import mate.academy.springboot.dto.order.OrderUpdateRequestDto;

public interface OrderService {
    OrderResponseDto addOrder(Long userId, OrderRequestDto requestDto);

    List<OrderResponseDto> findAll(Long userId);

    OrderResponseDto updateOrderStatus(Long orderId, OrderUpdateRequestDto requestDto);

    List<OrderItemResponseDto> findAllOrderItems(Long userId, Long orderId);

    OrderItemResponseDto findOrderItemByIdAndOrderId(Long userId,
                                                     Long orderId,
                                                     Long itemId);
}
