package mate.academy.springboot.service;

import mate.academy.springboot.dto.UserRegistrationRequestDto;
import mate.academy.springboot.dto.UserResponseDto;
import mate.academy.springboot.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;
}
