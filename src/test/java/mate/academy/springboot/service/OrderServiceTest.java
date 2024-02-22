package mate.academy.springboot.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.springboot.dto.order.OrderItemResponseDto;
import mate.academy.springboot.dto.order.OrderResponseDto;
import mate.academy.springboot.dto.order.OrderUpdateRequestDto;
import mate.academy.springboot.mapper.OrderItemMapper;
import mate.academy.springboot.mapper.OrderMapper;
import mate.academy.springboot.model.Book;
import mate.academy.springboot.model.Category;
import mate.academy.springboot.model.Order;
import mate.academy.springboot.model.OrderItem;
import mate.academy.springboot.model.Role;
import mate.academy.springboot.model.RoleName;
import mate.academy.springboot.model.Status;
import mate.academy.springboot.model.User;
import mate.academy.springboot.repository.order.OrderItemRepository;
import mate.academy.springboot.repository.order.OrderRepository;
import mate.academy.springboot.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderItemMapper orderItemsMapper;
    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private Role roleUser;
    private Role roleAdmin;
    private User user;
    private OrderItem orderItem;
    private Category category;
    private Book book;
    private OrderResponseDto orderDto;
    private OrderItemResponseDto orderItemDto;

    @BeforeEach
    public void setup() {
        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName(RoleName.USER);

        roleAdmin = new Role();
        roleAdmin.setId(2L);
        roleAdmin.setName(RoleName.ADMIN);

        user = new User();
        user.setId(1L);
        user.setEmail("sam@email.com");
        user.setPassword("123456789");
        user.setFirstName("Sam");
        user.setLastName("Smith");
        user.setShippingAddress("123 Main St, City, Country");
        user.setRoles(Set.of(roleUser, roleAdmin));

        category = new Category();
        category.setId(1L);
        category.setName("Category");
        category.setDescription("Description");

        book = new Book();
        book.setId(1L);
        book.setAuthor("Author");
        book.setPrice(BigDecimal.valueOf(111));
        book.setTitle("Title");
        book.setDescription("Description");
        book.setIsbn("1207199818861");
        book.setCategories(Set.of(category));

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotal(BigDecimal.valueOf(222));
        order.setOrderDate(LocalDateTime.of(2024,2, 18, 17, 24, 1));
        order.setShoppingAddress("123 Main St, City, Country");

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setBook(book);
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(222));
        orderItem.setOrder(order);

        orderDto = new OrderResponseDto()
                .setId(order.getId())
                .setUserId(order.getUser().getId())
                .setTotal(order.getTotal())
                .setOrderDate(order.getOrderDate());

        orderItemDto = new OrderItemResponseDto();
        orderItemDto.setId(1L);
        orderItemDto.setBookId(1L);
        orderItemDto.setQuantity(2);
    }

    @Test
    @DisplayName("Verify the order status was updated")
    void updateOrderStatus_WithValidStatus_ReturnOrderDto() {
        String updateStatus = "DELIVERED";
        order.setStatus(Status.NEW);
        orderDto.setStatus(updateStatus);

        Long orderId = 1L;
        OrderUpdateRequestDto updateRequestDto = new OrderUpdateRequestDto(updateStatus);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        when(orderMapper.toDto(any())).thenReturn(orderDto);

        OrderResponseDto actual = orderService.updateOrderStatus(orderId, updateRequestDto);

        assertEquals(updateStatus, actual.getStatus());
    }

    @Test
    @DisplayName("""
            Given available orders, retrieve the available list of order
            """)
    void findAll_WithValidUserId_ReturnOrderList() {
        Long userId = 1L;
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        when(orderRepository.findAllByUserId(userId)).thenReturn(orders);

        List<OrderResponseDto> orderList = orderService.findAll(userId);
        assertEquals(1, orderList.size());
    }

    @Test
    void findAllOrderItems_WithValidOderId_ReturnOrderItemsList() {
        Long userId = 1L;
        Long orderId = 1L;
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        when(orderRepository.findByIdAndUserId(userId, orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrder(order)).thenReturn(orderItemList);

        List<OrderItemResponseDto> orderItems = orderService.findAllOrderItems(userId, orderId);

        Assertions.assertEquals(orderItemList.size(), orderItems.size());
    }

    @Test
    void findOrderItemByIdAndOrderId() {
        Long userId = 1L;
        Long itemId = 1L;
        Long orderId = 1L;

        when(orderRepository.findByIdAndUserId(userId, orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByIdAndOrderId(itemId, orderId))
                .thenReturn(Optional.of(orderItem));
        when(orderItemsMapper.toDto(any())).thenReturn(orderItemDto);

        OrderItemResponseDto actual = orderService
                .findOrderItemByIdAndOrderId(userId, orderId, itemId);

        assertNotNull(actual);
        assertEquals(orderItem.getId(), actual.getId());
    }
}
