package mate.academy.springboot.mapper;

import mate.academy.springboot.config.MapperConfig;
import mate.academy.springboot.dto.cart.ShoppingCartRequestDto;
import mate.academy.springboot.dto.cart.ShoppingCartResponseDto;
import mate.academy.springboot.model.ShoppingCart;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);

    ShoppingCart toModel(ShoppingCartRequestDto requestDto);
}
