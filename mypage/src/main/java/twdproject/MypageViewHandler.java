package twdproject;

import twdproject.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {
    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_Create(@Payload Ordered Ordered) {
        System.out.println("\n\n##### MYPAGE whenOrdered_then_Create : " + Ordered.toJson() + "\n\n");
        if(!Ordered.validate()) return;
        Mypage Mypage = new Mypage();
        Mypage.setOrderId(Ordered.getOrderId());
        Mypage.setEqpStatus(Ordered.getEqpStatus());
        System.out.println("\n\n###################################11111 : " + mypageRepository + "\n\n");
        mypageRepository.save(Mypage);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCancelled_then_Update(@Payload OrderCancelled OrderCancelled) {
        System.out.println("\n\n##### MYPAGE whenOrderCancelled_then_Update : " + OrderCancelled.toJson() + "\n\n");
        if(!OrderCancelled.validate()) return;
        java.util.Optional<Mypage> cOptional = mypageRepository.findById(OrderCancelled.getOrderId());
        Mypage view = cOptional.get();
        view.setEqpStatus(OrderCancelled.getEqpStatus());

        System.out.println("\n\n###################################22222 : " + mypageRepository + "\n\n");
        mypageRepository.save(view);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryStarted_then_Update(@Payload DeliveryStarted DeliveryStarted) {
        System.out.println("\n\n##### MYPAGE whenDeliveryStarted_then_Update : " + DeliveryStarted.toJson() + "\n\n");
        if(!DeliveryStarted.validate()) return;
        java.util.Optional<Mypage> cOptional = mypageRepository.findById(DeliveryStarted.getOrderId());
        Mypage view = cOptional.get();
        view.setEqpStatus(DeliveryStarted.getEqpStatus());
        System.out.println("\n\n###################################33333 : " + mypageRepository + "\n\n");
        mypageRepository.save(view);
    }
}