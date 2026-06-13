package com.cabbooking.authservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {


    private String fullName;

    private String email;

    private String phone;

    private String password;
    private String gender;

    private Integer code;
    private String role;
}
