package com.tracker.fin_tr.User.Controller;

import com.tracker.fin_tr.User.LoginDTO;
import com.tracker.fin_tr.User.Repository.UserRepository;
import com.tracker.fin_tr.User.User;
import com.tracker.fin_tr.User.Service.UserService;
import com.tracker.fin_tr.User.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
            @Valid @ModelAttribute("userDTO")UserDTO userDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
            ) {
        if(bindingResult.hasErrors()) return "register";
        try {
            userService.registerUserr(userDTO);
            log.info("POST /register Регистрация прошла успешно: {}", userDTO.getUsername());
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDTO.getUsername(),
                userDTO.getPassword()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("POST /register Пользователь зареган: {}", userDTO.getUsername());
            return "redirect:/login";
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
            @Valid @ModelAttribute("LoginDTO") LoginDTO loginDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes

    ) {
        if (bindingResult.hasErrors()) return "login";

        User user = repository.findByUsername(loginDTO.getUsername());

        // 2. Если пользователь не найден или пароль неверный
        if (user == null || !userService.checkPassword(user, loginDTO.getPassword())) {
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
