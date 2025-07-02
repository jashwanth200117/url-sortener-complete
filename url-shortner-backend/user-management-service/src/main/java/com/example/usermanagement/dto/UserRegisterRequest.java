package com.example.usermanagement.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String password;
}