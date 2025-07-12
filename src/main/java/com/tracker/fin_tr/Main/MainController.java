package com.tracker.fin_tr.Main;

import com.tracker.fin_tr.User.Repository.UserRepository;
import com.tracker.fin_tr.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class MainController {
    @Autowired
    private UserRepository repository;

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "home";
    }

}
