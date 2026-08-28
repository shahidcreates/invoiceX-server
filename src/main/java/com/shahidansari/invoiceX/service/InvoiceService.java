package com.shahidansari.invoiceX.service;

import com.shahidansari.invoiceX.entity.Invoice;
import com.shahidansari.invoiceX.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Invoice saveInvoice(Invoice invoice){
        return invoiceRepository.save(invoice);
    }
}
