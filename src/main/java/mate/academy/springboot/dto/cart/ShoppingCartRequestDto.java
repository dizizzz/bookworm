package mate.academy.springboot.dto.cart;

import lombok.Data;

@Data
public class ShoppingCartRequestDto {
    private Long bookId;
    private int quantity;
}
