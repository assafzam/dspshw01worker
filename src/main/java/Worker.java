import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Worker {



    public static void main(String[] args) {

        String workersQueueIn = args[0];
        String workersQueueOut = args[1];
        SQSHandler sqs = new SQSHandler();


        while (true) {
            final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(workersQueueOut).withVisibilityTimeout(300).withMaxNumberOfMessages(10);
            final List<Message> messages = sqs.getSqs().receiveMessage(receiveMessageRequest.withMessageAttributeNames("ClientName", "RecordTitle", "ReviewId")).getMessages();
            for (Message message : messages) {
                System.out.println("message: " + message.getBody());
                if (message.getBody().equals("terminate")) {
                    sqs.sendMessage("terminate",workersQueueOut);
                    System.out.println("worker received from manger terminate message. exiting...");
                    // send the manager that you finished:
                    sqs.sendMessage("worker terminated", workersQueueIn);
                    System.exit(0);
                }
                // stanford nlp:
                String reviewText = message.getBody();
                ArrayList<String> entities = StanNLPHandler.findEntities(reviewText);
                int sentiment = StanNLPHandler.findSentiment(reviewText);
                System.out.println("stanford processed the message:");
                System.out.println("entities: " + entities.toString());
                System.out.println("sentiment: " + sentiment);

                // send the message with the new data(entities, sentiment)
                Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
                sqs.sendReviewToManager(workersQueueIn, attributes, entities.toString(), sentiment);

                //delete the message from queue:
                sqs.deleteMessageFromSqs(workersQueueOut,message);

            }

        }


    }
}

