package com.shahidansari.invoiceX.repository;

import com.shahidansari.invoiceX.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
}
