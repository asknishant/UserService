package com.example.userservice.services;


import com.example.userservice.dtos.LoginRequestDTO;
import com.example.userservice.dtos.SignupRequestDTO;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

//ideally should be an interface
@Service
public class UserService {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    public  UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository, TokenRepository tokenRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public User signup(SignupRequestDTO signupRequestDTO) {
        if(userRepository.findByEmail(signupRequestDTO.getEmail()).isPresent()) {
            return null;
        }
        User newUser = User.builder()
                .hashedPassword(bCryptPasswordEncoder.encode(signupRequestDTO.getPassword()))
                .email(signupRequestDTO.getEmail())
                .name(signupRequestDTO.getName())
                .build();

        return userRepository.save(newUser);
    }

    public Token login(LoginRequestDTO loginRequestDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequestDTO.getEmail());
        if(userOptional.isEmpty()) {
            return null;
        }
        if(bCryptPasswordEncoder.matches(loginRequestDTO.getPassword(), userOptional.get().getHashedPassword())) {
            Token token = new Token();
            token.setUser(userOptional.get());
            token.setValue(RandomStringUtils.randomAlphabetic(128));
            LocalDate today = LocalDate.now();
            LocalDate onedayLater = today.plusDays(1);
            Date expiryAt = Date.from(onedayLater.atStartOfDay(ZoneId.systemDefault()).toInstant());
            token.setExpiryAt(expiryAt);
            return tokenRepository.save(token);
        }
        return null;
    }

    public void logout(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByValueAndDeleted(token, false);
        if(tokenOptional.isPresent()) {
            Token token1 = tokenOptional.get();
            token1.setDeleted(true);
            tokenRepository.save(token1);
        }
    }

    public User validateToken(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByValueAndDeleted(token, false);
        if(tokenOptional.isEmpty()) {
            return null;
        }

        if(tokenOptional.get().getExpiryAt().before(new Date())) {
            tokenOptional.get().setDeleted(true);
            tokenRepository.save(tokenOptional.get());
            return null;
        }
        return null;
    }
}
