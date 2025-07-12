package com.tracker.fin_tr.Export.Controller;

import com.itextpdf.text.DocumentException;
import com.tracker.fin_tr.Excel.Sevice.ExcelService;
import com.tracker.fin_tr.PDF.PDFService;
import com.tracker.fin_tr.Transaction.Repository.TransactionRepository;
import com.tracker.fin_tr.Transaction.Transaction;
import com.tracker.fin_tr.User.Repository.UserRepository;
import com.tracker.fin_tr.User.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@AllArgsConstructor
public class ExportController {

    private final UserRepository userRepository;
    private final ExcelService excelService;
    private final TransactionRepository transactionRepository;
    private final PDFService pdfService;
    private static final Logger log  = LoggerFactory.getLogger(ExportController.class);
    @GetMapping("/export")
    public String exportShow() {
        return "export";
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        List<Transaction> transactions = getCurrentUserTransactions();
        byte[] excelContent = excelService.ExcelService(transactions);
        log.info("POST /export/excel - экспорт прошёл успешно");
        return ResponseEntity.ok()
                .headers(createExcelHeaders())
                .body(excelContent);
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportToPdf() throws IOException, DocumentException {
        List<Transaction> transactions = getCurrentUserTransactions();
        byte[] pdfContent = pdfService.generatePDF(transactions);
        log.info("POST /export/pdf - экспорт прошёл успешно");
        return ResponseEntity.ok()
                .headers(createPdfHeaders())
                .body(pdfContent);
    }

    private List<Transaction> getCurrentUserTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        return transactionRepository.findByUser(user);
    }

    private HttpHeaders createExcelHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = "transactions_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".xlsx";
        headers.setContentDispositionFormData("attachment", filename);
        return headers;
    }

    private HttpHeaders createPdfHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "transactions_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);
        return headers;
    }
}