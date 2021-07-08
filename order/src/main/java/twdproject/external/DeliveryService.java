
package twdproject.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

//@FeignClient(name="delivery", url="http://localhost:8083")
@FeignClient(name="order", url="${feign.client.url.orderUrl}")
public interface DeliveryService {
    
    @RequestMapping(method= RequestMethod.GET, path="/startDelivery")
    public boolean startDelivery(@RequestParam("orderId") Long orderId,
                              @RequestParam("shopId") Long shopId,
                              @RequestParam("eqpNm") String eqpNm,
                              @RequestParam("eqpStatus") String eqpStatus,
                              @RequestParam("eqpSernum") Long eqpSernum);

}
