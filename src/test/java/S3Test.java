import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class S3Test {
    @Test
    public void testS3Upload() throws Exception {
        System.out.println("    Reading .env file...");
        List<String> lines = Files.readAllLines(Paths.get(".env"));
        String endpoint = "", region = "", accessKey = "", secretKey = "", bucketName = "";

        for (String line : lines) {
            if (line.startsWith("S3_ENDPOINT_URL=")) endpoint = line.split("=", 2)[1].trim();
            if (line.startsWith("S3_REGION=")) region = line.split("=", 2)[1].trim();
            if (line.startsWith("S3_ACCESS_KEY_ID=")) accessKey = line.split("=", 2)[1].trim();
            if (line.startsWith("S3_SECRET_ACCESS_KEY=")) secretKey = line.split("=", 2)[1].trim();
            if (line.startsWith("S3_BUCKET_NAME=")) bucketName = line.split("=", 2)[1].trim();
        }

        // Remove quotes if present
        endpoint = endpoint.replace("\"", "");
        region = region.replace("\"", "");
        accessKey = accessKey.replace("\"", "");
        secretKey = secretKey.replace("\"", "");
        bucketName = bucketName.replace("\"", "");

        System.out.println("    Endpoint: " + endpoint);
        System.out.println("    Region: " + region);
        System.out.println("    Bucket: " + bucketName);

        System.out.println("    Building S3Client...");
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(serviceConfiguration)
                .build();

        String testContent = "Hello from automated test at " + System.currentTimeMillis();
        String testKey = "test-upload-" + System.currentTimeMillis() + ".txt";

        System.out.println("    Uploading test file to S3...");
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(testKey)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(testContent.getBytes()));

        System.out.println("    UPLOAD SUCCESSFUL! Signature calculation was perfect.");
    }
}
