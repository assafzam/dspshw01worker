import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Worker {


    private S3Handler s3 = new S3Handler();

    private SQSHandler sqs = new SQSHandler();

    public static void main(String[] args) {

        String workersQueueIn = args[0];
        String workersQueueOut = args[1];
        SQSHandler sqs = new SQSHandler();


        while (true) {
            final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(workersQueueOut).withVisibilityTimeout(180).withMaxNumberOfMessages(10);
            final List<Message> messages = sqs.getSqs().receiveMessage(receiveMessageRequest).getMessages();
            for (Message message : messages) {
                System.out.println("message: " + message.getBody());
                if (message.getBody().equals("terminate")) {
                    System.out.println("worker received from manger terminate message. exiting...");
                    System.exit(0);
                }
                // stanford nlp:
                String reviewText = message.getBody();
                ArrayList<String> entities = StanforNLP.findEntities(reviewText);
                int sentiment = StanforNLP.findSentiment(reviewText);

                // send the message with the new data(entities, sentiment)
                Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
                sqs.sendReviewToManager(workersQueueIn, attributes, entities.toString(), sentiment);

                //delete the message from queue:
                sqs.deleteMessageFromSqs(workersQueueOut,message);

            }


        }


    }
}

