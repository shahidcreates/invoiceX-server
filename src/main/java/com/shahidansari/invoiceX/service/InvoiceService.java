package com.shahidansari.invoiceX.service;

import com.shahidansari.invoiceX.entity.Invoice;
import com.shahidansari.invoiceX.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice saveInvoice(Invoice invoice){
        return invoiceRepository.save(invoice);
    }

    public List<Invoice> fetchInvoice(){
        return invoiceRepository.findAll();
    }

    public void removeInvoice(Long invoiceId){
        Invoice existingInvoice = invoiceRepository.findById(invoiceId).
                orElseThrow(()-> new RuntimeException("Invoice not found: "+invoiceId));
        invoiceRepository.delete(existingInvoice);
    }
}
