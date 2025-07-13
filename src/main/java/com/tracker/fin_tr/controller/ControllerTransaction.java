package com.tracker.fin_tr.controller;

import com.tracker.fin_tr.Repository.TransactionRepository;
import com.tracker.fin_tr.domain.Transaction;
import com.tracker.fin_tr.domainDTO.TransactionDTO;
import com.tracker.fin_tr.Repository.UserRepository;
import com.tracker.fin_tr.domain.User;
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
        model.addAttribute("transactionDTO", new TransactionDTO());

        return "add-transaction";
    }
    @PostMapping("/add-transaction")
    public String addTransaction(
            @Validated @ModelAttribute("transactionDTO") TransactionDTO transactionDTO ,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()){
            model.addAttribute("Category", Transaction.Category.values());
            return "add-transaction";
        }

        logger.info("POST /transactions - Adding new transaction: {}", transactionDTO);



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
            return "redirect:/login";
        }
        List<Transaction> transactions = transactionRepository.findByUser(user);
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        int totalBalance = 0;
        int BalanceOfRaise = 0;
        int BalanceOfTraty = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getAction().equals("income")) {
                totalBalance += transaction.getSum();
                BalanceOfRaise += transaction.getSum();
            } else {
                totalBalance -= transaction.getSum();
                BalanceOfTraty += transaction.getSum();
            }
        }
        model.addAttribute("transactions", transactions);
        model.addAttribute("totalBalance", totalBalance);
        model.addAttribute("BalanceOfRaise", BalanceOfRaise);
        model.addAttribute("BalanceOfTraty", BalanceOfTraty);
        model.addAttribute("Category", Transaction.Category.values());


        Transaction transaction = new Transaction();
        // Маппим DTO в Entity
        transaction.setDate(transactionDTO.getDate());
        transaction.setOpisaniya(transactionDTO.getOpisaniya());
        transaction.setSum(transactionDTO.getSum());
        transaction.setAction(transactionDTO.getAction());
        transaction.setCategory(transactionDTO.getCategory());
        transaction.setUser(user);

        transaction.setBalanceOfTraty(transactionDTO.getAction().equals("expense") ? transactionDTO.getSum() : 0);
        transaction.setBalanceOfRaise(transactionDTO.getAction().equals("income") ? transactionDTO.getSum() : 0);

        transactionRepository.save(transaction);

        redirectAttributes.addFlashAttribute("success", "Транзакция добавлена!");
        return "redirect:/add-transaction";
    }
}
