package com.tracker.fin_tr.service;

import com.tracker.fin_tr.domain.User;
import com.tracker.fin_tr.Repository.UserRepository;
import com.tracker.fin_tr.domainDTO.UserDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public void registerUserr(UserDTO userDTO) {
        if (repository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Имя пользователя уже занято");
        }
        if (repository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email уже используется");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        repository.save(user);
    }
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }


}