package mate.academy.springboot.service;

import mate.academy.springboot.dto.cart.CartItemRequestDto;
import mate.academy.springboot.dto.cart.CartItemRequestUpdateDto;
import mate.academy.springboot.dto.cart.ShoppingCartResponseDto;

public interface ShoppingCartService {
    ShoppingCartResponseDto findByUser(Long userId);

    ShoppingCartResponseDto addBook(Long userId, CartItemRequestDto requestDto);

    ShoppingCartResponseDto updateCartItem(Long cartItemId,
                                           CartItemRequestUpdateDto requestDto);

    void deleteById(Long cartItemId);

}
