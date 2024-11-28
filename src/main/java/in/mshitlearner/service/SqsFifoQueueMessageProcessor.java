package in.mshitlearner.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sqs.model.Message;

@Service
public class SqsFifoQueueMessageProcessor {
	
	@Autowired
	SqsMessageReceiverService sqsMessageReceiverService;
	
	@Scheduled(fixedDelay = 5000) // Poll every 5 seconds
	public void pollMessages() {
		String queueName = "myFifoQueue.fifo";
		if (queueName != null) {
			Map<String, List<Message>> messageMap = sqsMessageReceiverService.receiveMessages(queueName);
			if (messageMap != null) {
				Map.Entry<String, List<Message>> entry = messageMap.entrySet().iterator().next();
				String queueUrl = entry.getKey();
				List<Message> lstMessages = entry.getValue();
				for (Message message : lstMessages) {
					// Process the message here...
					System.out.println("Message ID -" + message.messageId() + "---Message --- " + message.body());
					// After processing, delete the message (optional)
					deleteMessage(queueUrl, message);
				}
			}
		}
	}

	private void deleteMessage(String queueUrl, Message message) {
		sqsMessageReceiverService.sqsClient
				.deleteMessage(builder -> builder.queueUrl(queueUrl).receiptHandle(message.receiptHandle()));
		System.out.println("Message deleted from queue.");
	}
}
