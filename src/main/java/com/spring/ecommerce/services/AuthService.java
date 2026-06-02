package com.spring.ecommerce.services;

import com.spring.ecommerce.dto.LoginRequest;
import com.spring.ecommerce.dto.LoginResponse;
import com.spring.ecommerce.dto.RefreshTokenRequest;
import com.spring.ecommerce.dto.RegisterRequest;
import com.spring.ecommerce.models.AppRole;
import com.spring.ecommerce.models.RefreshToken;
import com.spring.ecommerce.models.User;
import com.spring.ecommerce.repositories.RefreshTokenRepository;
import com.spring.ecommerce.repositories.UserRepository;
import com.spring.ecommerce.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetails;

    public void registerUser(RegisterRequest req) {
        boolean ifExists = userRepository.existsByEmail(req.email());
        if (ifExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already registered. Please login");
        }
        User user = User.builder().firstName(req.firstName()).lastName(req.lastName()).email(req.email()).password(passwordEncoder.encode(req.password())).roles(Set.of(AppRole.ROLE_USER)).build();
        userRepository.save(user);
    }

    public LoginResponse loginUser(LoginRequest req) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            String accessToken = jwtService.generateAccessToken(userDetails);
            String raw = jwtService.generateRefreshToken();
            String hash = jwtService.hashToken(raw);

            RefreshToken refreshToken = RefreshToken.builder()
                    .tokenHash(hash)
                    .user(user)
                    .expiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenExpiration()))
                    .revoked(false)
                    .build();
            refreshTokenRepository.save(refreshToken);

            return new LoginResponse("Bearer", accessToken, raw);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest req) {
        String hash = jwtService.hashToken(req.refreshToken());
        RefreshToken existing = refreshTokenRepository.findByTokenHash(hash).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));
        if (existing.isRevoked()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is revoked");
        }
        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is expired");
        }
        existing.setRevoked(true);

        UserDetails ud = userDetails.loadUserByUsername(existing.getUser().getEmail());
        String newAccessToken = jwtService.generateAccessToken(ud);
        String newRaw = jwtService.generateRefreshToken();
        String newHash = jwtService.hashToken(newRaw);

        RefreshToken newRefreshToken = RefreshToken.builder().tokenHash(newHash).user(existing.getUser()).expiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenExpiration()))
                .revoked(false).build();
        refreshTokenRepository.save(newRefreshToken);

        existing.setReplacedByHash(newHash);
        refreshTokenRepository.save(existing);

        return new LoginResponse("Bearer", newAccessToken, newRaw);
    }

    @Transactional
    public void logoutUser(RefreshTokenRequest req) {
        String hash = jwtService.hashToken(req.refreshToken());
        refreshTokenRepository.findByTokenHash(hash).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }
}
