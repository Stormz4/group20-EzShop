package it.polito.ezshop.data;

public class EZUser implements User {
    public static final String URNoRole = "";
    public static final String URAdministrator = "Administrator";
    public static final String URShopManager = "ShopManager";
    public static final String URCashier = "Cashier";

    private Integer id;
    private String userName;
    private String password;
    private String role;


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
        if (role == null)
            this.role = URNoRole;

        this.role = role;
    }

    public boolean hasRequiredRole(String ...requiredRoles) {
        if (requiredRoles == null)
            return false;

        for (String role : requiredRoles ) {
            if (role.equals(this.role))
                return true;
        }

        return false;
    }
}
