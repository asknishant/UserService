package com.example.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogoutRequestDTO {
    private String token;
}
