package mate.academy.springboot.mapper;

import mate.academy.springboot.config.MapperConfig;
import mate.academy.springboot.dto.order.OrderItemResponseDto;
import mate.academy.springboot.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemsMapper {
    OrderItemResponseDto toDto(OrderItem orderItem);
}
