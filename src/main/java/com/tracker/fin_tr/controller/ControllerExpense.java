package com.tracker.fin_tr.controller;

import com.tracker.fin_tr.Repository.TransactionRepository;
import com.tracker.fin_tr.domain.Transaction;
import com.tracker.fin_tr.Repository.UserRepository;
import com.tracker.fin_tr.domain.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class ControllerExpense {
    private static final Logger log  = LoggerFactory.getLogger(ControllerExpense.class);
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @GetMapping("/expense")
    public String countOfExpense(Model model,
                                 @RequestParam(required = false) Integer year,
                                 @RequestParam(required = false) Integer month) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);

        LocalDate now = LocalDate.now();
        int currentYear = year != null ? year : now.getYear();
        int currentMonth = month != null ? month : now.getMonthValue();

        YearMonth currentYearMonth = YearMonth.of(currentYear, currentMonth);
        YearMonth prevYearMonth = currentYearMonth.minusMonths(1);
        YearMonth nextYearMonth = currentYearMonth.plusMonths(1);

        LocalDate startDate = currentYearMonth.atDay(1);
        LocalDate endDate = currentYearMonth.atEndOfMonth();

        List<Transaction> expenses = transactionRepository.findByYearAndMonth(user, "expense", startDate, endDate);
        Map<Transaction.Category, Integer> expenseByCategories = expenses.stream().collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingInt(Transaction::getSum)
        ));

        int totalExpense = expenses.stream().mapToInt(Transaction::getSum).sum();

        model.addAttribute("expenses", expenses);
        model.addAttribute("expenseByCategories", expenseByCategories);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("startDate", startDate);
        model.addAttribute("currentYearMonth", currentYearMonth);
        model.addAttribute("prevYearMonth", prevYearMonth);
        model.addAttribute("nextYearMonth", nextYearMonth);
        log.error("ControllerExpense - Счёт трат не работает");
        return "expense";

    }
}