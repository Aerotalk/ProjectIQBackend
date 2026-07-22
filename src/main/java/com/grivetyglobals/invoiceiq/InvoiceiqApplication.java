package com.grivetyglobals.invoiceiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class InvoiceiqApplication {

	public static void main(String[] args) {
		// Strip literal double quotes from environment variables injected by Railway/Render
		System.getenv().forEach((key, val) -> {
			if (val != null && val.startsWith("\"") && val.endsWith("\"") && val.length() > 1) {
				String cleaned = val.substring(1, val.length() - 1);
				System.setProperty(key, cleaned);
			}
		});
		SpringApplication.run(InvoiceiqApplication.class, args);
	}

}
