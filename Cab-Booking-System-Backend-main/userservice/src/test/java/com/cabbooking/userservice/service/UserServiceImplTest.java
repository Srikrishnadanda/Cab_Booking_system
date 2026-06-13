package com.cabbooking.userservice.service;

import com.cabbooking.userservice.config.MapperConfig;
import com.cabbooking.userservice.dto.ForgotPassword;
import com.cabbooking.userservice.dto.UserDto;
import com.cabbooking.userservice.dto.UserRequest;
import com.cabbooking.userservice.dto.UserServiceResponse;
import com.cabbooking.userservice.exception.*;
import com.cabbooking.userservice.model.User;
import com.cabbooking.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private MapperConfig mapperConfig;
    private UserServiceImpl userService;

    private static final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private static final String EMAIL = "saikumar@gmail.com";
    private static final String FULL_NAME = "Sai Kumar";
    private static final String PHONE = "9876543210";
    private static final String GENDER = "Male";

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        modelMapper = new ModelMapper();
        mapperConfig = mock(MapperConfig.class);
        userService = new UserServiceImpl(userRepository, modelMapper, mapperConfig);
    }

    @Test
    @DisplayName("Register user successfully")
    void registerUser_success() {
        UserRequest request = new UserRequest();
        request.setFullName(FULL_NAME);
        request.setEmail(EMAIL);
        request.setPhone(PHONE);
        request.setGender(GENDER);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(request.getPhone())).thenReturn(false);
        when(mapperConfig.generateCode()).thenReturn(5678);

        User user = new User();
        user.setUserId(USER_ID);
        user.setFullName(FULL_NAME);
        user.setEmail(EMAIL);
        user.setPhone(PHONE);
        user.setGender(GENDER);
        user.setCode(5678);
        user.setPassword("securePassword123");

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserServiceResponse response = userService.registerUser(request);

        assertEquals("success", response.getStatus());
        assertEquals("User Registered Successfully", response.getMessage());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Register user throws EmailAlreadyExistsException if email exists")
    void registerUser_emailExists_throwsException() {
        UserRequest request = new UserRequest();
        request.setEmail(EMAIL);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.registerUser(request));
    }

    @Test
    @DisplayName("Get user by ID successfully")
    void getUserById_success() throws UserNotFoundException {
        User user = new User();
        user.setUserId(USER_ID);
        user.setFullName(FULL_NAME);
        user.setEmail(EMAIL);
        user.setPhone(PHONE);
        user.setGender(GENDER);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));

        UserDto dto = userService.getUserById(USER_ID);
        assertEquals(EMAIL, dto.getEmail());
        assertEquals(FULL_NAME, dto.getFullName());
    }

    @Test
    @DisplayName("Get user by ID throws UserNotFoundException if not found")
    void getUserById_notFound_throwsException() {
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(USER_ID));
    }

    @Test
    @DisplayName("Delete user successfully")
    void deleteUser_success() throws UserNotFoundException {
        User user = new User();
        user.setUserId(USER_ID);
        user.setFullName(FULL_NAME);
        user.setEmail(EMAIL);
        user.setPhone(PHONE);
        user.setGender(GENDER);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        String result = userService.deleteUser(EMAIL);
        assertEquals("User Deleted Successfully", result);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Delete user throws UserNotFoundException if not found")
    void deleteUser_notFound_throwsException() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(EMAIL));
    }

    @Test
    @DisplayName("Verify OTP successfully")
    void verifyOtp_success() throws UserNotFoundException {
        User user = new User();
        user.setUserId(USER_ID);
        user.setCode(5678);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));

        SuccessResponse response = userService.verifyOtp(USER_ID, "5678");
        assertEquals("success", response.getStatus());
        assertEquals("Code verified successfully.", response.getMessage());
    }

    @Test
    @DisplayName("Verify OTP throws CodeNotMatchedException if code is invalid")
    void verifyOtp_invalidCode_throwsException() {
        User user = new User();
        user.setUserId(USER_ID);
        user.setCode(5678);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(user));

        assertThrows(CodeNotMatchedException.class, () -> userService.verifyOtp(USER_ID, "9999"));
    }

    @Test
    @DisplayName("Get user by email successfully")
    void getUserByEmail_success() throws UserNotFoundException {
        User user = new User();
        user.setUserId(USER_ID);
        user.setEmail(EMAIL);
        user.setFullName(FULL_NAME);
        user.setPhone(PHONE);
        user.setGender(GENDER);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        UserDto dto = userService.getUserByEmail(EMAIL);
        assertEquals(EMAIL, dto.getEmail());
        assertEquals(FULL_NAME, dto.getFullName());
    }

    @Test
    @DisplayName("Get user by email throws UserNotFoundException if not found")
    void getUserByEmail_notFound_throwsException() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(EMAIL));
    }

    @Test
    @DisplayName("Update user successfully")
    void updateUser_success() throws UserNotFoundException {
        UserRequest request = new UserRequest();
        request.setFullName("Updated Name");
        request.setEmail(EMAIL);
        request.setPhone(PHONE);
        request.setGender(GENDER);

        User existingUser = new User();
        existingUser.setUserId(USER_ID);
        existingUser.setEmail(EMAIL);
        existingUser.setFullName(FULL_NAME);
        existingUser.setPhone(PHONE);
        existingUser.setGender(GENDER);

        User updatedUser = new User();
        updatedUser.setUserId(USER_ID);
        updatedUser.setEmail(EMAIL);
        updatedUser.setFullName("Updated Name");
        updatedUser.setPhone(PHONE);
        updatedUser.setGender(GENDER);

        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserServiceResponse response = userService.updateUser(USER_ID, request);
        assertEquals("success", response.getStatus());
        assertEquals("User Updated Successfully", response.getMessage());
        assertEquals("Updated Name", response.getBody().getFullName());
    }

    @Test
    @DisplayName("Update user throws UserNotFoundException if not found")
    void updateUser_notFound_throwsException() {
        UserRequest request = new UserRequest();
        request.setEmail(EMAIL);
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(USER_ID, request));
    }

    @Test
    @DisplayName("Forgot password successfully")
    void forgotPassword_success() throws UserNotFoundException {
        String newPassword = "newSecurePassword@123";

        User user = new User();
        user.setUserId(USER_ID);
        user.setEmail(EMAIL);
        user.setPassword("oldPassword@123");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail(EMAIL);
        forgotPassword.setNewPassword(newPassword);

        SuccessResponse response = userService.forgotPassword(forgotPassword);
        assertEquals("success", response.getStatus());
        assertEquals("Password updated successfully.", response.getMessage());
    }


    @Test
    @DisplayName("Register user throws PhoneAlreadyExistsException if phone exists")
    void registerUser_phoneExists_throwsException() {
        UserRequest request = new UserRequest();
        request.setEmail("surya@example.com");
        request.setPhone(PHONE);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(request.getPhone())).thenReturn(true);

        assertThrows(PhoneAlreadyExistsException.class, () -> userService.registerUser(request));
    }

    // Java
    @Test
    @DisplayName("Register user throws EmailAlreadyExistsException if both email and phone exist")
    void registerUser_bothEmailAndPhoneExist_throwsEmailException() {
        UserRequest request = new UserRequest();
        request.setEmail(EMAIL);
        request.setPhone(PHONE);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        when(userRepository.existsByPhone(request.getPhone())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.registerUser(request));
    }


    // Java
    @Test
    @DisplayName("Verify OTP throws UserNotFoundException if user not found")
    void verifyOtp_userNotFound_throwsException() {
        when(userRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.verifyOtp(USER_ID, "5678"));
    }

    // Java
    @Test
    @DisplayName("Forgot password throws UserNotFoundException if user not found")
    void forgotPassword_userNotFound_throwsException() {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail("unknown@example.com");
        forgotPassword.setNewPassword("newPassword");

        when(userRepository.findByEmail(forgotPassword.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.forgotPassword(forgotPassword));
    }



}
