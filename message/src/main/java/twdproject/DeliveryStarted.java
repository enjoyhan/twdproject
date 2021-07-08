
package twdproject;

public class DeliveryStarted extends AbstractEvent {

    private Long id;
    private Long deliveryId;
    private Long orderId;
    private Long shopId;
    private String eqpStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
}

