package it.polito.ezshop.data;

import java.util.List;

public class EZReturnTransaction{
    private Integer returnId;
    private double returnedValue; //   > 0
    private Integer itsSaleTransactionId;
    private List<TicketEntry> entries;
    private boolean isPayed;
    private boolean isClosed;

    public EZReturnTransaction(Integer returnId, Integer itsSaleTransactionId) {
        this.returnId = returnId;
        this.itsSaleTransactionId = itsSaleTransactionId;
        this.returnedValue = 0;
        this.isPayed = false;
        this.isClosed = false;
    }

    public EZReturnTransaction(EZReturnTransaction tmpRetTr) { // necessary???
        this.returnId = tmpRetTr.returnId;
        this.itsSaleTransactionId = tmpRetTr.itsSaleTransactionId;
        this.returnedValue = tmpRetTr.returnedValue;
        this.entries = tmpRetTr.entries;
        this.isPayed = tmpRetTr.isPayed;
        this.isClosed = tmpRetTr.isClosed;
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

    public Integer getItsSaleTransactionId() { return itsSaleTransactionId; }

    public void setItsSaleTransactionId(Integer itsSaleTransactionId) { this.itsSaleTransactionId = itsSaleTransactionId; }

    public boolean isPayed() { return isPayed; }

    public void setPayed(boolean payed) { isPayed = payed; }

    public boolean isClosed() { return isClosed; }

    public void setClosed(boolean closed) { isClosed = closed; }

    public List<TicketEntry> getEntries() { return entries; }

    public void setEntries(List<TicketEntry> entries) { this.entries = entries; }
}
