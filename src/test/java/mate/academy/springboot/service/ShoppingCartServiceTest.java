package mate.academy.springboot.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import mate.academy.springboot.dto.cart.CartItemRequestDto;
import mate.academy.springboot.dto.cart.ShoppingCartRequestDto;
import mate.academy.springboot.dto.cart.ShoppingCartResponseDto;
import mate.academy.springboot.exception.EntityNotFoundException;
import mate.academy.springboot.mapper.ShoppingCartMapper;
import mate.academy.springboot.model.Book;
import mate.academy.springboot.model.CartItem;
import mate.academy.springboot.model.Category;
import mate.academy.springboot.model.Role;
import mate.academy.springboot.model.RoleName;
import mate.academy.springboot.model.ShoppingCart;
import mate.academy.springboot.model.User;
import mate.academy.springboot.repository.book.BookRepository;
import mate.academy.springboot.repository.cart.CartItemRepository;
import mate.academy.springboot.repository.cart.ShoppingCartRepository;
import mate.academy.springboot.repository.user.UserRepository;
import mate.academy.springboot.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private ShoppingCart shoppingCart;
    private User user;
    private Role roleUser;
    private Role roleAdmin;

    private CartItem cartItem;
    private Book book;
    private Category category;
    private ShoppingCartResponseDto shoppingCartDto;
    private ShoppingCartRequestDto requestDto;
    private CartItemRequestDto cartItemRequestDto;

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

        shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(book);
        cartItem.setQuantity(2);

        shoppingCart.setCartItems(Set.of(cartItem));

        cartItemRequestDto = new CartItemRequestDto();
        cartItemRequestDto.setBookId(cartItem.getBook().getId());
        cartItemRequestDto.setQuantity(cartItem.getQuantity());

        shoppingCartDto = new ShoppingCartResponseDto();
        shoppingCartDto.setId(shoppingCart.getId());
        shoppingCartDto.setUserId(shoppingCart.getUser().getId());
        shoppingCartDto.setCartItems(Set.of(cartItemRequestDto));
    }

    @Test
    @DisplayName("""
    Verify the correct shopping cart was returned when shopping cart exists
            """)
    void findByUser_WithValidUserId_ShouldReturnValidShoppingCart() {
        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);
        ShoppingCartResponseDto actual = shoppingCartService.findByUser(user.getId());
        Assertions.assertEquals(book.getId(), actual.getId());
    }

    @Test
    @DisplayName("Given incorrect id, check if returns exception")
    void findByUser_WithNotExistingUserId_ShouldReturnException() {
        Long userId = 100L;
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.findByUser(userId)
        );

        String expected = "Can`t find shopping cart by user id " + userId;
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Verify the correct shopping cart was saved")
    void addBook() {
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(shoppingCartMapper.toDto(any())).thenReturn(shoppingCartDto);
        ShoppingCartResponseDto actual = shoppingCartService
                .addBook(user.getId(), cartItemRequestDto);

        assertNotNull(actual);
        assertEquals(shoppingCartDto.getId(), actual.getId());
    }

    @Test
    @DisplayName("Given correct id, check if shopping cart is deleted")
    void deleteById() {
        Long cartId = 1L;
        shoppingCartService.deleteById(cartId);
        verify(cartItemRepository, times(1)).deleteById(cartId);
    }
}
