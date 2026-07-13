package com.grivetyglobals.invoiceiq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"cloud.s3.endpoint-url=https://mock.s3.endpoint",
		"cloud.s3.region=auto",
		"cloud.s3.bucket-name=mock-bucket",
		"cloud.s3.access-key=mock-access-key",
		"cloud.s3.secret-key=mock-secret-key",
		"SUPABASE_DB_URL=jdbc:h2:mem:testdb",
		"SUPABASE_DB_USER=sa",
		"SUPABASE_DB_PASSWORD=password",
		"JWT_SECRET=mockjwtsecretthathastobelongenoughtobevalid1234567890"
})
class InvoiceiqApplicationTests {

	@Test
	void contextLoads() {
	}

}
