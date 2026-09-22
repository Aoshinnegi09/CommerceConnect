package com.commerceconnect.service;

import com.commerceconnect.dto.*;
import com.commerceconnect.entity.*;
import com.commerceconnect.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Mock
    private com.commerceconnect.security.JwtTokenProvider jwtTokenProvider;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldCreateNewCustomerUser() {
        RegisterRequest request = new RegisterRequest("Jane", "Doe", "jane@example.com", "secret123");
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(jwtTokenProvider.generateToken("jane@example.com")).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertEquals("token", response.token());
        assertEquals("jane@example.com", response.email());
        verify(userRepository).save(any(User.class));
    }
}
