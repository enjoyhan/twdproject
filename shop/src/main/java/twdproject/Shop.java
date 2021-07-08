package twdproject;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Shop_table")
public class Shop {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long shopId;
    private Long eqpSernum;
    private String eqpNm;
    private String eqpStatus;

    @PostPersist
    public void onPostPersist(){
        EqpRegistered eqpRegistered = new EqpRegistered();
        BeanUtils.copyProperties(this, eqpRegistered);
        eqpRegistered.publishAfterCommit();
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
