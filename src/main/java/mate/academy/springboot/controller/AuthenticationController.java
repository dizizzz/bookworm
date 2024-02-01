package mate.academy.springboot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.user.UserLoginRequestDto;
import mate.academy.springboot.dto.user.UserLoginResponseDto;
import mate.academy.springboot.dto.user.UserRegistrationRequestDto;
import mate.academy.springboot.dto.user.UserResponseDto;
import mate.academy.springboot.exception.RegistrationException;
import mate.academy.springboot.security.AuthenticationService;
import mate.academy.springboot.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management", description = "Endpoints for managing authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate the user", description = "Authenticate the user")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto) {
        return authenticationService.authentication(requestDto);
    }

    @PostMapping("/registration")
    @Operation(summary = "Register the user", description = "Register the user")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }
}
