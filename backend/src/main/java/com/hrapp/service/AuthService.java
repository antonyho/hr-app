package com.hrapp.service;

import com.hrapp.dto.AuthResponse;
import com.hrapp.dto.LoginRequest;
import com.hrapp.model.User;
import com.hrapp.repository.UserRepository;
import com.hrapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String token = jwtUtil.generateToken(user.getId(), user.getEmail());
            
            return new AuthResponse(token, user.getId(), user.getEmail(), user.getRole());
            
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}