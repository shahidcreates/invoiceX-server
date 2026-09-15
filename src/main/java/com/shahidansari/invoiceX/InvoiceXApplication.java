package com.shahidansari.invoiceX;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class InvoiceXApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceXApplication.class, args);
        System.out.println("Heello");
	}

}
