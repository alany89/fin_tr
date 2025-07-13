package com.tracker.fin_tr.service;


import com.tracker.fin_tr.Repository.TransactionRepository;
import com.tracker.fin_tr.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class); // <-- Добавили логгер
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Iterable<Transaction> getAllTransactions() {
        logger.debug("Getting all transactions from DB"); // <-- Логируем действие
        return transactionRepository.findAll();
    }

    public Transaction addTransaction(Transaction transaction) {
        logger.debug("Adding transaction to DB: {}", transaction); // <-- Логируем данные
        return transactionRepository.save(transaction);
    }



}
