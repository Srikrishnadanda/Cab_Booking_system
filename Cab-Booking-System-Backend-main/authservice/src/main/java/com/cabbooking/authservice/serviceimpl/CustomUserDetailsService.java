package com.cabbooking.authservice.serviceimpl;

import com.cabbooking.authservice.entity.User;
import com.cabbooking.authservice.exception.UserNotFoundException;
import com.cabbooking.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email){
        User u = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException(email));

        return org.springframework.security.core.userdetails.User.builder().username(u.getEmail()).password(u.getPassword())
                .roles(u.getRole()).build();
    }
}
