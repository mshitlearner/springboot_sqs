package in.mshitlearner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsSqsConfig {
	
	@Value("${aws.accessKeyId}")
	private String accessKey;

	@Value("${aws.secretKey}")
	private String accessSecret;

	@Bean
	public SqsClient sqsClient() {
		return SqsClient.builder().region(Region.AP_SOUTH_1) // Set your region
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, accessSecret))).build();
	}
}
