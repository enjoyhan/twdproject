
package twdproject;

public class Ordered extends AbstractEvent {

    private Long id;
    private Long orderId;
    private String eqpStatus;
    private String eqpNm;
    private Long shopId;
    private Long eqpSernum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    public Long getEqpSernum() {
        return eqpSernum;
    }

    public void setEqpSernum(Long eqpSernum) {
        this.eqpSernum = eqpSernum;
    }
}

