package com.bhagavan.lastwish.service;

import com.bhagavan.lastwish.dto.AuthDtos;
import com.bhagavan.lastwish.model.User;
import com.bhagavan.lastwish.repository.UserRepository;
import com.bhagavan.lastwish.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository repo; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo=repo; this.encoder=encoder; this.jwt=jwt;
    }
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest r) {
        if (repo.existsByEmail(r.email())) throw new IllegalArgumentException("Email already registered");
        User u=new User(); u.setName(r.name()); u.setEmail(r.email().toLowerCase());
        u.setPasswordHash(encoder.encode(r.password())); repo.save(u);
        return new AuthDtos.AuthResponse(jwt.generate(u.getId(),u.getEmail()),u.getId(),u.getName(),u.getEmail());
    }
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest r) {
        User u=repo.findByEmail(r.email().toLowerCase());
        if (u==null || !encoder.matches(r.password(),u.getPasswordHash()))
            throw new IllegalArgumentException("Invalid email or password");
        return new AuthDtos.AuthResponse(jwt.generate(u.getId(),u.getEmail()),u.getId(),u.getName(),u.getEmail());
    }
}
