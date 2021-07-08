package twdproject;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long shopId;
    private String eqpStatus;
    private String eqpNm;
    private Long orderId;
    private Long eqpSernum;

    @PostPersist
    public void onPostPersist(){
        System.out.println("####11111111111111111111111111111111111111: ");
        Ordered ordered = new Ordered();

        System.out.println("####this 객체 : " + this.getEqpNm() + "\n\n");
        ordered.setEqpStatus("Order");
        ordered.publishAfterCommit();

        boolean rslt = OrderApplication.applicationContext.getBean(twdproject.external.DeliveryService.class).startDelivery(this.getOrderId(),this.getShopId() ,this.getEqpNm(),this.getEqpStatus(),this.getEqpSernum());
        System.out.println("####rslt rslt rslt: " + rslt + "\n\n");

        if(rslt) {
            ordered.setOrderId(this.getOrderId());
            ordered.setShopId(this.getShopId());
            ordered.setEqpNm(this.getEqpNm());
            ordered.setEqpSernum(this.getEqpSernum());
            ordered.setEqpStatus(this.getEqpStatus());

            System.out.println("####ordered 객체 : " + ordered.toJson() + "\n\n");
            BeanUtils.copyProperties(this, ordered);
            ordered.publishAfterCommit();
        }

    }

    @PostRemove
    public void onPostRemove() {

        OrderCancelled orderCancelled = new OrderCancelled();
        
        // this 부분은 http POST 할 때 파라미터를 넣어서 호출할 때 this 객체에 담기고
        // 디폴트로 값을 세팅하려면 this. 으로 항목에 값을 세팅해야함   
        
        // 현재 OrderId와 paymentId가 동일하게 증가하므로 걍 값 세팅
        this.setOrderId(this.getOrderId());
        this.setEqpStatus("OrderCancel");

        orderCancelled.setOrderId(this.getOrderId());
        orderCancelled.setEqpNm(this.getEqpNm());
        orderCancelled.setEqpSernum(this.getEqpSernum());
        orderCancelled.setEqpStatus(this.getEqpStatus());

        BeanUtils.copyProperties(this, orderCancelled);
        orderCancelled.publishAfterCommit();

    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    public String getEqpStatus() {
        return eqpStatus;
    }

    public void setEqpStatus(String eqpStatus) {
        this.eqpStatus = eqpStatus;
    }
    public String getEqpNm() {
        return eqpNm;
    }

    public void setEqpNm(String eqpNm) {
        this.eqpNm = eqpNm;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getEqpSernum() {
        return eqpSernum;
    }

    public void setEqpSernum(Long eqpSernum) {
        this.eqpSernum = eqpSernum;
    }

}