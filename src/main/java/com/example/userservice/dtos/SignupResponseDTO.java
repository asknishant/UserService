package com.example.userservice.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupResponseDTO {
    private String email;
    private String name;
    private boolean isEmailVerified;
}