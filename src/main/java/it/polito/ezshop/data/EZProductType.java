package it.polito.ezshop.data;

public class EZProductType implements ProductType {
    private Integer id;
    private Integer quantity;
    private String location;
    private String note;
    private String productDescription;
    private String barCode;
    private double pricePerUnit;

    public EZProductType (Integer id, Integer quantity, String location, String note, String productDescription, String barCode, double pricePerUnit) {
        this.id = id;
        this.quantity = quantity;
        this.location = location;
        this.note = note;
        this.productDescription = productDescription;
        this.barCode = barCode;
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public Integer getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getNote() {
        return this.note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getProductDescription() {
        return this.productDescription;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @Override
    public String getBarCode() {
        return this.barCode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Override
    public Double getPricePerUnit() {
        return this.pricePerUnit;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public void editQuantity(int toBeAdded) { this.quantity += toBeAdded; }
}
