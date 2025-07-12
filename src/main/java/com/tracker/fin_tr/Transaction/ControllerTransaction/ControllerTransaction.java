package com.tracker.fin_tr.Transaction.ControllerTransaction;

import com.tracker.fin_tr.Transaction.Repository.TransactionRepository;
import com.tracker.fin_tr.Transaction.Transaction;
import com.tracker.fin_tr.User.Repository.UserRepository;
import com.tracker.fin_tr.User.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@Validated
public class ControllerTransaction {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final static Logger logger= LoggerFactory.getLogger(ControllerTransaction.class);
    @GetMapping("/add-transaction")
    public String transaction(Model model){
        logger.info("GET /transactions - прошел заход на add-transaction");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println(">>> AUTH USER: " + authentication.getName());
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login"; // Перенаправляем, если не аутентифицирован
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());
        if (transactions == null) {
            transactions = new ArrayList<>(); // Защита от null
        }
        int totalBalance = 0;
        int BalanceOfTraty = 0;
        int BalanceOfRaise = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getAction().equals("income")){
                totalBalance += transaction.getSum();
                BalanceOfRaise += transaction.getSum();
            }
            else {
                totalBalance -= transaction.getSum();
                BalanceOfTraty += transaction.getSum();
            }
        }
        model.addAttribute("transactions", transactions);
        model.addAttribute("totalBalance", totalBalance);
        model.addAttribute("BalanceOfRaise", BalanceOfRaise);
        model.addAttribute("BalanceOfTraty", BalanceOfTraty);
        model.addAttribute("Category", Transaction.Category.values());
        model.addAttribute("transaction", new Transaction());

        return "add-transaction";
    }
    @PostMapping("/add-transaction")
    public String addTransaction(
            @Validated @ModelAttribute("transaction") Transaction transaction,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        logger.info("POST /transactions - Adding new transaction: {}", transaction);

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                logger.error("Validation error: {}", error.getDefaultMessage());
            });

            // Добавляем необходимые атрибуты в модель перед возвратом страницы
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);

            List<Transaction> transactions = transactionRepository.findByUserId(user.getId());
            if (transactions == null) {
                transactions = new ArrayList<>();
            }

            int totalBalance = 0;
            int BalanceOfTraty = 0;
            int BalanceOfRaise = 0;
            for (Transaction t : transactions) {
                if (t.getAction().equals("income")){
                    totalBalance += t.getSum();
                    BalanceOfRaise += t.getSum();
                }
                else {
                    totalBalance -= t.getSum();
                    BalanceOfTraty += t.getSum();
                }
            }

            model.addAttribute("transactions", transactions);
            model.addAttribute("totalBalance", totalBalance);
            model.addAttribute("BalanceOfRaise", BalanceOfRaise);
            model.addAttribute("BalanceOfTraty", BalanceOfTraty);
            model.addAttribute("Category", Transaction.Category.values());

            // Обязательно добавляем transaction обратно в модель
            model.addAttribute("transaction", transaction);

            return "add-transaction";
        }

        // Остальной код метода остается без изменений
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
            return "redirect:/login";
        }

        transaction.setUser(user);
        transaction.setBalanceOfTraty(transaction.getAction().equals("expense") ? transaction.getSum() : 0);
        transaction.setBalanceOfRaise(transaction.getAction().equals("income") ? transaction.getSum() : 0);

        transactionRepository.save(transaction);

        redirectAttributes.addFlashAttribute("success", "Транзакция добавлена!");
        return "redirect:/add-transaction";
    }
}
