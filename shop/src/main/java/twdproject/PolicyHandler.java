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
    @Autowired ShopRepository shopRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCancelled_CancelEqp(@Payload OrderCancelled orderCancelled){
        
        if(!orderCancelled.validate()) return;
        System.out.println("\n\n##### listener orderCancelled : " + orderCancelled.toJson() + "\n\n");

        java.util.Optional<Shop> ShopOptional = shopRepository.findById(orderCancelled.getShopId());
        System.out.println("\n\n#################### ShopOptional.get() : " +ShopOptional.get() + "\n\n");

        Shop shop = ShopOptional.get();
        shop.setEqpStatus("OrderAvailable");

        shopRepository.save(shop);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
