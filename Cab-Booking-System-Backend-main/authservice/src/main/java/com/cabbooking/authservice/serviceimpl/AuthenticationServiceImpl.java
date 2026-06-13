package com.cabbooking.authservice.serviceimpl;

import com.cabbooking.authservice.client.DriverClient;
import com.cabbooking.authservice.client.UserClient;
import com.cabbooking.authservice.dto.*;
import com.cabbooking.authservice.entity.User;
import com.cabbooking.authservice.exception.AuthenticationAPIException;
import com.cabbooking.authservice.repository.UserRepository;
import com.cabbooking.authservice.security.JwtTokenProvider;
import com.cabbooking.authservice.service.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserClient userClient;
    private final DriverClient driverClient;

    private static final String USER_NOT_FOUND_MESSAGE = "User with that email does not exist.";
    private static final String ROLE_DRIVER = "driver";


    @Override
    public JwtResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(), loginDto.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            JwtResponse jwtResponse = new JwtResponse();
            log.info("Authentication successful for email: {}", loginDto.getEmail());
            User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new AuthenticationAPIException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
            log.info("User found: {}", user);
            if (user.getRole().equalsIgnoreCase(ROLE_DRIVER)) {
                ResponseEntity<DriverDto> driverDtoResponse = driverClient.getDriverByEmail(user.getEmail());
                DriverDto driverDto = driverDtoResponse.getBody();
                jwtResponse.setId(driverDto.getDriverId());

            } else {
                ResponseEntity<UserDto> userDtoResponse = userClient.getUserByEmail(user.getEmail());
                UserDto userDto = userDtoResponse.getBody();
                jwtResponse.setId(userDto.getUserId());
            }
            jwtResponse.setAccessToken(jwtTokenProvider.generateToken(authentication));
            jwtResponse.setRole(user.getRole());
            jwtResponse.setMessage(user.getRole().equalsIgnoreCase("user") ? "User logged in successfully" : "Driver logged in successfully");
            return jwtResponse;

        } catch (Exception e) {
            log.info("Error during authentication for email: {}: {}", loginDto.getEmail(), e.getMessage());
            throw new AuthenticationAPIException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    @Override
    public UserServiceResponse registerUser(UserRequest userRequest) {


        if (Boolean.TRUE.equals(userRepository.existsByEmail(userRequest.getEmail()))) {
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "Given Email Already Registered: " + userRequest.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(hashedPassword);

        ResponseEntity<UserServiceResponse> userServiceResponse = userClient.registerUser(userRequest);

        if (userServiceResponse.getStatusCode() == HttpStatus.CREATED && userServiceResponse.getBody() != null) {
            UserServiceResponse responseBody = userServiceResponse.getBody();
            UserDto userDto = null;
            if (responseBody != null) {
                userDto = responseBody.getBody();
            }
            if (userDto == null) {
                throw new AuthenticationAPIException(HttpStatus.INTERNAL_SERVER_ERROR, "User details not found in response.");
            }


            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setRole(userRequest.getRole());
            user.setPassword(hashedPassword);
            userRepository.save(user);
            return userServiceResponse.getBody();
        }

        return userServiceResponse.getBody();
    }

    @Override
    public DriverServiceResponse registerDriver(DriverRequest driverRequest) {

        if(Boolean.TRUE.equals(userRepository.existsByEmail(driverRequest.getEmail()))) {
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "Given Email Already Registered: " + driverRequest.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(driverRequest.getPassword());
        driverRequest.setPassword(hashedPassword);

        ResponseEntity<DriverServiceResponse> driverResponse = driverClient.registerDriver(driverRequest);

        if (driverResponse.getStatusCode() == HttpStatus.CREATED && driverResponse.getBody() != null) {

            DriverServiceResponse driverServiceResponse = driverResponse.getBody();
            DriverDto driverDto = null;
            if (driverServiceResponse != null) {
                driverDto = driverServiceResponse.getBody();
            }
            if (driverDto == null) {
                throw new AuthenticationAPIException(HttpStatus.INTERNAL_SERVER_ERROR, "Driver details not found in response.");
            }


            User user = new User();
            user.setEmail(driverDto.getEmail());
            user.setRole(driverRequest.getRole());
            user.setPassword(hashedPassword);
            userRepository.save(user);
            return driverResponse.getBody();
        }

        return driverResponse.getBody();
    }

    @Override
    public PasswordResetResponse resetPassword(ForgotPassword forgotPassword) {

        User u =  userRepository.findByEmail(forgotPassword.getEmail()).orElseThrow(()-> new AuthenticationAPIException(HttpStatus.BAD_REQUEST, USER_NOT_FOUND_MESSAGE));

        String role = u.getRole();

        String email = forgotPassword.getEmail();
        String newPassword = forgotPassword.getNewPassword();
        String encodedPassword = passwordEncoder.encode(newPassword);
        forgotPassword.setNewPassword(encodedPassword);
        String status;
        String message;
        try {
            if (ROLE_DRIVER.equalsIgnoreCase(role)) {
                User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "Driver with that email does not exist."));
                user.setPassword(encodedPassword);
                userRepository.save(user);
                driverClient.forgotPassword(forgotPassword);
                status = "success";
                message = "Driver password reset successfully";
            } else {
                User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthenticationAPIException(HttpStatus.BAD_REQUEST, USER_NOT_FOUND_MESSAGE));
                user.setPassword(encodedPassword);
                userRepository.save(user);
                userClient.forgotPassword(forgotPassword);
                status = "success";
                message = "User password reset successfully";
            }
        } catch (Exception e) {
            status = "error";
            message = e.getMessage();
        }
        return new PasswordResetResponse(status, message, LocalDateTime.now());
    }

    @Override
    @Transactional
    public String deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationAPIException(HttpStatus.BAD_REQUEST, USER_NOT_FOUND_MESSAGE));

        String role = user.getRole();
        ResponseEntity<String> response;

        if (ROLE_DRIVER.equalsIgnoreCase(role)) {
            response = driverClient.deleteDriver(email);
            if(response.getStatusCode() == HttpStatus.OK) {
                userRepository.deleteByEmail(email);
            }
        } else {
            response = userClient.deleteUser(email);
            if(response.getStatusCode() == HttpStatus.OK) {
                userRepository.deleteByEmail(email);
            }
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            userRepository.deleteByEmail(email);
        }
        return response.getBody();
    }


    @Override
    public Boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token.substring(7));
    }
}
