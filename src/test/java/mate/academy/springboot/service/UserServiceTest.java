package mate.academy.springboot.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;
import mate.academy.springboot.dto.user.UserRegistrationRequestDto;
import mate.academy.springboot.dto.user.UserResponseDto;
import mate.academy.springboot.mapper.UserMapper;
import mate.academy.springboot.model.Role;
import mate.academy.springboot.model.RoleName;
import mate.academy.springboot.model.User;
import mate.academy.springboot.repository.role.RoleRepository;
import mate.academy.springboot.repository.user.UserRepository;
import mate.academy.springboot.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private Role role;
    private UserResponseDto userResponseDto;

    @BeforeEach
    public void setup() {
        role = new Role();
        role.setId(1L);
        role.setName(RoleName.USER);

        user = new User();
        user.setId(1L);
        user.setEmail("susanna@email.com");
        user.setPassword("123456789encodedPassword");
        user.setFirstName("Susanna");
        user.setLastName("Smith");
        user.setShippingAddress("123 Main St, City, Country");
        user.setRoles(Set.of(role));

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setShippingAddress(user.getShippingAddress());
    }

    @Test
    @DisplayName("Given correct id, check if user is registered")
    void register_VerifyRegistration() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("susanna@email.com");
        requestDto.setPassword("123456789");
        requestDto.setRepeatPassword("123456789");
        requestDto.setFirstName("Susanna");
        requestDto.setLastName("Smith");
        requestDto.setShippingAddress("123 Main St, City, Country");

        when(passwordEncoder.encode(requestDto.getPassword()))
                .thenReturn("123456789encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto actual = userService.register(requestDto);

        assertNotNull(actual);
        assertEquals(userResponseDto, actual);
    }
}
