package mate.academy.springboot.mapper;

import mate.academy.springboot.config.MapperConfig;
import mate.academy.springboot.dto.UserRegistrationRequestDto;
import mate.academy.springboot.dto.UserResponseDto;
import mate.academy.springboot.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(UserRegistrationRequestDto requestDto);
}
