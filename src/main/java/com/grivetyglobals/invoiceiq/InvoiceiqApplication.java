package com.grivetyglobals.invoiceiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InvoiceiqApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceiqApplication.class, args);
	}

}
