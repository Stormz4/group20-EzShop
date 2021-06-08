package it.polito.ezshop.data;

import java.util.LinkedList;
import java.util.List;

public class EZReturnTransaction{
    public static final String RTOpened = "OPENED";
    public static final String RTClosed = "CLOSED";
    public static final String RTPayed = "PAYED";

    private Integer returnId;
    private double returnedValue; //   > 0
    private Integer saleTransactionId;
    private List<TicketEntry> entries;
    private String status;

    public EZReturnTransaction(Integer returnId, Integer saleTransactionId, List<TicketEntry> entries, double returnedValue, String status) {
        this.returnId = returnId;
        this.saleTransactionId = saleTransactionId;
        this.returnedValue = returnedValue;
        this.status = status;
        this.entries = entries != null ? entries : new LinkedList<>();
    }

    public EZReturnTransaction(EZReturnTransaction tmpRetTr) {
        this.returnId = tmpRetTr.returnId;
        this.saleTransactionId = tmpRetTr.saleTransactionId;
        this.returnedValue = tmpRetTr.returnedValue;
        this.entries = tmpRetTr.entries != null ? tmpRetTr.entries : new LinkedList<>();
        this.status = tmpRetTr.status;
    }

    public Integer getReturnId() { return returnId; }

    public void setReturnId(Integer returnId) { this.returnId = returnId; }

    public double getReturnedValue() {
        return returnedValue;
    }

    public void setReturnedValue(double returnedValue) {
        this.returnedValue = returnedValue;
    }

    public void updateReturnedValue(double toBeAdded) {
        this.returnedValue += toBeAdded;
    }

    public Integer getSaleTransactionId() {
        return saleTransactionId;
    }

    public void setItsSaleTransactionId(Integer itsSaleTransactionId) {
        this.saleTransactionId = itsSaleTransactionId;
    }

    public List<TicketEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TicketEntry> entries) {
        this.entries = entries != null ? entries : new LinkedList<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
