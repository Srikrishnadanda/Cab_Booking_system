package com.cabbooking.userservice.service;

import com.cabbooking.userservice.dto.ForgotPassword;
import com.cabbooking.userservice.dto.UserDto;
import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserServiceResponse;
import com.cabbooking.userservice.exception.SuccessResponse;
import com.cabbooking.userservice.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface UserService
{
    public UserServiceResponse registerUser(UserRequest userRequest);

    UserDto getUserById(String id) throws UserNotFoundException;

    UserDto getUserByEmail(String email) throws UserNotFoundException;

    UserServiceResponse updateUser(String id, UserRequest userRequest) throws UserNotFoundException;

    String deleteUser(String email) throws UserNotFoundException;

    SuccessResponse verifyOtp(String userId, String code) throws UserNotFoundException;

    SuccessResponse forgotPassword(ForgotPassword forgotPassword) throws UserNotFoundException;
}
