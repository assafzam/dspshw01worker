
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;


import java.util.Map;

public class SQSHandler {

    private AmazonSQS sqs;

    SQSHandler() {

        this.sqs = AmazonSQSClientBuilder.standard()
                .withRegion("us-east-1")
                .build();
    }


    public AmazonSQS getSqs() {
        return sqs;
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



    void sendMessage(String message, String queueUrl) {

        // Send a message
        System.out.println("Sending a message to " + queueUrl + ".\n");
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(message);
        sqs.sendMessage(sendMessageRequest);
    }





    public void deleteMessageFromSqs(String queueUrl, Message message){
        System.out.println("Deleting the message: " + message.getBody() + " from queue: " + queueUrl + ".\n");
        final String messageReceiptHandle = message.getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
    }
}
