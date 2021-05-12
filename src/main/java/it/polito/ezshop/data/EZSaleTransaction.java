package it.polito.ezshop.data;

import java.util.LinkedList;
import java.util.List;

public class EZSaleTransaction implements SaleTransaction {
    public static final String STOpened = "OPENED";
    public static final String STClosed = "CLOSED";
    public static final String STPayed = "PAYED";

    private Integer ticketNumber;
    private List<TicketEntry> entries;
    private double discountRate;
    private double price;
    private List<EZReturnTransaction> returns;
    private String status; //accepted values: "OPENED", "CLOSED", "PAYED"
    private EZCard attachedCard;

    public EZSaleTransaction (Integer ticketNumber){
        this.ticketNumber = ticketNumber;
        this.status = EZSaleTransaction.STOpened;
        this.attachedCard = null;
        this.entries = new LinkedList<>();
        this.discountRate = 0;
        this.price = 0;
        this.returns = new LinkedList<>();
    }

    public EZSaleTransaction (Integer ticketNumber, List<TicketEntry> entries, double discountRate, double price, String status) {
        this.ticketNumber = ticketNumber;
        this.entries = entries != null ? entries : new LinkedList<>();
        this.discountRate = discountRate;
        this.price = price;
        this.status = status;
        this.attachedCard = null;
        this.returns = new LinkedList<>();
    }

    @Override
    public Integer getTicketNumber() {
        return this.ticketNumber;
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    @Override
    public List<TicketEntry> getEntries() {
        return this.entries;
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        this.entries = entries != null ? entries : new LinkedList<>();
    }

    @Override
    public double getDiscountRate() {
        return this.discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus(){
        return this.status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public boolean hasRequiredStatus(String ...requiredStatus) {
        if (requiredStatus == null)
            return false;

        for (String role : requiredStatus ) {
            if (role.equals(this.status))
                return true;
        }

        return false;
    }

    public EZCard getAttachedCard() {
        return this.attachedCard;
    }

    public void setAttachedCard(EZCard attachedCard) {
        this.attachedCard = attachedCard;
    }

    public List<EZReturnTransaction> getReturns() { return returns; }

    public void setReturns(List<EZReturnTransaction> returns) { this.returns = returns; }

    public EZTicketEntry getTicketEntryByBarCode(String barCode)
    {
        for (TicketEntry entry : entries) {
            if (entry.getBarCode().equals(barCode))
                return (EZTicketEntry) entry;
        }
        return null;
    }

    public void updatePrice(double toBeAdded)
    {
        this.price += toBeAdded;
    }
}
