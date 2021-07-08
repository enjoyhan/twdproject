package twdproject;

import twdproject.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired MessageRepository messageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryStarted_SendMessage(@Payload DeliveryStarted deliveryStarted){

        if(!deliveryStarted.validate()) return;

        System.out.println("\n\n##### listener SendMessage : " + deliveryStarted.toJson() + "\n\n");

        // Sample Logic //
        Message message = new Message();
        messageRepository.save(message);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_SendMessage(@Payload OrderCancelled orderCancelled){

        if(!orderCancelled.validate()) return;

        System.out.println("\n\n##### listener SendMessage : " + orderCancelled.toJson() + "\n\n");

        // Sample Logic //
        Message message = new Message();
        messageRepository.save(message);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_SendMessage(@Payload Ordered ordered){

        if(!ordered.validate()) return;

        System.out.println("\n\n##### listener SendMessage : " + ordered.toJson() + "\n\n");

        // Sample Logic //
        Message message = new Message();
        messageRepository.save(message);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
