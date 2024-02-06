package mate.academy.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.cart.CartItemRequestDto;
import mate.academy.springboot.dto.cart.CartItemRequestUpdateDto;
import mate.academy.springboot.dto.cart.ShoppingCartResponseDto;
import mate.academy.springboot.model.User;
import mate.academy.springboot.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart management", description = "Endpoints for managing shopping cart")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new book", description = "Add book to the shopping cart")
    public ShoppingCartResponseDto addBookToCart(@RequestBody @Valid CartItemRequestDto requestDto,
                                                 @AuthenticationPrincipal User user) {
        return shoppingCartService.addBook(user.getId(), requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    @Operation(summary = "Get user's shopping cart",
            description = "Retrieve user's shopping cart")
    public ShoppingCartResponseDto getShoppingCart(@AuthenticationPrincipal User user) {
        return shoppingCartService.findByUser(user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update item",
            description = "Update quantity of a book in the shopping cart")
    public ShoppingCartResponseDto updateCartItem(@PathVariable Long cartItemId,
                                  @RequestBody CartItemRequestUpdateDto requestDto) {
        return shoppingCartService.updateCartItem(cartItemId, requestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Delete the item by ID",
            description = "Remove a book from the shopping cart")
    public void deleteBookById(@PathVariable Long cartItemId) {
        shoppingCartService.deleteById(cartItemId);
    }

}
