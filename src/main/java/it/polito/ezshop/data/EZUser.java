package it.polito.ezshop.data;

public class EZUser implements User {
    public static final int URNoRole = 0;
    public static final int URAdministrator = 1;
    public static final int URShopManager = 2;
    public static final int URCashier = 4; // skipping 3 because the compare we do looks for bits

    private Integer id;
    private String userName;
    private String password;
    private int role;


    public EZUser (Integer id, String userName, String password, String role) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.setRole(role);
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
    public String getUsername() {
        return this.userName;
    }

    @Override
    public void setUsername(String username) {
        this.userName = username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRole() {

        switch (this.role) {
            case URAdministrator:
                return "Administrator";

            case URShopManager:
                return "ShopManager";

            case URCashier:
                return "Cashier";

            default:
                return "";
        }
    }

    @Override
    public void setRole(String role) {
        if (role == null) {
            this.role = URNoRole;
            return;
        }

        switch (role) {
            case "Administrator":
                this.role = URAdministrator;
                break;
            case "ShopManager":
                this.role = URShopManager;
                break;
            case "Cashier":
                this.role = URCashier;
                break;

            default:
                this.role = URNoRole;
        }
    }

    public boolean hasRequiredRole(int requiredRole) {

        return (requiredRole & this.role) != 0;
    }
}
