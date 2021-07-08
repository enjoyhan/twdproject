package twdproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

 @RestController
 public class DeliveryController {
    @Autowired
    DeliveryRepository deliveryRepository;

    @RequestMapping(value = "/startDelivery", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")


    public boolean startDelivery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("##startDelivery called##");

        Long orderId = Long.valueOf(request.getParameter("orderId"));
        Long shopId = Long.valueOf(request.getParameter("shopId"));
        String eqpNm = String.valueOf(request.getParameter("eqpNm"));

        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setShopId(shopId);
        delivery.setEqpNm(eqpNm);
        delivery.setEqpStatus("DeliveryStart");
        deliveryRepository.save(delivery);

        return true;
    }

 }
