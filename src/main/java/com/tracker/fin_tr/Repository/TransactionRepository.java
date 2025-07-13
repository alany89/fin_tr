package com.tracker.fin_tr.Repository;

import com.tracker.fin_tr.domain.Transaction;
import com.tracker.fin_tr.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long id);
    List<Transaction> findByUser(User user);
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.action = :action AND t.date BETWEEN :start AND :end")
    List<Transaction> findByYearAndMonth(@Param("user") User user,
                                         @Param("action") String action,
                                         @Param("start") LocalDate start,
                                         @Param("end") LocalDate end);
}
