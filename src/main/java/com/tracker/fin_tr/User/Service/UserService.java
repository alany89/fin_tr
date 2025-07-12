package com.tracker.fin_tr.User.Service;

import com.tracker.fin_tr.User.User;
import com.tracker.fin_tr.User.Repository.UserRepository;
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

    public void registerUserr(String username, String email, String password) {
        if (repository.existsByUsername(username)) {
            throw new IllegalArgumentException("Имя пользователя уже занято");
        }
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email уже используется");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        repository.save(user);
    }
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }


}