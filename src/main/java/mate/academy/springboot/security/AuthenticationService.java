package mate.academy.springboot.security;

import lombok.RequiredArgsConstructor;
import mate.academy.springboot.dto.user.UserLoginRequestDto;
import mate.academy.springboot.dto.user.UserLoginResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUnit jwtUnit;
    private final AuthenticationManager authenticationManager;

    public UserLoginResponseDto authentication(UserLoginRequestDto requestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.email(), requestDto.password()
                )
        );
        String token = jwtUnit.generateToken(authentication.getName());
        return new UserLoginResponseDto(token);
    }
}
