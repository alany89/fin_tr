package com.tracker.fin_tr.controller;

import com.tracker.fin_tr.Repository.TransactionRepository;
import com.tracker.fin_tr.domain.Transaction;
import com.tracker.fin_tr.Repository.UserRepository;
import com.tracker.fin_tr.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class ControllerRaise {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    @GetMapping("/raise")
    public String CountOfRaiseShow(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);


        LocalDate now = LocalDate.now();
        int currentYear = year != null ? year : now.getYear();
        int currentMonth = month != null ? month : now.getMonthValue();

        YearMonth currentYearMonth = YearMonth.of(currentYear, currentMonth);
        LocalDate startDate = currentYearMonth.atDay(1);
        LocalDate endDate = currentYearMonth.atEndOfMonth();

        List<Transaction> income = transactionRepository.findByYearAndMonth(user, "income", startDate, endDate);
        Map<Transaction.Category, Integer> incomeByCategory = income.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingInt(Transaction::getSum)
                ));

        int totalIncomes = income.stream().mapToInt(Transaction::getSum).sum();

        model.addAttribute("incomes", income);
        model.addAttribute("incomeByCategory", incomeByCategory);
        model.addAttribute("totalIncome", totalIncomes);
        model.addAttribute("currentYearMonth", currentYearMonth);
        model.addAttribute("prevYearMonth", currentYearMonth.minusMonths(1));
        model.addAttribute("nextYearMonth", currentYearMonth.plusMonths(1));

        return "dohody";
    }
}
