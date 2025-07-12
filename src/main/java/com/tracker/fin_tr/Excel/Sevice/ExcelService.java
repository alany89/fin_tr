package com.tracker.fin_tr.Excel.Sevice;

import com.tracker.fin_tr.Category.Controller.ControllerExpense;
import com.tracker.fin_tr.Transaction.Transaction;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
@Service
public class ExcelService {
    private static final Logger log  = LoggerFactory.getLogger(ExcelService.class);
    public byte[] ExcelService(List<Transaction> transactions )throws IOException {
        System.setProperty("java.awt.headless", "true");
        try (Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Transaction");
            Row row = sheet.createRow(0);

            String[] columns = {"дата", "описание", "категория", "cумма", "тип"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(columns[i]);
            }
            int RowNum = 1;
            for (Transaction transaction : transactions) {
                Row row1 = sheet.createRow(RowNum++);
                row1.createCell(0).setCellValue(transaction.getDate());
                row1.createCell(1).setCellValue(transaction.getOpisaniya());
                row1.createCell(2).setCellValue(String.valueOf(transaction.getCategory().getDisplayName()));
                row1.createCell(3).setCellValue(transaction.getSum());
                row1.createCell(4).setCellValue(transaction.getAction());
            }
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            log.info("ExcelService -  работает");
            return outputStream.toByteArray();
        }
    }
}
