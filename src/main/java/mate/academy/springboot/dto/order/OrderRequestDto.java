package mate.academy.springboot.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequestDto {
    @NotBlank
    private String shoppingAddress;
}
