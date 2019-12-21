import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SQSHandler {

    private AmazonSQS sqs;

    SQSHandler() {
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new ProfileCredentialsProvider().getCredentials());
        this.sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("us-east-1")
                .build();
    }


    public AmazonSQS getSqs() {
        return sqs;
    }


    String createQueue(String QueueNamePrefix) {

        try {
            // Create a queue
            System.out.println("Creating a new SQS queue called " + QueueNamePrefix + ".\n");
            CreateQueueRequest createQueueRequest = new CreateQueueRequest(QueueNamePrefix + UUID.randomUUID());
            return this.sqs.createQueue(createQueueRequest).getQueueUrl();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void sendReviewToManager(String queueUrl, Map<String, MessageAttributeValue> attributes , String entities, int sentiment){
        Map<String, MessageAttributeValue> newAttributes = attributes;
        newAttributes.put("sentiment", new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(String.valueOf(sentiment)));


        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(entities)
                .withMessageAttributes(newAttributes);
        sqs.sendMessage(sendMessageRequest);

    }

    void sendReviewToWorkers(String recordTitle, String reviewId, String reviewText, String queueUrl, String clientName){
        System.out.println("Sending a message to a worker\n");

        final Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("ClientName", new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(clientName));
        attributes.put("RecordTitle", new MessageAttributeValue()
                .withDataType("String")
                .withStringListValues(recordTitle));
        attributes.put("ReviewId", new MessageAttributeValue()
                .withDataType("String")
                .withStringListValues(reviewId));


        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(reviewText)
                .withMessageAttributes(attributes);
        sqs.sendMessage(sendMessageRequest);
    }

    void sendMessage(String message, String queueUrl) {

        // Send a message
        System.out.println("Sending a message to " + queueUrl + ".\n");
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(message);
        sqs.sendMessage(sendMessageRequest);
    }

    private List<Message> receiveMessages(String urlQueue) {
        return  sqs.receiveMessage(urlQueue).getMessages();
    }


    public void deleteMessage(String urlQueue) {
        // Delete a message
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(urlQueue);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        System.out.println("Deleting a message.\n");
        String messageRecieptHandle = messages.get(0).getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(urlQueue, messageRecieptHandle));
    }

    public void deleteQueue(String urlQueue) {
        // Delete a queue
        System.out.println("Deleting " + urlQueue + " queue.\n");
        sqs.deleteQueue(new DeleteQueueRequest(urlQueue));
    }

    String waitForTerminateMessage(String urlQueue) {
        while (true) {
            List<Message> messages = receiveMessages(urlQueue);
            for (Message message : messages) {
                if (message.getBody().equals("terminate")) {
                    if (!message.getAttributes().containsKey("summary") || !message.getAttributes().get("summary").equals("done")) {
                        System.out.println("terminate message was received but without summary that is not done, now exiting");
                        deleteMessageFromSqs(urlQueue, message);
//                        System.exit(1);
                        return "";
                    } else
                        return message.getAttributes().get("dir");
                }
            }
        }
    }


    private static void printMessage(Message message) {
        System.out.println("  Message");
        System.out.println("    MessageId:     " + message.getMessageId());
        System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
        System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
        System.out.println("    Body:          " + message.getBody());
        for (Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
            System.out.println("  Attribute");
            System.out.println("    Name:  " + entry.getKey());
            System.out.println("    Value: " + entry.getValue());
        }
    }

    public List<String> getQueuesList() {
        return sqs.listQueues().getQueueUrls();
    }

    public List<Message> receiveMessage(String urlQueue) {
        System.out.println("Receiving messages from " + urlQueue + ".\n");
        final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(urlQueue);
        final List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        System.out.println();
        return messages;
    }

    public void waitForAliveMessageFromManger(String personalQueueUrl) {
        while (true) {
            List<Message> messages = receiveMessages(personalQueueUrl);
            for (Message message : messages)
                if (message.getBody().equals("alive")){
                    deleteMessageFromSqs(personalQueueUrl, message);
                    return;
                }
        }
    }

    public void deleteMessageFromSqs(String queueUrl, Message message){
        System.out.println("Deleting the message: " + message.getBody() + " from queue: " + queueUrl + ".\n");
        final String messageReceiptHandle = message.getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
    }
}
