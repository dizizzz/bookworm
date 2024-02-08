package mate.academy.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.order.OrderItemResponseDto;
import mate.academy.springboot.dto.order.OrderRequestDto;
import mate.academy.springboot.dto.order.OrderResponseDto;
import mate.academy.springboot.dto.order.OrderUpdateRequestDto;
import mate.academy.springboot.model.User;
import mate.academy.springboot.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place an order",
            description = "Place an order for the books in the shopping cart")
    public OrderResponseDto addOrder(@RequestBody @Valid OrderRequestDto requestDto,
                                          @AuthenticationPrincipal User user) {
        return orderService.addOrder(user.getId(), requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get user's orders",
            description = "Retrieve user's order history")
    public List<OrderResponseDto> getOrders(@AuthenticationPrincipal User user) {
        return orderService.findAll(user.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    @Operation(summary = "Update order status",
            description = "Update order status")
    public OrderResponseDto updateOrderStatus(@PathVariable Long id,
                                              @RequestBody OrderUpdateRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items")
    @Operation(summary = "Get all OrderItems",
            description = "Get all OrderItems for a specific order")
    public List<OrderItemResponseDto> getOrderItems(@AuthenticationPrincipal User user,
                                                    @PathVariable Long orderId) {
        return orderService.findAllOrderItems(user.getId(), orderId);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Get OrderItem by id",
            description = "Get OrderItem by id for a specific order")
    public OrderItemResponseDto getOrderItemById(@AuthenticationPrincipal User user,
                                                 @PathVariable Long orderId,
                                                 @PathVariable Long itemId) {
        return orderService.findOrderItemByIdAndOrderId(user.getId(), orderId, itemId);
    }
}
