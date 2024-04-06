package com.example.userservice.controllers;

import com.example.userservice.dtos.LoginRequestDTO;
import com.example.userservice.dtos.LogoutRequestDTO;
import com.example.userservice.dtos.SignupRequestDTO;
import com.example.userservice.dtos.SignupResponseDTO;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDTO requestDto) {
        // check if email and password in db
        // if yes create token (use random string) return token
        // else throw some error
        return userService.login(requestDto);
    }

    @PostMapping("/signup")
    public SignupResponseDTO signUp(@RequestBody SignupRequestDTO requestDto) {

        // hash password
        // create user
        // return user
        return toSignUpResponseDto(userService.signup(requestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDTO requestDto) {
        // delete token if exists -> 200
        // if doesn't exist give a 404

        userService.logout(requestDto.getToken());
        return ResponseEntity.ok().build(); // or throw an exception, based on your error handling policy
    }

    public SignupResponseDTO toSignUpResponseDto(User user) {
        if (user == null) {
            return null; // Or throw an exception, based on your error handling policy
        }

        SignupResponseDTO dto = new SignupResponseDTO();
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setEmailVerified(user.isEmailVerified());

        return dto;
    }

    @PostMapping("/validate/{token}")
    public User validateToken(@PathVariable("token") @NonNull String token) {
        return userService.validateToken(token);
    }
}