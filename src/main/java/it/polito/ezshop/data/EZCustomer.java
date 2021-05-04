package it.polito.ezshop.data;

public class EZCustomer implements Customer {
    private Integer id;
    private String customerName;
    private String customerCard;
    private Integer points;

    public EZCustomer(Integer id, String customerName, String customerCard, Integer points){
        this.id = id;
        this.customerName = customerName;
        this.customerCard = customerCard;
        this.points = points;
    }

    @Override
    public String getCustomerName() {
        return this.customerName;
    }

    @Override
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String getCustomerCard() {
        return this.customerCard;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        this.customerCard = customerCard;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getPoints() {
        return this.points;
    }

    @Override
    public void setPoints(Integer points) {
        this.points =points;
    }

    public void addPoints(Integer points) {this.points = this.points+points;}
}
