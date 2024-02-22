package mate.academy.springboot.dto.cart;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CartItemRequestDto {
    private Long bookId;
    private int quantity;
}
