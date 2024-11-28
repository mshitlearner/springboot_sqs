package in.mshitlearner.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.mshitlearner.util.SqsUtil;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Service
public class SqsMessageReceiverService {
	
	@Autowired
	SqsClient sqsClient;
	
	@Autowired
	SqsUtil sqlUtil;
	
    //public List<Message> receiveMessages(String queueName) {
    public Map<String, List<Message>> receiveMessages(String queueName) {	
    	// Inject the URL of the queue you want to poll
       String queueUrl = sqlUtil.getQueueUrl(queueName);
       Map<String, List<Message>> queueDtlsMap = new HashMap<String, List<Message>>();		
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)        // Adjust the number of messages to retrieve
                .waitTimeSeconds(20)            // Long polling (max 20 seconds)
                .build();

        ReceiveMessageResponse response = sqsClient.receiveMessage(receiveMessageRequest);
        queueDtlsMap.put(queueUrl, response.messages());
        return queueDtlsMap;
    }
}
