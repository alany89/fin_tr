package com.tracker.fin_tr.User.Repository;

import com.tracker.fin_tr.User.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username); // Проверяет существование username
    boolean existsByEmail(String email);      // Проверяет существование email
    User findByUsername(String username);
    User findByEmail(String email);

}
