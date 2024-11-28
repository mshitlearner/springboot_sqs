package in.mshitlearner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.mshitlearner.util.SqsUtil;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
public class SqsSendMessage {

	@Autowired
	SqsClient sqsClient;
	
	@Autowired
	SqsUtil sqlUtil;

	public String sendMessageToStandardQueue(String queueName, String msgBody) {
		String queueUrl =sqlUtil. getQueueUrl(queueName);
		if(queueUrl != null) {
			SendMessageRequest msgRequest = SendMessageRequest.builder().queueUrl(queueUrl).messageBody(msgBody)
					.delaySeconds(5).build();
			SendMessageResponse msgResponse = sqsClient.sendMessage(msgRequest);
			return msgResponse.messageId();
		}
		return "Provide a valid queue name";
	}
	
	public String sendMessageToFifoQueue(String queueName, String msgBody,String messageGroupId,String messageDeduplicationId) {
		String queueUrl =sqlUtil. getQueueUrl(queueName);
		if(queueUrl != null) {
			SendMessageRequest msgRequest = SendMessageRequest.builder().queueUrl(queueUrl).messageBody(msgBody)
					.messageGroupId(messageGroupId)
					.messageDeduplicationId(messageDeduplicationId)
					.build();
			SendMessageResponse msgResponse = sqsClient.sendMessage(msgRequest);
			return msgResponse.messageId();
		}
		return "Provide a valid queue name";
	}

	
}
