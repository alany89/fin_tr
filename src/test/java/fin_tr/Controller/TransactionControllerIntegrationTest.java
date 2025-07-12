package fin_tr.Controller;

import com.tracker.fin_tr.FinTrApplication;
import com.tracker.fin_tr.Transaction.Repository.TransactionRepository;
import com.tracker.fin_tr.Transaction.Transaction;
import com.tracker.fin_tr.User.Repository.UserRepository;
import com.tracker.fin_tr.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FinTrApplication.class)
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    private User testUser;
    @BeforeEach
    void setUp(){
        transactionRepository.deleteAll();
        testUser = new User();
        testUser.setUsername("egorTEST");
        testUser.setPassword("pass");
        testUser.setEmail("test@example.com");
        userRepository.save(testUser);
    }
    @Test
    @WithMockUser(username = "testUser")
    void shouldCreateTransaction() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/add-transaction")
                .param("action", "income")
                .param("category", "FOOD")
                .param("sum", "100")
                .param("date", "07-11-2025")
                .param("opisaniya", "обед")
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/add-transaction"))
                .andExpect(flash().attributeExists("success"));
        assertEquals(1, transactionRepository.count());
        Transaction savedTransaction = transactionRepository.findAll().get(0);
        assertEquals(100, savedTransaction.getSum());
        assertEquals("income", savedTransaction.getAction());
    }
    @Test
    @WithMockUser(username = "testUser")
    void shouldDontCreateTransaction() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/add-transaction")
                .param("action", "income")
                .param("category", "FOOD")
                .param("sum", "100")
                .param("date", "07-11-2025")
                .param("opisaniya", "обед")).andExpect(status().isOk())
                .andExpect(view().name("add-transaction"))
                .andExpect(model().attributeHasErrors("transaction"))
                .andExpect(model().attributeExists("Category"));
        assertEquals(0,transactionRepository.count());
    }
    @Test
    @WithMockUser(username = "unknownuser") // Пользователя нет в БД
    void shouldRedirectToLoginIfUserNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/add-transaction")
                        .param("sum", "100")
                        .param("action", "income")
                        .param("category", "FOOD")
                        .param("date", "2025-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("error"));
    }
}
