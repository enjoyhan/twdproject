package twdproject;

public class EqpRegistered extends AbstractEvent {

    private Long id;
    private Long shopId;
    private String eqpNm;
    private String eqpStatus;
    private Long eqpSernum;

    public EqpRegistered(){
        super();
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
    public String getEqpNm() {
        return eqpNm;
    }

    public void setEqpNm(String eqpNm) {
        this.eqpNm = eqpNm;
    }
    public String getEqpStatus() {
        return eqpStatus;
    }

    public void setEqpStatus(String eqpStatus) {
        this.eqpStatus = eqpStatus;
    }
    public Long getEqpSernum() {
        return eqpSernum;
    }

    public void setEqpSernum(Long eqpSernum) {
        this.eqpSernum = eqpSernum;
    }
}
