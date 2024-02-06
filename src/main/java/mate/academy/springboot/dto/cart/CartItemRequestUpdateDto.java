package mate.academy.springboot.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequestUpdateDto {
    @NotNull
    private int quantity;
}
