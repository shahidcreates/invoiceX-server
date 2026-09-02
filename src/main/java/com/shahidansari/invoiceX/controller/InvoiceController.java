package com.shahidansari.invoiceX.controller;

import com.shahidansari.invoiceX.entity.Invoice;
import com.shahidansari.invoiceX.service.EmailService;
import com.shahidansari.invoiceX.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invoices")
@CrossOrigin("*")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Invoice> saveInvoice(@RequestBody Invoice invoice){
        return ResponseEntity.ok(invoiceService.saveInvoice(invoice));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> fetchInvoices(){
        return ResponseEntity.ok(invoiceService.fetchInvoice());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeInvoice(@PathVariable Long id){
        invoiceService.removeInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sendinvoice")
    public ResponseEntity<?> sendInvoice (@RequestPart("file") MultipartFile file,
                                          @RequestPart("email") String customerEmail){
        try {
            emailService.sendInvoiceMail(customerEmail,file);
            return ResponseEntity.ok().body("Invoice send successfully.");
        } catch (Exception e) {
            System.err.println("Email sending failed:");
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send invoice.");
        }
    }
}
