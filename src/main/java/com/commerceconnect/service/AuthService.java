package com.commerceconnect.service;

import com.commerceconnect.dto.AuthResponse;
import com.commerceconnect.dto.LoginRequest;
import com.commerceconnect.dto.RegisterRequest;
import com.commerceconnect.entity.Role;
import com.commerceconnect.entity.User;
import com.commerceconnect.repository.UserRepository;
import com.commerceconnect.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(true);
        user.setRoles(Set.of(Role.CUSTOMER));
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getFirstName(), user.getLastName(), Role.CUSTOMER.name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        String token = jwtTokenProvider.generateToken(user.getEmail());
        String role = user.getRoles().isEmpty() ? "CUSTOMER" : user.getRoles().iterator().next().name();
        return new AuthResponse(token, user.getEmail(), user.getFirstName(), user.getLastName(), role);
    }
}
