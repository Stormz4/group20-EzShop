package it.polito.ezshop.data;

public class EZProduct {
    private String RFID;
    private Integer prodTypeID;
    private Integer saleID;
    private Integer returnID;

    public EZProduct (String RFID, Integer prodTypeID, Integer saleID, Integer returnID) {
        this.RFID = RFID;
        this.prodTypeID = prodTypeID;
        this.saleID = -1;
        this.returnID = -1;
    }

    public String getRFID() {
        return RFID;
    }

    public Integer getProdTypeID() {
        return prodTypeID;
    }

    public void setRFID(String RFID) {
        this.RFID = RFID;
    }

    public void setProdTypeID(Integer prodTypeID) {
        this.prodTypeID = prodTypeID;
    }

    public Integer getSaleID() { return saleID; }

    public void setSaleID(Integer saleID) { this.saleID = saleID; }

    public Integer getReturnID() { return returnID; }

    public void setReturnID(Integer returnID) { this.returnID = returnID; }
}
