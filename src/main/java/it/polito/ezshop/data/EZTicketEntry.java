package it.polito.ezshop.data;

public class EZTicketEntry implements TicketEntry {
    String barCode;
    //Integer productId;
    String productDescription;
    int amount;
    double pricePerUnit;
    double discountRate;

    public EZTicketEntry (String barCode, String productDescription, int amount, double pricePerUnit, double discountRate) {
        this.barCode = barCode;
        this.productDescription = productDescription;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.discountRate = discountRate;
    }

    @Override
    public String getBarCode() {
        return this.barCode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

//    public Integer getProductId() {
//        return productId;
//    }
//
//    public void setProductId(Integer productId) {
//        this.productId = productId;
//    }

    @Override
    public String getProductDescription() {
        return this.productDescription;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public double getPricePerUnit() {
        return this.pricePerUnit;
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public double getDiscountRate() {
        return discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getTotal() {
        return (pricePerUnit * amount * (1-discountRate));
    }

    public void updateAmount(int toBeAdded) {
        this.amount += toBeAdded;
    }
}
