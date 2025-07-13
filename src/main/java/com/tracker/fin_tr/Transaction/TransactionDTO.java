package com.tracker.fin_tr.Transaction;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionDTO {
    @NotNull(message = "Дата не может быть пустой")
    private LocalDate date;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 100, message = "Описание не может быть больше 100 символов")
    private String opisaniya;

    @NotNull(message = "Сумма не может быть пустой")
    @Min(value = 1, message = "Сумма должна быть не менее 1")
    private int sum;

    @NotBlank(message = "Тип операции не может быть пустым")
    @Pattern(regexp = "income|expense", message = "Тип операции должен быть 'income' или 'expense'")
    private String action;

    private Transaction.Category category;
}
