package it.polito.ezshop.data;

public class EZCard {
    private String cardCode;
    private Integer customerId;
    private Integer points;

    public EZCard(String cardCode, Integer customerId, Integer points) {
        this.cardCode = cardCode;
        this.customerId = customerId;
        this.points = points;
    }

    public String getCardCode() {
        return this.cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getPoints() {
        return this.points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
