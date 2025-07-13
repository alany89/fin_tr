package com.tracker.fin_tr.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Entity(name = "Transaction")
@Getter
@Setter
public class Transaction {
    public enum Category{
        FOOD("еда"),
        TRANSPORT("транспорт"),
        ENTERTAINMENT("Развлечения"),
        UTILITIES("Коммунальные услуги"),
        SHOPPING("Покупки"),
        HEALTH("Здоровье"),
        EDUCATION("Образование"),
        SALARY("Зарплата"),
        OTHER("Другое");
        private final String DisplayName;

        Category(String displayName) {
            DisplayName = displayName;
        }

        public String getDisplayName() {
            return DisplayName;
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Дата не может быть пустой")
    private LocalDate date;
    @NotBlank(message = "описание не может быть пустым")
    @Size(max = 100, message = "не может быть больше 100")
    private String opisaniya;
    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount must be at least 1")
    private int sum;
    @NotBlank(message = "описание не может быть пустым")
    @Size(max = 100, message = "не может быть больше 100")
    @Pattern(regexp = "income|expense", message = "Тип операции должен быть 'income' или 'expense'")
    private String action;
    private int BalanceOfTraty;
    private int BalanceOfRaise;
    @Enumerated(EnumType.STRING)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
