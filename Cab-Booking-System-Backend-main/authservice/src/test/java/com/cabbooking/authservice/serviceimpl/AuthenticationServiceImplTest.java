package com.cabbooking.authservice.serviceimpl;

import com.cabbooking.authservice.client.DriverClient;
import com.cabbooking.authservice.client.UserClient;
import com.cabbooking.authservice.dto.*;
import com.cabbooking.authservice.entity.User;
import com.cabbooking.authservice.exception.AuthenticationAPIException;
import com.cabbooking.authservice.repository.UserRepository;
import com.cabbooking.authservice.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserClient userClient;
    @Mock
    private DriverClient driverClient;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("login_user_success: should return user JWT response on valid user login")
    void login_user_success() {
        LoginDto loginDto = new LoginDto("user@email.com", "password");
        User user = new User();
        user.setEmail(loginDto.getEmail());
        user.setRole("user");
        Authentication authentication = mock(Authentication.class);
        UserDto userDto = new UserDto();
        userDto.setUserId("user-1");
        userDto.setEmail(loginDto.getEmail());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(userClient.getUserByEmail(loginDto.getEmail())).thenReturn(ResponseEntity.ok(userDto));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");

        JwtResponse response = authenticationService.login(loginDto);
        assertEquals("user", response.getRole());
        assertEquals("User logged in successfully", response.getMessage());
        assertEquals("token", response.getAccessToken());
        assertEquals("user-1", response.getId());
    }

    @Test
    @DisplayName("login_driver_success: should return driver JWT response on valid driver login")
    void login_driver_success() {
        LoginDto loginDto = new LoginDto("driver@email.com", "password");
        User user = new User();
        user.setEmail(loginDto.getEmail());
        user.setRole("driver");
        Authentication authentication = mock(Authentication.class);
        DriverDto driverDto = new DriverDto();
        driverDto.setDriverId("user-1");
        driverDto.setEmail(loginDto.getEmail());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        when(driverClient.getDriverByEmail(loginDto.getEmail())).thenReturn(ResponseEntity.ok(driverDto));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("token");

        JwtResponse response = authenticationService.login(loginDto);
        assertEquals("driver", response.getRole());
        assertEquals("Driver logged in successfully", response.getMessage());
        assertEquals("token", response.getAccessToken());
        assertEquals("user-1", response.getId());
    }

    @Test
    @DisplayName("login_invalid_credentials_throws_exception: should throw AuthenticationAPIException on invalid credentials")
    void login_invalid_credentials_throws_exception() {
        LoginDto loginDto = new LoginDto("invalid@email.com", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new RuntimeException("Bad credentials"));
        assertThrows(AuthenticationAPIException.class, () -> authenticationService.login(loginDto));
    }
}
