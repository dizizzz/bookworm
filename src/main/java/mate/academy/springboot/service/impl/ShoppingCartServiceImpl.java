package mate.academy.springboot.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.cart.CartItemRequestDto;
import mate.academy.springboot.dto.cart.CartItemRequestUpdateDto;
import mate.academy.springboot.dto.cart.ShoppingCartResponseDto;
import mate.academy.springboot.exception.EntityNotFoundException;
import mate.academy.springboot.mapper.ShoppingCartMapper;
import mate.academy.springboot.model.Book;
import mate.academy.springboot.model.CartItem;
import mate.academy.springboot.model.ShoppingCart;
import mate.academy.springboot.model.User;
import mate.academy.springboot.repository.book.BookRepository;
import mate.academy.springboot.repository.cart.CartItemRepository;
import mate.academy.springboot.repository.cart.ShoppingCartRepository;
import mate.academy.springboot.repository.user.UserRepository;
import mate.academy.springboot.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private ShoppingCart createShoppingCartForUserId(Long userId) {
        ShoppingCart newShoppingCart = new ShoppingCart();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by id " + userId)
        );
        newShoppingCart.setUser(user);
        return shoppingCartRepository.save(newShoppingCart);
    }

    @Override
    public ShoppingCartResponseDto addBook(Long userId, CartItemRequestDto requestDto) {
        Long bookId = requestDto.getBookId();
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can`t find book by id " + bookId)
        );
        ShoppingCart shoppingCart = createShoppingCartForUserId(userId);

        CartItem newCartItem = new CartItem();
        newCartItem.setShoppingCart(shoppingCart);
        newCartItem.setBook(book);
        newCartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(newCartItem);

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto findByUser(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find shopping cart by user id " + userId)
                );
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto updateCartItem(Long cartItemId,
                                                  CartItemRequestUpdateDto requestDto) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can`t find cart item by id " + cartItemId)
                );
        cartItem.setQuantity(requestDto.getQuantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cartItem.getShoppingCart());
    }

    @Override
    public void deleteById(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
