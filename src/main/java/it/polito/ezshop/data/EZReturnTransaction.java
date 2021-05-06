package it.polito.ezshop.data;

import java.time.LocalDate;
import java.util.HashMap;

public class EZReturnTransaction extends EZBalanceOperation{
    private int quantity;
    private double returnedValue;
    private boolean isClosed;
    private ProductType returnedProduct;

    public EZReturnTransaction(int balanceId, LocalDate date, double money, String type)
    {   // 'type' shouldn't be of type "BalanceOpTypeEnum" ???
        super(balanceId, date, money, type);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getReturnedValue() {
        return returnedValue;
    }

    public void setReturnedValue(double returnedValue) {
        this.returnedValue = returnedValue;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public ProductType getReturnedProduct() {
        return returnedProduct;
    }

    public void setReturnedProduct(ProductType returnedProduct) {
        this.returnedProduct = returnedProduct;
    }
}
