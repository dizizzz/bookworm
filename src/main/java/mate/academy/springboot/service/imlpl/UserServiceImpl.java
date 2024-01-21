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
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can`t register user");
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setShippingAddress(requestDto.getShippingAddress());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
