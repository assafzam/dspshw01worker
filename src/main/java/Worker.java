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

        String msg = "As soon as I got this package, I gave it to my 18 month old son to open and he made me read it to him about a dozen times. He loves it! He's obsessed with his belly button, so this is the perfect book for him. He's also obsessed with peek-a-boo, so he loves lifting the flaps. It was a definite hit!";
        ArrayList<String> entities = StanNLPHandler.findEntities(msg);
        int sentiment = StanNLPHandler.findSentiment(msg);
        System.out.println(entities.toString());
        System.out.println(sentiment);


//        while (true) {
//            final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(workersQueueOut).withVisibilityTimeout(180).withMaxNumberOfMessages(10);
//            final List<Message> messages = sqs.getSqs().receiveMessage(receiveMessageRequest).getMessages();
//            for (Message message : messages) {
//                System.out.println("message: " + message.getBody());
//                if (message.getBody().equals("terminate")) {
//                    System.out.println("worker received from manger terminate message. exiting...");
//                    System.exit(0);
//                }
//                // stanford nlp:
//                String reviewText = message.getBody();
//                ArrayList<String> entities = StanNLPHandler.findEntities(reviewText);
//                int sentiment = StanNLPHandler.findSentiment(reviewText);
//
//                // send the message with the new data(entities, sentiment)
//                Map<String, MessageAttributeValue> attributes = message.getMessageAttributes();
//                sqs.sendReviewToManager(workersQueueIn, attributes, entities.toString(), sentiment);
//
//                //delete the message from queue:
//                sqs.deleteMessageFromSqs(workersQueueOut,message);
//
//            }
//
//
//        }


    }
}

