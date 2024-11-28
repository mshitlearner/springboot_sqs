package in.mshitlearner.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.mshitlearner.service.SqsMessageReceiverService;
import in.mshitlearner.service.SqsQueueService;
import in.mshitlearner.service.SqsSendMessage;
import in.mshitlearner.util.SqsUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import software.amazon.awssdk.services.sqs.model.Message;

@RestController
@RequestMapping(value = "/sqs")
@CrossOrigin
public class SqsController {

	@Autowired
	SqsQueueService sqsQueueService;
	@Autowired
	SqsSendMessage sqsSendMessage;
	@Autowired
	SqsMessageReceiverService sqsMessageReceiverService;

	@GetMapping(value = "/createStandardQueue")
	@Operation(summary = "Providing the Queue Name for Creating the Standard Queue", description = "Returns a Queue URL")
	public String createStandardQueue(@RequestParam(defaultValue = "myStandardQueue") String queueName) {
		return sqsQueueService.createStandardQueue(queueName);
	}
	
	@GetMapping(value = "/createFifoQueue")
	@Operation(summary = "Providing the Queue Name for Creating the FIFO Queue", description = "Returns a Queue URL")
	public String createFifoQueue(@RequestParam(defaultValue = "myFifoQueue") String queueName) {
		return sqsQueueService.createFifoQueue(queueName);
	}
	
	@GetMapping(value = "/deleteQueue")
	@Operation(summary = "Providing the Queue Name for Deleting", description = "Returns Meta Data as String")
	public String deleteQueue(@Parameter(description = "Provide the Valid Queue Name which is existed") @RequestParam String queueName) {
		return sqsQueueService.deleteQueue(queueName);
	}

	@PostMapping("/sendMessageToStandardQueue")
	@Operation(summary = "Providing the Queue Name for Sending the Message", description = "Returns a Message ID")
	public String sendMessageToStandardQueue(@Parameter(description = "Provide the Queue Name") @RequestParam(defaultValue = "myStandardQueue") String queueName, 
				@Parameter(description = "Provide the Message") @RequestBody String messageBody) {
		return sqsSendMessage.sendMessageToStandardQueue(queueName, messageBody);
	}
	
	@PostMapping("/sendMessageToFifoQueue")
	@Operation(summary = "Providing the Queue Name for Sending the Message", description = "Returns a Message ID")
	public String sendMessageToFifoQueue(@Parameter(description = "Provide the Queue Name") @RequestParam(defaultValue = "myFifoQueue")  String queueName, 
				@Parameter(description = "Provide the Message") @RequestBody String messageBody,
				@Parameter(description = "Provide the Message") @RequestParam String messageGroupId,
	            @RequestParam(required = false) String messageDeduplicationId) {
		// Use current timestamp as default deduplication ID if not provided
		 if (!queueName.endsWith(".fifo")) {
	         queueName = queueName.concat(".fifo");   
	     }
        String deduplicationId = messageDeduplicationId != null ? messageDeduplicationId : String.valueOf(System.currentTimeMillis());
		return sqsSendMessage.sendMessageToFifoQueue(queueName, messageBody,messageGroupId,messageDeduplicationId);
	}
	
	@GetMapping(value = "/getMessages/{queueName}")
	@Operation(summary = "List all the Messages", description = "Listing all the message")
	public void getMessages(@Parameter(description = "Provide the Valid Queue Name which is existed") @PathVariable String queueName) {
		Map<String, List<Message>> messageMap = sqsMessageReceiverService.receiveMessages(queueName);
		String queueUrl = messageMap.keySet().toString();
		List<Message> lstMessage= messageMap.get(queueUrl);
		for(Message msg : lstMessage) {
			System.out.println("Message Id - "+msg.messageId()+"-Message Body-"+msg.body());
		}
	}
	
	@GetMapping(value = "/listOfQueues")
	@Operation(summary = "List Of Queue Urls will be provided", description = "Listing the Queue Urls which are created")
	public List<String> listOfQueues(){
		return sqsQueueService.listOfQueuesDtls();
	}
}
