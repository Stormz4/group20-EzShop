package it.polito.ezshop.data;

public class EZProduct {
    private String RFID;
    private Integer prodTypeID;
    private Integer saleID;

    public EZProduct (String RFID, Integer prodTypeID, EZSaleTransaction saleID) {
        this.RFID = RFID;
        this.prodTypeID = prodTypeID;
        this.saleID = -1; // ???
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
}
