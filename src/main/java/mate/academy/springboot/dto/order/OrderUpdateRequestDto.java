package mate.academy.springboot.dto.order;

import jakarta.validation.constraints.NotBlank;

public record OrderUpdateRequestDto(
        @NotBlank
        String status
) {
}
