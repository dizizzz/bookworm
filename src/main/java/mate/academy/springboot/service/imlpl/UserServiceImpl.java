package mate.academy.springboot.service.imlpl;

import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.UserRegistrationRequestDto;
import mate.academy.springboot.dto.UserResponseDto;
import mate.academy.springboot.exception.RegistrationException;
import mate.academy.springboot.mapper.UserMapper;
import mate.academy.springboot.model.User;
import mate.academy.springboot.repository.user.UserRepository;
import mate.academy.springboot.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("Can`t register user by email: " + email);
        }
        User user = userMapper.toModel(requestDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
