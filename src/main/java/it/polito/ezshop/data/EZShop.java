package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;

import it.polito.ezshop.utils.*;

import java.io.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.lang.Math;
import java.util.stream.Collectors;

import static it.polito.ezshop.data.EZBalanceOperation.*;
import static it.polito.ezshop.data.EZReturnTransaction.*;
import static it.polito.ezshop.data.EZOrder.*;
import static it.polito.ezshop.data.EZUser.*;
import static it.polito.ezshop.data.SQLiteDB.defaultID;
import static it.polito.ezshop.data.SQLiteDB.defaultValue;


public class EZShop implements EZShopInterface {
    final boolean USE_TEST_DB = false;

    private final SQLiteDB shopDB = new SQLiteDB();
    private EZUser currUser = null;

    private HashMap<Integer, EZCustomer> ezCustomers;
    private HashMap<Integer, EZUser> ezUsers;
    private HashMap<Integer, EZProductType> ezProducts;
    private HashMap<Integer, EZOrder> ezOrders;
    private EZReturnTransaction tmpRetTr;
    private int nextBalanceId;

    // TODO verify is this map is needed
    private List<String> ezCards;

    private EZAccountBook accountingBook;
    private HashMap<Integer, EZBalanceOperation> ezBalanceOperations;
    private HashMap<Integer, EZSaleTransaction> ezSaleTransactions;
    private HashMap<Integer, EZReturnTransaction> ezReturnTransactions;

    public EZShop() {
        this.loadDataFromDB();

        // TODO: remove before delivery
        this.testDB();
    }

    public void  loadDataFromDB() {
        if (this.shopDB.dbConnection == null)
            shopDB.connect();
        shopDB.initDatabase();

        if (ezBalanceOperations == null)
            ezBalanceOperations = shopDB.selectAllBalanceOperations();

        if (ezCards == null)
            ezCards = shopDB.selectAllCards();

        if (ezCustomers == null)
            ezCustomers = shopDB.selectAllCustomers();

        if (ezOrders == null)
            ezOrders = shopDB.selectAllOrders();

        if (ezProducts == null)
            ezProducts = shopDB.selectAllProductTypes();

        if (ezSaleTransactions == null)
            ezSaleTransactions = shopDB.selectAllSaleTransactions();

        if (ezUsers == null)
            ezUsers = shopDB.selectAllUsers();
    }

    private void clearData() {
        ezBalanceOperations = null;
        ezOrders = null;
        ezProducts = null;
        ezSaleTransactions = null;
    }

