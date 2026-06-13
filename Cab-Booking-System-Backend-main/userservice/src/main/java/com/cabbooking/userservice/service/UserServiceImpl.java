package com.cabbooking.userservice.service;

import com.cabbooking.userservice.config.MapperConfig;
import com.cabbooking.userservice.dto.ForgotPassword;
import com.cabbooking.userservice.dto.UserDto;
import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserServiceResponse;
import com.cabbooking.userservice.exception.*;
import com.cabbooking.userservice.model.User;
import com.cabbooking.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String SUCCESS = "success";
    private static final String USER_NOT_FOUND = "User not found with ID: {}";
    private static final String USER_WITH_EMAIL="User with email ";
    private static final String USER_WITH_ID="User with ID ";
    private static final String NOT_FOUND=" not found";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final MapperConfig mapperConfig;
//    private final static String SUCCESS="success"; String

    @Override
    public UserServiceResponse registerUser(UserRequest userRequest) {
        log.info("Registering user with email: {}", userRequest.getEmail());

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            log.warn("Email already registered: {}", userRequest.getEmail());
            throw new EmailAlreadyExistsException("Given Email Already Registered: " + userRequest.getEmail());
        }

        if (userRepository.existsByPhone(userRequest.getPhone())) {
            log.warn("Phone number already registered: {}", userRequest.getPhone());
            throw new PhoneAlreadyExistsException("Given Phone number already registered: " + userRequest.getPhone());
        }

        User user = modelMapper.map(userRequest, User.class);
        user.setCode(mapperConfig.generateCode());
        User savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getUserId());

        UserDto userDto = modelMapper.map(savedUser, UserDto.class);
        UserServiceResponse userResponse = new UserServiceResponse();
        userResponse.setBody(userDto);
        userResponse.setStatus(SUCCESS);
        userResponse.setMessage("User Registered Successfully");

        return userResponse;
    }

    @Override
    public UserDto getUserById(String id) throws UserNotFoundException {
        log.info("Fetching user with ID: {}", id);

        User user = userRepository.findByUserId(id)
                .orElseThrow(() -> {
                    log.error(USER_NOT_FOUND, id);
                    return new UserNotFoundException(USER_WITH_ID + id + NOT_FOUND);
                });

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) throws UserNotFoundException {
        log.info("Fetching user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UserNotFoundException(USER_WITH_EMAIL + email + NOT_FOUND);
                });

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserServiceResponse updateUser(String id, UserRequest updateRequest) throws UserNotFoundException {
        User user = userRepository.findByUserId(id)
                .orElseThrow(() -> {
                    log.error(USER_NOT_FOUND, id);
                    return new UserNotFoundException(USER_WITH_ID + id + NOT_FOUND);
                });

        Integer code=user.getCode();
        String email= user.getEmail();

        user.setFullName(updateRequest.getFullName());
        user.setEmail(email);
        user.setPhone(updateRequest.getPhone());
        user.setGender(updateRequest.getGender());
        user.setCode(code);



        userRepository.save(user);

        UserDto userDto = modelMapper.map(user, UserDto.class);
        UserServiceResponse userResponse = new UserServiceResponse();
        userResponse.setBody(userDto);
        userResponse.setStatus(SUCCESS);
        userResponse.setMessage("User Updated Successfully");
        return userResponse;
    }

    @Override
    public String deleteUser(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error(USER_NOT_FOUND, email);
                    return new UserNotFoundException(USER_WITH_EMAIL + email + NOT_FOUND);
                });

        userRepository.delete(user);

        log.info("User deleted successfully with email: {}", email);

        return "User Deleted Successfully";
    }

    @Override
    public SuccessResponse verifyOtp(String userId, String code) throws UserNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error(USER_NOT_FOUND,userId);
                    return new UserNotFoundException(USER_WITH_ID + userId + NOT_FOUND);
                });

        if (!(user.getCode().toString().equals(code))) {

            throw new CodeNotMatchedException("Invalid code.");

        }
        return new SuccessResponse(SUCCESS,"Code verified successfully." );
    }

    @Override
    public SuccessResponse forgotPassword(ForgotPassword forgotPassword) throws UserNotFoundException {
        User user = userRepository.findByEmail(forgotPassword.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", forgotPassword.getEmail());
                    return new UserNotFoundException(USER_WITH_EMAIL + forgotPassword.getEmail() + NOT_FOUND);
                });

        user.setPassword(forgotPassword.getNewPassword());
        userRepository.save(user);
        log.info("Password updated successfully for user with email: {}", forgotPassword.getEmail());
        return new SuccessResponse(SUCCESS,"Password updated successfully." );
    }

}

