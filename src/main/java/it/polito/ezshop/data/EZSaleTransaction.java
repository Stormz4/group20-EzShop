package it.polito.ezshop.data;

import java.util.List;

public class EZSaleTransaction implements SaleTransaction {
    private Integer ticketNumber;
    private List<TicketEntry> entries;
    private double discountRate;
    private double price;

    public EZSaleTransaction (Integer ticketNumber, List<TicketEntry> entries, double discountRate, double price) {
        this.ticketNumber = ticketNumber;
        this.entries = entries;
        this.discountRate = discountRate;
        this.price = price;
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
        return this.entries; // should return a copy?
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        this.entries = entries; // should do a copy?
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
}
