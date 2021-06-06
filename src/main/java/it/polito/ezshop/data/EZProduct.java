package it.polito.ezshop.data;

public class EZProduct {
    private String RFID;
    private Integer prodTypeID;

    public EZProduct (String RFID, Integer prodTypeID) {
        this.RFID = RFID;
        this.prodTypeID = prodTypeID;
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
}
