package in.mshitlearner.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.mshitlearner.util.SqsUtil;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;

@Service
public class SqsQueueService {

	@Autowired
	SqsClient sqsClient;

	@Autowired
	SqsUtil sqsUtil;

	public String createStandardQueue(String queueName) {
		// Below Configuration is related to the Standard Queue Creation
		// Creating the Map for Queue attributes
		Map<String, String> queueAttributes = new HashMap<>();
		queueAttributes.put("DelaySeconds", "0");
		queueAttributes.put("MaximumMessageSize", "262144");
		queueAttributes.put("MessageRetentionPeriod", "345600");
		queueAttributes.put("ReceiveMessageWaitTimeSeconds", "0");
		// queueAttributes.put("FifoQueue", "false");

		CreateQueueRequest createQueueRequest = CreateQueueRequest.builder().queueName(queueName)
				.attributesWithStrings(queueAttributes).build();
		CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
		return createQueueResponse.queueUrl();
	}
	
	public String createFifoQueue(String queueName) {
		// Below Configuration is related to the FIFO Queue Creation
		// Creating the Map for Queue attributes
		 if (!queueName.endsWith(".fifo")) {
			 //FIFO queue names must end with '.fifo'
	         queueName = queueName.concat(".fifo");   
			 //throw new IllegalArgumentException("FIFO queue names must end with '.fifo'");
	     }
		 
		Map<String, String> queueAttributes = new HashMap<>();
		queueAttributes.put("DelaySeconds", "0");
		queueAttributes.put("MaximumMessageSize", "262144");
		queueAttributes.put("MessageRetentionPeriod", "345600");
		queueAttributes.put("ReceiveMessageWaitTimeSeconds", "0");
		queueAttributes.put("ContentBasedDeduplication", "true"); // Optional, enables automatic deduplication
		queueAttributes.put("FifoQueue", "true");

		CreateQueueRequest createQueueRequest = CreateQueueRequest.builder().queueName(queueName)
				.attributesWithStrings(queueAttributes).build();
		CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
		return createQueueResponse.queueUrl();
	}

	public String deleteQueue(String queueName) {
		String queueUrl = sqsUtil.getQueueUrl(queueName);
		if (queueUrl != null) {
			DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder().queueUrl(queueUrl).build();
			DeleteQueueResponse deleteQueueResponse = sqsClient.deleteQueue(deleteQueueRequest);
			return deleteQueueResponse.responseMetadata().requestId();
		}
		return null;
	}
	
	public List<String> listOfQueuesDtls(){
		// Build the ListQueuesRequest
        ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().build();
        // Fetch all queues
        ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);
     // Get the queue URLs and extract the names from the URLs if necessary
        List<String> queueUrls = listQueuesResponse.queueUrls();
        // Extracting just the queue names if needed
        List<String> queueNames = queueUrls.stream()
                .map(url -> url.substring(url.lastIndexOf('/') + 1))
                .toList();

        return queueNames;
	}
}
