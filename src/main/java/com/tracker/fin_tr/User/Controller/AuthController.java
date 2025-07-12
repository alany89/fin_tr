package com.tracker.fin_tr.User.Controller;

import com.tracker.fin_tr.User.Repository.UserRepository;
import com.tracker.fin_tr.User.User;
import com.tracker.fin_tr.User.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository repository;
    private final UserService userService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            RedirectAttributes redirectAttributes,
            @ModelAttribute User user
    ) {
        try {
            userService.registerUserr(username, email, password);
            log.info("POST /register Регистрация прошла успешно: {}", user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("POST /register Пользователь зареган: {}", username);
            return "redirect:/home";
        } catch (Exception e) {
            log.error("POST /register - Ошибка при создании пользователя: {}", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            log.error("GET /login - Ошибка при захода пользователя");
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {
        // 1. Находим пользователя в БД
        User user = repository.findByUsername(username);

        // 2. Если пользователь не найден или пароль неверный
        if (user == null || !userService.checkPassword(user, password)) {
            redirectAttributes.addAttribute("error", true);
            log.error("проблема регистрации");
            return "redirect:/login";
        }

        // 3. Создаём аутентификацию и устанавливаем её
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // Роли (пока пусто)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("логин работает");
        // 4. Перенаправляем на /home
        return "redirect:/home";
    }
    }