    @Override
    public void reset() {
        if (this.shopDB.dbConnection == null)
            this.shopDB.connect();

        if (this.shopDB.clearDatabase())
            this.clearData();
    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        if (username == null || username.isEmpty() ) {
            throw new InvalidUsernameException();
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException();
        }

        if (role.isEmpty() || !(role.equals("Administrator") || role.equals("Cashier") || role.equals("ShopManager"))) {
            throw new InvalidRoleException();
        }

        if (this.ezUsers == null)
            return defaultID;

        for (EZUser user : ezUsers.values()) {
            if (user.getUsername().equals(username))
                return defaultID;
        }
        Integer id = shopDB.insertUser(username, password, role);
        // Get the highest ID from the DB
        if (id == defaultID) // -1
            return id;

        EZUser user = new EZUser(id, username, password, role);

        ezUsers.put(id, user);

        return id;
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if (id == null || id <=0) {
            throw new InvalidUserIdException();
        }

        if (!ezUsers.containsKey(id)) {
            return false;
        }

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator)){
            throw new UnauthorizedException();
        }

        boolean success = shopDB.deleteUser(id);
        if (!success)
            return false;

        ezUsers.remove(id);

        return true;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        if(this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator)){
            throw new UnauthorizedException();
        }

        List<User> usersList = new LinkedList<>(ezUsers.values());
        return usersList;
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if (!ezUsers.containsKey(id)) {
            return null;
        }
        if (id == null || id <=0) {
            throw new InvalidUserIdException();
        }

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator)){
            throw new UnauthorizedException();
        }

        User user = ezUsers.get(id);
        return user;
    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
        /**
         * This method updates the role of a user with given id. It can be invoked only after a user with role "Administrator" is
         * logged in.
         *
         * @param id the id of the user
         * @param role the new role the user should be assigned to
         *
         * @return true if the update was successful, false if the user does not exist
         *
         * @throws InvalidUserIdException   if the user Id is less than or equal to 0 or if it is null
         * @throws InvalidRoleException     if the new role is empty, null or not among one of the following : {"Administrator", "Cashier", "ShopManager"}
         * @throws UnauthorizedException    if there is no logged user or if it has not the rights to perform the operation
         */
        if (!ezUsers.containsKey(id)) {
            return false;
        }
        if (id==null || id <=0) {
            throw new InvalidUserIdException();
        }
        if (role.isEmpty() || !(role.equals("Administrator") || role.equals("Cashier") || role.equals("ShopManager"))) {
            throw new InvalidRoleException();
        }
        if((this.currUser == null) || (!this.currUser.hasRequiredRole(URAdministrator))){
            throw new UnauthorizedException();
        }
        EZUser user = ezUsers.get(id);

        boolean success = shopDB.updateUser(id, user.getUsername(), user.getPassword(), role);
        if (!success)
            return false;

        user.setRole(role);
        ezUsers.replace(id, user);
        return true;
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        /**
         * This method lets a user with given username and password login into the system
         *
         * @param username the username of the user
         * @param password the password of the user
         *
         * @return an object of class User filled with the logged user's data if login is successful, null otherwise ( wrong credentials or db problems)
         *
         * @throws InvalidUsernameException if the username is empty or null
         * @throws InvalidPasswordException if the password is empty or null
         */
        if (username == null || username.isEmpty()){
            throw new InvalidUsernameException();
        }
        if (password == null || password.isEmpty()){
            throw new InvalidPasswordException();
        }

        //
        // Iterate the map and search the user
        for (EZUser user : ezUsers.values()) {
            if (user.getPassword().equals(password) && user.getUsername().equals(username)){
                this.currUser = user;
            }
        }

        // TODO return null if DB problems?
        return currUser;
    }

    @Override
    public boolean logout() {
        if (currUser == null){
            return false;
        }
        currUser = null;
        return true;
    }

    public boolean isValidBarCode(String barCode){

        if (barCode.matches("[0-9]{12,14}")){
            int sum=0;
            int number=0;
            for(int i=0; i<barCode.length()-1; i++){
                number=Integer.parseInt(Character.toString(barCode.charAt(i)));
                System.out.println(Integer.parseInt(Character.toString(barCode.charAt(i))));
                if (!(i%2==0)){
                    number=number*3;
                }
                // else number = number*1;
                sum = sum+number;
            }
            // Now find the nearest multiple of 10 and subtract sum from it

            // Return of closest of two
            int result = (10-sum%10)+sum;
            if ((result-sum)==Integer.parseInt(Character.toString(barCode.charAt(barCode.length()-1)))){
                return true;
            }
        }

        return false;
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        if (description == null || description.isEmpty() ) {
            throw new InvalidProductDescriptionException();
        }
        if (productCode == null || productCode.isEmpty() || !isValidBarCode(productCode)) {
            throw new InvalidProductCodeException();
        }
        if(pricePerUnit <=0){
            throw new InvalidPricePerUnitException();
        }
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        // Check if the Barcode is unique
        for (ProductType product : ezProducts.values()) {
            if (product.getBarCode().equals(productCode)) {
                return -1;
            }
        }
        if (note == null){
            note="";
        }
        Integer id = shopDB.insertProductType(0, "", note, description, productCode, pricePerUnit);
        // Get the highest ID from the DB

        // Return -1 if there is an error with the DB
        if (id == defaultID)
            return id;

        EZProductType prodType;
        prodType = new EZProductType(id, 0, "", note, description, productCode, pricePerUnit);

        ezProducts.put(id, prodType);

        return id;
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {

        if (id == null || id<=0){
            throw new InvalidProductIdException();
        }

        if (!ezProducts.containsKey(id)) {
            return false;
        }

        if (newDescription == null || newDescription.isEmpty() ) {
            throw new InvalidProductDescriptionException();
        }
        if (newCode == null || newCode.isEmpty() || !isValidBarCode(newCode)) {
            throw new InvalidProductCodeException();
        }
        if(newPrice <=0){
            throw new InvalidPricePerUnitException();
        }
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        // Check if the Barcode is unique
        for (EZProductType product : ezProducts.values()) {
            if (product.getBarCode().equals(newCode))
                return false;
        }

        EZProductType prodType = ezProducts.get(id);
        boolean success = shopDB.updateProductType(id, prodType.getQuantity(), prodType.getLocation(), newNote, newDescription, newCode,newPrice);

        if (!success)
            return false;

        prodType.setProductDescription(newDescription);
        prodType.setBarCode(newCode);
        prodType.setPricePerUnit(newPrice);
        prodType.setNote(newNote);
        ezProducts.replace(id, prodType);
        return true;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        if (id == null || id<=0){
            throw new InvalidProductIdException();
        }
        if (!ezProducts.containsKey(id)) {
            return false;
        }
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        boolean success=shopDB.deleteProductType(id);
        if (!success)
            return false;

        ezProducts.remove(id);

        return true;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        // TODO get it from the DB
        List<ProductType> prodList = new LinkedList<>(ezProducts.values());
        return prodList;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        if (barCode == null || barCode.isEmpty() || !isValidBarCode(barCode)) {
            throw new InvalidProductCodeException();
        }

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        for (ProductType product : ezProducts.values()) {
            if (product.getBarCode().equals(barCode)) {
                return product;
            }
        }

        return null;
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }
        List<ProductType> filteredList = getAllProductTypes();

        // Doesn't match the description: remove.
        filteredList.removeIf(product -> !(product.getProductDescription().equals(description)));
        return filteredList;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
        if (productId == null || productId<=0){
            throw new InvalidProductIdException();
        }
        if (!ezProducts.containsKey(productId)) {
            return false;
        }
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        EZProductType prodType = ezProducts.get(productId);
        if ((toBeAdded > 0) || (toBeAdded < 0 && prodType.getQuantity() > Math.abs(toBeAdded))){
                    // If i need to remove 50 quantity (oBeAdded = -50), i must have quanity > abs(50).
            int q = prodType.getQuantity();


            boolean success = shopDB.updateProductType(productId, toBeAdded, prodType.getLocation(), prodType.getNote(), prodType.getProductDescription(), prodType.getBarCode(), prodType.getPricePerUnit());
            if (!success)
                return false;

            prodType.setQuantity(toBeAdded+q);
            ezProducts.replace(productId, prodType);

            return true;
        }


        return false;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        if (productId == null || productId<=0){
            throw new InvalidProductIdException();
        }
        if (!ezProducts.containsKey(productId)) {
            return false;
        }
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        if (newPos == null || newPos.isEmpty()){
            // Reset the location if null or empty
            EZProductType prodType = ezProducts.get(productId);
            boolean success = shopDB.updateProductType(productId, prodType.getQuantity(), "", prodType.getNote(), prodType.getProductDescription(), prodType.getBarCode(), prodType.getPricePerUnit());
            if (!success)
                return false;
            prodType.setLocation("");
            ezProducts.replace(productId, prodType);
            return true;
        }
        else {
            // position has to be unique: check if it is
            for (ProductType product : ezProducts.values()) {
                if (product.getLocation().equals(newPos)) {
                    return false;
                }
            }
        }

        //The position has the following format :
        //<aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>
        if (!(newPos.matches("[0-9]+-[a-zA-z]+-[0-9]+"))){
            // If it doens't match:
            throw new InvalidLocationException();
        }

        EZProductType prodType = ezProducts.get(productId);
        boolean success = shopDB.updateProductType(productId, prodType.getQuantity(), newPos, prodType.getNote(), prodType.getProductDescription(), prodType.getBarCode(), prodType.getPricePerUnit());
        if (!success)
            return false;
        prodType.setLocation(newPos);
        ezProducts.replace(productId, prodType);
        return true;
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        if (productCode == null || productCode.isEmpty() || !isValidBarCode(productCode)) {
            throw new InvalidProductCodeException();
        }

        if (quantity <= 0)
            throw new InvalidQuantityException();

        if (pricePerUnit <=0)
            throw new InvalidPricePerUnitException();

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        List<ProductType> pl = getAllProductTypes().stream().filter(p -> p.getBarCode().equals(productCode)).collect(Collectors.toList());
        if(pl.size() != 1)
            return -1;

        if (this.ezOrders == null)
            this.ezOrders = this.shopDB.selectAllOrders();

        int orderID = shopDB.insertOrder(defaultID, productCode, pricePerUnit, quantity, EZOrder.OSIssued);
        if (orderID == defaultID)
            return orderID;

        EZOrder newOrder = new EZOrder(orderID, -1, productCode, pricePerUnit, quantity, EZOrder.OSIssued);
        this.ezOrders.put(orderID, newOrder);

        return orderID;
    }

    /**
     * This method directly orders and pays <quantity> units of product with given <productCode>, each unit will be payed
     * <pricePerUnit> to the supplier. <pricePerUnit> can differ from the re-selling price of the same product. The
     * product might have no location assigned in this step.
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param productCode the code of the product to be ordered
     * @param quantity the quantity of product to be ordered
     * @param pricePerUnit the price to correspond to the supplier (!= than the resale price of the shop) per unit of
     *                     product
     *
     * @return  the id of the order (> 0)
     *          -1 if the product does not exists, if the balance is not enough to satisfy the order, if there are some
     *          problems with the db
     *
     * @throws InvalidProductCodeException if the productCode is not a valid bar code, if it is null or if it is empty
     * @throws InvalidQuantityException if the quantity is less than or equal to 0
     * @throws InvalidPricePerUnitException if the price per unit of product is less than or equal to 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        if (productCode == null || productCode.isEmpty() || !isValidBarCode(productCode)) {
            throw new InvalidProductCodeException();
        }

        if (quantity <= 0)
            throw new InvalidQuantityException();

        if (pricePerUnit <=0)
            throw new InvalidPricePerUnitException();

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        List<ProductType> pl = getAllProductTypes().stream().filter(p -> p.getBarCode().equals(productCode)).collect(Collectors.toList());
        if(pl.size() != 1)
            return -1;

        if(pricePerUnit*quantity > accountingBook.currentBalance)
            return -1;

        LocalDate time = LocalDate.now();

        // issue new order:
        int id = issueOrder(productCode, quantity, pricePerUnit);
        EZOrder order = ezOrders.get(id);

        if(!recordBalanceUpdate(-pricePerUnit*quantity))
            return -1;

        shopDB.updateOrder(order.getOrderId(), nextBalanceId, productCode, pricePerUnit, quantity, OSPayed); //todo: to be fixed ???

        return order.getOrderId();
    }

    /**
     * This method change the status the order with given <orderId> into the "PAYED" state. The order should be either
     * issued (in this case the status changes) or payed (in this case the method has no effect).
     * This method affects the balance of the system.
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param orderId the id of the order to be ORDERED
     *
     * @return  true if the order has been successfully ordered
     *          false if the order does not exist or if it was not in an ISSUED/ORDERED state
     *
     * @throws InvalidOrderIdException if the order id is less than or equal to 0 or if it is null.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        if (orderId == null || orderId <=0)
            throw new InvalidOrderIdException();

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        EZOrder order = ezOrders.get(orderId);

        if(order == null || !(order.getStatus().equals(OSIssued)))
            return false;

        if(!order.getStatus().equals(OSPayed))
        {
            if(!recordBalanceUpdate(-(order.getPricePerUnit() * order.getQuantity())))
                return false;

            shopDB.updateOrder(order.getOrderId(), nextBalanceId, order.getProductCode(), order.getPricePerUnit(), order.getQuantity(), OSPayed); //todo: to be fixed ???
        }

        return true;
    }

    /**
     * This method records the arrival of an order with given <orderId>. This method changes the quantity of available product.
     * The product type affected must have a location registered. The order should be either in the PAYED state (in this
     * case the state will change to the COMPLETED one and the quantity of product type will be updated) or in the
     * COMPLETED one (in this case this method will have no effect at all).
     * It can be invoked only after a user with role "Administrator" or "ShopManager" is logged in.
     *
     * @param orderId the id of the order that has arrived
     *
     * @return  true if the operation was successful
     *          false if the order does not exist or if it was not in an ORDERED/COMPLETED state
     *
     * @throws InvalidOrderIdException if the order id is less than or equal to 0 or if it is null.
     * @throws InvalidLocationException if the ordered product type has not an assigned location.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        if (orderId == null || orderId <=0)
            throw new InvalidOrderIdException();

        EZOrder order = ezOrders.get(orderId);

        if(order == null || !(order.getStatus().equals(OSIssued) || order.getStatus().equals(OSCompleted)))
            return false;

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        EZProductType prod = null;
        for(EZProductType product : ezProducts.values())
        {
            if(product.getBarCode().equals(order.getProductCode()))
            {
                prod = product;
                break;
            }
        }
        assert prod != null; //???
        if(prod.getLocation() == null)
            throw new InvalidLocationException();

        if(!order.getStatus().equals(OSCompleted))
        {
           if(!shopDB.updateOrder(order.getOrderId(), order.getBalanceId(), order.getProductCode(), order.getPricePerUnit(),
                   order.getQuantity(), OSCompleted))
               return false;
           order.setStatus(OSCompleted);

           if(!shopDB.updateProductType(prod.getId(), prod.getQuantity()+order.getQuantity(), prod.getLocation(),
                   prod.getNote(), prod.getProductDescription(), prod.getBarCode(), prod.getPricePerUnit()))
               return false;
           prod.editQuantity(order.getQuantity());
        }

        return true;
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        if (this.ezOrders == null)
            return new LinkedList<>();

        List<Order> orders = new LinkedList<>(this.ezOrders.values());

        return orders;
    }

    /**
     * This method saves a new customer into the system. The customer's name should be unique.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerName the name of the customer to be registered
     *
     * @return the id (>0) of the new customer if successful, -1 otherwise
     *
     * @throws InvalidCustomerNameException if the customer name is empty or null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        if (customerName == null || customerName.isEmpty()){
            throw new InvalidCustomerNameException();
        }
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URCashier, URShopManager)){
            throw new UnauthorizedException();
        }

        for (Customer customer : ezCustomers.values()) {
            if (customer.getCustomerName().equals(customerName)) {
                // Name should be unique
                return -1;
            }
        }

        Integer id = shopDB.insertCustomer(customerName, "");
        if (id != defaultID) {
            EZCustomer customer = new EZCustomer(id, customerName, "", defaultValue);
            ezCustomers.put(id, customer);
        }

        return id;
    }


    public boolean isValidCard(String card){
        if (card == null)
            return false;

        return ( card.isEmpty() || card.matches("\\b[0-9]{10}\\b") );
    }

    /**
     * This method updates the data of a customer with given <id>. This method can be used to assign/delete a card to a
     * customer. If <newCustomerCard> has a numeric value than this value will be assigned as new card code, if it is an
     * empty string then any existing card code connected to the customer will be removed and, finally, it it assumes the
     * null value then the card code related to the customer should not be affected from the update. The card code should
     * be unique and should be a string of 10 digits.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param id the id of the customer to be updated
     * @param newCustomerName the new name to be assigned
     * @param newCustomerCard the new card code to be assigned. If it is empty it means that the card must be deleted,
     *                        if it is null then we don't want to update the cardNumber
     *
     * @return true if the update is successful
     *          false if the update fails ( cardCode assigned to another user, db unreacheable)
     *
     * @throws InvalidCustomerNameException if the customer name is empty or null
     * @throws InvalidCustomerCardException if the customer card is empty, null or if it is not in a valid format (string with 10 digits)
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        // TODO Should we call the excepiont AND remove the card from the DB if the string is empty?

        if ( newCustomerName == null || newCustomerName.isEmpty() ){
            throw new InvalidCustomerNameException();
        }
        if ( newCustomerCard == null || !isValidCard(newCustomerCard)){
            throw new InvalidCustomerCardException();
        }
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URCashier, URShopManager)){
            throw new UnauthorizedException();
        }

        for (Customer c : ezCustomers.values()) {
            if ( !newCustomerCard.isEmpty() && c.getCustomerCard().equals(newCustomerCard)) {
                // if the update fails ( cardCode assigned to another user)
                // also the name must be unique
                return false;
            }
        }

        EZCustomer customer = ezCustomers.get(id);
        if (newCustomerCard.isEmpty()) {
            //if it is an empty string then any existing card code connected to the customer will be removed
            boolean deleted = shopDB.deleteCard(customer.getCustomerCard());
            if (deleted) {
                customer.setCustomerCard("");
                customer.setPoints(0);
                ezCustomers.replace(id, customer);
            }
            return deleted;
            //if (customer.getCustomerCard() != null && !customer.getCustomerCard().isEmpty())
            //       shopDB.deleteCard(customer.getCustomerCard());
        }

        boolean updated = shopDB.updateCustomer(id, newCustomerName, newCustomerCard);
        if (updated){
            customer.setCustomerName(newCustomerName);
            customer.setCustomerCard(newCustomerCard);
            ezCustomers.replace(id, customer);
        }

        return updated;

    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if (!ezCustomers.containsKey(id)) {
            return false;
        }
        if (id == null || id <=0 ){
            throw new InvalidCustomerIdException();
        }

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URCashier, URShopManager)){
            throw new UnauthorizedException();
        }

        boolean success = shopDB.deleteCustomer(id);
        if (!success)
            return false;

        ezUsers.remove(id);
        return true;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if (!ezCustomers.containsKey(id)) {
            return null;
        }
        if ( id==null || id <=0) {
            throw new InvalidCustomerIdException();
        }
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URCashier, URShopManager)){
            throw new UnauthorizedException();
        }

        Customer customer = ezCustomers.get(id);
        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URCashier, URShopManager)){
            throw new UnauthorizedException();
        }

        List<Customer> customerList = new LinkedList<>(ezCustomers.values());
        return customerList;
    }

    /**
     * This method returns a string containing the code of a new assignable card.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the code of a new available card. An empty string if the db is unreachable
     *
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public String createCard() throws UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        String cardCode = shopDB.insertCard(defaultValue);

        return cardCode;
    }

    /**
     * This method assigns a card with given card code to a customer with given identifier. A card with given card code
     * can be assigned to one customer only.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerCard the number of the card to be attached to a customer
     * @param customerId the id of the customer the card should be assigned to
     *
     * @return true if the operation was successful
     *          false if the card is already assigned to another user, if there is no customer with given id, if the db is unreachable
     *
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {

        if (customerId == null || customerId <=0){
            throw new InvalidCustomerIdException();
        }

        if (!ezCustomers.containsKey(customerId)) {
            return false;
        }

        // TODO test this method
        //verify if it's string with 10 digits!
        if (customerCard == null || customerCard.isEmpty() || !isValidCard(customerCard)){
            throw new InvalidCustomerCardException();
        }

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();

        EZCustomer customer = ezCustomers.get(customerId); // This functions checks if the customers map contains the ID.

        for (Customer cstmr : ezCustomers.values()) {
            if (cstmr.getCustomerCard().equals(customerCard))
                return false; //There is a customer with the given card
        }

        boolean attached = this.shopDB.updateCustomer(customerId, customer.getCustomerName(), customerCard);
        if (attached) {
            customer.setCustomerCard(customerCard);
            ezCustomers.replace(customerId, customer);
        }

        return attached;
    }

    /**
     * This method updates the points on a card adding to the number of points available on the card the value assumed by
     * <pointsToBeAdded>. The points on a card should always be greater than or equal to 0.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param customerCard the card the points should be added to
     * @param pointsToBeAdded the points to be added or subtracted ( this could assume a negative value)
     *
     * @return true if the operation is successful
     *          false   if there is no card with given code,
     *                  if pointsToBeAdded is negative and there were not enough points on that card before this operation,
     *                  if we cannot reach the db.
     *
     * @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {

        if (customerCard == null || !isValidCard(customerCard)){
            throw new InvalidCustomerCardException();
        }

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();

        for (Customer customer : ezCustomers.values()) {
            if (customer.getCustomerCard().equals(customerCard)){
                if ((pointsToBeAdded > 0) || (pointsToBeAdded < 0 && customer.getPoints() > Math.abs(pointsToBeAdded))){
                    // If i need to remove 50 points (pointsToBeAdded = -50), i must have points > abs(50).
                    int p = customer.getPoints();
                    customer.setPoints(pointsToBeAdded+p);
                    boolean success = shopDB.updateCard(customerCard, customer.getPoints());
                    if (success)
                        ezCustomers.replace(customer.getPoints(), (EZCustomer) customer);
                    return success;
                }
            }
        }
        return false;
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        return null;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        return false;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    public BalanceOperation getBalanceOpById(Integer balanceId) {
        return ezBalanceOperations.get(balanceId);
    } // serve???

    public EZSaleTransaction getSaleTransactionById(Integer saleNumber) {
        return  ezSaleTransactions.get(saleNumber);
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {

        if(saleNumber == null || saleNumber <= 0) throw new InvalidTransactionIdException();

        EZSaleTransaction sale = getSaleTransactionById(saleNumber);

        if(sale == null)
            return defaultID;

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        EZReturnTransaction retTr = new EZReturnTransaction(defaultID, saleNumber, null, defaultValue);
        int id = shopDB.insertReturnTransaction(null, saleNumber, defaultValue);
        if(id == -1) return -1;
        retTr.setReturnId(id);
        tmpRetTr = retTr;
        // return transaction is inserted in the proper lists only in endReturnTransaction() method (if commit == true)
        return retTr.getReturnId();
    }

    public EZReturnTransaction getReturnTransactionById(Integer returnId) {
        return ezReturnTransactions.get(returnId);
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId == null || returnId <= 0) throw new InvalidTransactionIdException();

        if(productCode == null || productCode.length() == 0  || !isValidBarCode(productCode)) throw new InvalidProductCodeException();

        if(amount <= 0) throw new InvalidQuantityException();

        ProductType product = getProductTypeByBarCode(productCode);

        if(product == null)
            return false;

        EZSaleTransaction sale = getSaleTransactionById(tmpRetTr.getItsSaleTransactionId());
        if(sale == null)
            return false;

        EZTicketEntry saleTicket = sale.getTicketEntryByBarCode(productCode);
        if(saleTicket == null)
            return false;

        if(saleTicket.getAmount() < amount)
            return false;

        EZTicketEntry returnTicket = new EZTicketEntry(productCode, product.getProductDescription(), amount, saleTicket.getPricePerUnit(), saleTicket.getDiscountRate());
        if(!shopDB.insertProductPerSale(productCode, tmpRetTr.getReturnId(), amount, saleTicket.getDiscountRate()))
            return false;

        tmpRetTr.getEntries().add(returnTicket);
        tmpRetTr.updateReturnedValue(returnTicket.getTotal());

        return true;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId == null || returnId <= 0) throw new InvalidTransactionIdException();

        if(tmpRetTr == null || tmpRetTr.getStatus().equals(RTClosed))
            return false;

        if(commit)
        {
            EZSaleTransaction sale = getSaleTransactionById(tmpRetTr.getItsSaleTransactionId());

            EZReturnTransaction retToBeStored = new EZReturnTransaction(tmpRetTr); //copy the temporary return transaction

            // add ReturnTransaction to return transactions list
            ezReturnTransactions.put(tmpRetTr.getReturnId(), retToBeStored);
            // add ReturnTransaction to SaleTransaction's list of returns
            sale.getReturns().add(retToBeStored);

            EZTicketEntry ezticket;

            for ( TicketEntry ticket: tmpRetTr.getEntries())
            {
                ezticket = (EZTicketEntry) ticket;
                EZProductType product = null;
                for (EZProductType p : ezProducts.values())
                {
                    if(p.getBarCode().equals(ezticket.getBarCode()))
                    {
                        product = p;
                        break;
                    }
                }

                assert product != null;
                // update (increase) quantity on the shelves
                if(!shopDB.updateProductType(product.getId(), product.getQuantity()+ezticket.amount, product.getLocation(),
                    product.getNote(), product.getProductDescription(), product.getBarCode(), product.getPricePerUnit()))
                    return false;
                product.editQuantity(+ezticket.getAmount());//re-place products on shelves

                // update (decrease) number of sold products (in related sale transaction)
                EZTicketEntry oldSaleTicket = getSaleTransactionById(tmpRetTr.getItsSaleTransactionId()).getTicketEntryByBarCode(ezticket.getBarCode());
                if(shopDB.updateProductPerSale(product.getBarCode(), sale.getTicketNumber(), oldSaleTicket.getAmount()-ezticket.getAmount(), oldSaleTicket.getDiscountRate()))
                    return false;
                getSaleTransactionById(tmpRetTr.getItsSaleTransactionId()).getTicketEntryByBarCode(ezticket.getBarCode()).updateAmount(-ezticket.getAmount()) ;


                // update (decrease) final price of related sale transaction
                /*if(*/shopDB.updateSaleTransaction(sale.getTicketNumber(), sale.getDiscountRate(), sale.getPrice()-ezticket.getTotal());//== false) return false;
                getSaleTransactionById(tmpRetTr.getItsSaleTransactionId()).updatePrice(-ezticket.getTotal()); //update final price of sale transaction
            }
            retToBeStored.setStatus(RTClosed);
            // update ReturnTransaction in DB:
            if(!shopDB.updateReturnTransaction(retToBeStored.getReturnId(), retToBeStored.getItsSaleTransactionId(), retToBeStored.getReturnedValue()))
                return false;
            // clear the temporary transaction:
            tmpRetTr = null;
        }
        else
        {//rollback ---> delete the transaction from DB and clear tmpRetTr (clear also related tickets)
            //delete the return tickets from DB:
            EZTicketEntry ezticket;
            for ( TicketEntry ticket: tmpRetTr.getEntries())
            {
                ezticket = (EZTicketEntry) ticket;
                /*if(!*/shopDB.deleteProductPerSale(ezticket.getBarCode(), tmpRetTr.getReturnId());//)
                    //return false;
            }

            if(!shopDB.deleteTransaction(tmpRetTr.getReturnId()))
                return false;

            //clear the temporary transaction:
            tmpRetTr = null;
        }

        return true;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId == null || returnId <= 0) throw new InvalidTransactionIdException();

        EZReturnTransaction retTr = getReturnTransactionById(returnId);

        if(retTr == null) return false;

        if(retTr.getStatus().equals(RTPayed) || !(retTr.getStatus().equals(RTClosed))) return false;

        /* "This method deletes a CLOSED (but not PAYED) return transaction. It affects the quantity of product sold in
         the connected sale transaction (and consequently its price) and the quantity of product available on the shelves." */

        EZSaleTransaction sale = getSaleTransactionById(getReturnTransactionById(returnId).getItsSaleTransactionId());
        EZTicketEntry ezticket;

        for (TicketEntry ticket: retTr.getEntries())
        {
            ezticket = (EZTicketEntry) ticket;
            EZProductType product = null; //getProductTypeByBarCode(ezticket.getBarCode());
            for (EZProductType p : ezProducts.values())
            {
                if(p.getBarCode().equals(ezticket.getBarCode()))
                {
                    product = p;
                    break;
                }
            }

            assert product != null; //???
            // re-update (decrease) quantity on the shelves
            if(!shopDB.updateProductType(product.getId(), product.getQuantity()-ezticket.getAmount(), product.getLocation(),
                product.getNote(), product.getProductDescription(), product.getBarCode(), product.getPricePerUnit()))
                return false;
            product.editQuantity(-ezticket.getAmount());

            // re-update (increase) number of sold products (in related sale transaction)
            EZTicketEntry oldSaleTicket = getSaleTransactionById(retTr.getItsSaleTransactionId()).getTicketEntryByBarCode(ezticket.getBarCode());
            if(shopDB.updateProductPerSale(product.getBarCode(), sale.getTicketNumber(), oldSaleTicket.getAmount()+ezticket.getAmount(), oldSaleTicket.getDiscountRate()))
                return false;
            getSaleTransactionById(tmpRetTr.getItsSaleTransactionId()).getTicketEntryByBarCode(ezticket.getBarCode()).updateAmount(+ezticket.getAmount()) ;

            // re-update (increase) final price of related sale transaction
            /*if(*/shopDB.updateSaleTransaction(sale.getTicketNumber(), sale.getDiscountRate(), sale.getPrice()+ezticket.getTotal());//== false) return false;
            getSaleTransactionById(retTr.getItsSaleTransactionId()).setPrice(+ezticket.getTotal());

            //delete also the return ticket from DB:
            /*if(!*/shopDB.deleteProductPerSale(ezticket.getBarCode(), retTr.getReturnId());//)
                //return false;

        }
        // remove the almost deleted return transaction from related lists:
        // remove ReturnTransaction from SaleTransaction's list of returns
        sale.getReturns().remove(retTr); // todo: verify if it is possible to use this type of remove
        // remove ReturnTransaction from return transactions list
        ezReturnTransactions.remove(retTr.getReturnId(), retTr); // todo: verify if it is possible to use this type of remove

        if(!shopDB.deleteTransaction(retTr.getReturnId()))
            return false;

        return true;
    }

    static boolean verifyByLuhnAlgo(String ccNumber)
    {
        int sum = 0;
        boolean alternate = false;
        for (int i = ccNumber.length() - 1; i >= 0; i--)
        {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
            if (alternate)
            {
                n *= 2;
                if (n > 9)
                    n = (n % 10) + 1;
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }


    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {

        if(ticketNumber == null || ticketNumber <= 0) throw new InvalidTransactionIdException();

        SaleTransaction sale = getSaleTransaction(ticketNumber);

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(cash <= 0) throw new InvalidPaymentException();

        if(sale == null || cash < sale.getPrice())
            return -1;

        if(!recordBalanceUpdate(sale.getPrice()))
            return -1; // return -1 if DB connection problems occur

        return (cash - sale.getPrice());
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {

        if(ticketNumber == null || ticketNumber <= 0) throw new InvalidTransactionIdException();

        SaleTransaction sale = getSaleTransaction(ticketNumber);

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();


        if(creditCard == null || !verifyByLuhnAlgo(creditCard) || creditCard.equals("")) throw new InvalidCreditCardException();

        /*if(sale == null ||
            getCreditCardFromTXT(ticketNumber) == null ||
            !verifyCreditCardBalance(ticketNumber, creditCard))
            return false;*/

        // todo: keep money from credit card (from txt/JSON (?))


        if(!recordBalanceUpdate(sale.getPrice()))
            return false; // return false if DB connection problems occur

        return true;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId <= 0) throw new InvalidTransactionIdException(); // not "returnId == null ||" ???

        EZReturnTransaction retTr = getReturnTransactionById(returnId);

        if(retTr == null) return -1;

        if(!retTr.getStatus().equals(RTClosed)) return -1;

        double returnedMoney = retTr.getReturnedValue();

        if(!recordBalanceUpdate(-returnedMoney))
            return -1; // return -1 if DB connection problems occur

        return returnedMoney;

    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId <= 0) throw new InvalidTransactionIdException(); // not "returnId == null ||" ???

        if(creditCard == null || !verifyByLuhnAlgo(creditCard) || creditCard.equals("")) throw new InvalidCreditCardException();

        EZReturnTransaction retTr = getReturnTransactionById(returnId);

        if(retTr == null) return -1;
        if(!retTr.getStatus().equals(RTClosed)) return -1;

        //if(getCreditCardFromTXT(ticketNumber) == null) return -1;

        double returnedMoney = retTr.getReturnedValue();

        if(!recordBalanceUpdate(-returnedMoney))
            return -1; // return -1 if DB connection problems occur

        // todo: give money to credit card

        return returnedMoney;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager) )
            throw new UnauthorizedException();

        String type;

        if(toBeAdded >= 0)
            type = Credit;
        else
            type = Debit;

        LocalDate time = LocalDate.now();
        // create new balance operation
        EZBalanceOperation op = new EZBalanceOperation(defaultID, time, toBeAdded, type);
        int id = shopDB.insertBalanceOperation(time, toBeAdded, type);
        if(id == -1) return false; //return false if DB connection problem occurs
        op.setBalanceId(id);
        ezBalanceOperations.put(op.getBalanceId(), op);
        nextBalanceId = op.getBalanceId(); // used to pay orders ???

        accountingBook.updateBalance(toBeAdded);

        // should do anything else???

        return !((toBeAdded + accountingBook.currentBalance) < 0);
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        LinkedList<BalanceOperation> balanceOperations = new LinkedList<>(ezBalanceOperations.values());
        List<BalanceOperation> filteredBalanceOperations = new LinkedList<>();

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager) )
            throw new UnauthorizedException();

        LocalDate startingDate;
        LocalDate endingDate;
        if(from != null && to != null) {
            startingDate = from.isAfter(to) ? to : from;
            endingDate = from.isAfter(to) ? from : to;

            filteredBalanceOperations = balanceOperations.stream()
                    .filter( op -> op.getDate().isAfter(startingDate) && op.getDate().isBefore(endingDate))
                    .collect(Collectors.toList());
        }
        else if(from != null) {
            startingDate = from;
            filteredBalanceOperations = balanceOperations.stream()
                    .filter( op -> op.getDate().isAfter(startingDate))
                    .collect(Collectors.toList());
        }
        else if(to != null) {
            endingDate = to;
            filteredBalanceOperations = balanceOperations.stream()
                    .filter( op -> op.getDate().isBefore(endingDate))
                    .collect(Collectors.toList());
        }
        //else --> both startingDate and endingDate are null

        return filteredBalanceOperations;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager) )
            throw new UnauthorizedException();

        if (accountingBook != null)
            return accountingBook.currentBalance;

        return -1;
    }

    private void testDB() {
        // TODO: remove all this stuff before delivery
        if (!USE_TEST_DB)
            return;

        this.shopDB.insertUser("admin", "admin", URAdministrator);
        this.shopDB.insertUser("sheldon", "pwd", URAdministrator);
        this.shopDB.insertUser("manager", "manager", URShopManager);
        this.shopDB.insertUser("aldo", "pwd", URShopManager);
        this.shopDB.insertUser("cashier", "cashier", URCashier);
        this.shopDB.insertUser("giovanni", "pwd", URCashier);
        this.shopDB.insertUser("giacomo", "pwd", URCashier);

        String card = this.shopDB.insertCard(1200);
        String card2 = this.shopDB.insertCard(defaultValue);
        this.shopDB.insertCustomer("Leonard", card);
        this.shopDB.insertCustomer("Penny", card2);
        this.shopDB.insertCustomer("Raj", null);
        this.shopDB.insertCustomer("Cheerios", null);


        EZProductType prod1 = new EZProductType(defaultID, 5, "", "A simple note",
                "First product", "A0070Z", 12.50);
        prod1.setId( this.shopDB.insertProductType(prod1.getQuantity(), prod1.getLocation(), prod1.getNote(),
                        prod1.getProductDescription(), prod1.getBarCode(), prod1.getPricePerUnit()) );

        EZProductType prod2 = new EZProductType(defaultID, 5, "", "A simple note",
                "First product", "A0070Z", 12.50);
        prod2.setId( this.shopDB.insertProductType(prod2.getQuantity(), prod2.getLocation(), prod2.getNote(),
                prod2.getProductDescription(), prod2.getBarCode(), prod2.getPricePerUnit()) );

        this.shopDB.insertOrder(defaultID, prod2.getBarCode(), prod2.getPricePerUnit(), prod2.getQuantity(), OSPayed);
    }
}
