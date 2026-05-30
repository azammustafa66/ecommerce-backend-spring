package com.spring.ecommerce.services;

import com.spring.ecommerce.dto.RegisterRequest;
import com.spring.ecommerce.models.AppRole;
import com.spring.ecommerce.models.User;
import com.spring.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterRequest req) {
        boolean ifExists = userRepository.existsByEmail(req.email());
        if (ifExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already registered. Please login");
        }
        User user = User.builder().firstName(req.firstName()).lastName(req.lastName()).email(req.email()).password(passwordEncoder.encode(req.password())).roles(Set.of(AppRole.ROLE_USER)).build();
        userRepository.save(user);
    }
}
