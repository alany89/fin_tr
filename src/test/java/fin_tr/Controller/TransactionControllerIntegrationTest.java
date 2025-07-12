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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.PatternMatchUtils;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FinTrApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldCreateTransaction() throws Exception {
        mockMvc.perform(post("/add-transaction")
                        .param("action", "income")
                        .param("category", "FOOD")
                        .param("sum", "100.50")
                        .param("date", "2025-07-12")
                        .param("description", "Test transaction")
                        .with(csrf()))
                .andExpect(status().isOk()) // Изменили на isOk()
                .andExpect(content().string(containsString("success")));
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldNotCreateInvalidTransaction() throws Exception {
        mockMvc.perform(post("/add-transaction")
                        .param("action", "")
                        .param("sum", "0")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-transaction"))
                .andExpect(model().attributeHasErrors("transaction"));
    }

    @Test
    void shouldReturnUnauthorizedForUnauthenticatedUser() throws Exception {
        mockMvc.perform(post("/add-transaction")
                        .param("sum", "100")
                        .param("action", "income")
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); // Ожидаем 401
    }

}