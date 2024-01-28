package mate.academy.springboot.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.user.UserRegistrationRequestDto;
import mate.academy.springboot.dto.user.UserResponseDto;
import mate.academy.springboot.exception.RegistrationException;
import mate.academy.springboot.mapper.UserMapper;
import mate.academy.springboot.model.User;
import mate.academy.springboot.repository.user.UserRepository;
import mate.academy.springboot.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new RegistrationException("Can`t register user by email: " + email);
        }
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setLastName(requestDto.getLastName());
        user.setFirstName(requestDto.getFirstName());
        user.setShippingAddress(requestDto.getShippingAddress());

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
