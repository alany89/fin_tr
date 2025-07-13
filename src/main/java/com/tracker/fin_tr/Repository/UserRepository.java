package com.tracker.fin_tr.Repository;

import com.tracker.fin_tr.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username); // Проверяет существование username
    boolean existsByEmail(String email);      // Проверяет существование email
    User findByUsername(String username);
    User findByEmail(String email);

}
