package com.tracker.fin_tr.PDF;

import com.itextpdf.text.*;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tracker.fin_tr.Excel.Sevice.ExcelService;
import com.tracker.fin_tr.Transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;



@Service
public class PDFService {
    private static final Logger log  = LoggerFactory.getLogger(PDFService.class);
    public byte[] generatePDF(List<Transaction> transactions) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document,outputStream);
            document.open();
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Paragraph title = new Paragraph("список транзакций", font);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingAfter(10f);
            float[] columns = {20f,20f,20f,20f,20f};
            table.setWidths(columns);
            String[] headers = {"дата", "описание", "категория", "cумма", "тип"};
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,12,BaseColor.BLACK);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header,headerFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5f);
                table.addCell(cell);
            }
            Font dataTransaction = FontFactory.getFont(FontFactory.HELVETICA, 10,BaseColor.BLACK);
            for (Transaction transaction : transactions) {
                table.addCell(createCell(transaction.getDate().toString(), dataTransaction));
                table.addCell(createCell(transaction.getOpisaniya(), dataTransaction));
                table.addCell(createCell(String.valueOf(transaction.getCategory()), dataTransaction));
                table.addCell(createCell(String.valueOf(transaction.getSum()), dataTransaction));
                table.addCell(createCell(transaction.getAction(),dataTransaction));
            }
            document.add(table);
        }finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
        }
        log.error("PDFService - не работает");
        return outputStream.toByteArray();
    }
    private PdfPCell createCell(String content, Font font){
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(5f);
        return cell;
    }
}
