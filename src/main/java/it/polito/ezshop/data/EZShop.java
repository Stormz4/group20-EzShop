package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;

import java.io.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
    final static String creditCardsFile = "src/main/java/it/polito/ezshop/utils/CreditCards.txt";

    private final SQLiteDB shopDB = new SQLiteDB();
    private EZUser currUser = null;

    private HashMap<String, Integer> ezCards;
    private HashMap<Integer, EZCustomer> ezCustomers;
    private HashMap<Integer, EZOrder> ezOrders;
    private HashMap<Integer, EZProductType> ezProducts;
    private HashMap<Integer, EZUser> ezUsers;
    private EZReturnTransaction tmpRetTr;

    private EZAccountBook accountingBook;
    private HashMap<Integer, EZBalanceOperation> ezBalanceOperations;
    private HashMap<Integer, EZSaleTransaction> ezSaleTransactions;
    private HashMap<Integer, EZReturnTransaction> ezReturnTransactions;
    private EZSaleTransaction tmpSaleTransaction;

    //================================================================================================================//
    //                                                  Constructor                                                   //
    //================================================================================================================//
    public EZShop() {
        this.loadDataFromDB();

        accountingBook = new EZAccountBook(0);
        accountingBook.setCurrentBalance(shopDB.selectTotalBalance()); // starting balance
    }


    //================================================================================================================//
    //                                    Reset the application to its base state                                     //
    //================================================================================================================//
    @Override
    public void reset() {
        if ( !this.shopDB.isConnected() )
            this.shopDB.connect();

        if (this.shopDB.clearDatabase())
            this.clearData();
    }


    //================================================================================================================//
    //                                            Manage users and rights                                             //
    //================================================================================================================//
    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        if (username == null || username.isEmpty() ) {
            throw new InvalidUsernameException();
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException();
        }

        if (role.isEmpty() || !(role.equals(URAdministrator) || role.equals(URCashier) || role.equals(URShopManager))) {
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
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator)){
            throw new UnauthorizedException("Not authorized");
        }

        if (id == null || id <=0) {
            throw new InvalidUserIdException();
        }

        if (!ezUsers.containsKey(id)) {
            return false;
        }

        boolean success = shopDB.deleteUser(id);
        if (!success)
            return false;

        ezUsers.remove(id);

        return true;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator))
            throw new UnauthorizedException();

        List<User> usersList = new LinkedList<>(ezUsers.values());
        return usersList;
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if (id == null || id <= 0)
            throw new InvalidUserIdException();

        if (!ezUsers.containsKey(id))
            return null;

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator))
            throw new UnauthorizedException();

        User user = ezUsers.get(id);
        return user;
    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
        if (id==null || id <=0)
            throw new InvalidUserIdException();

        if (!ezUsers.containsKey(id))
            return false;

        if (role.isEmpty() || !(role.equals(URAdministrator) || role.equals(URCashier) || role.equals(URShopManager)))
            throw new InvalidRoleException();

        if((this.currUser == null) || (!this.currUser.hasRequiredRole(URAdministrator)))
            throw new UnauthorizedException();

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
        if (this.currUser != null)
            return null;

        if (username == null || username.isEmpty())
            throw new InvalidUsernameException();

        if (password == null || password.isEmpty())
            throw new InvalidPasswordException();

        //
        // Iterate the map and search the user
        for (EZUser user : ezUsers.values()) {
            if (user.getPassword().equals(password) && user.getUsername().equals(username))
                this.currUser = user;
        }

        return currUser;
    }

    @Override
    public boolean logout() {
        if (currUser == null)
            return false;

        currUser = null;
        return true;
    }


    //================================================================================================================//
    //                                                Manage Products                                                 //
    //================================================================================================================//
    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager))
            throw new UnauthorizedException();

        if (description == null || description.isEmpty() )
            throw new InvalidProductDescriptionException();

        if (productCode == null || productCode.isEmpty() || !isValidBarCode(productCode))
            throw new InvalidProductCodeException();

        if(pricePerUnit <= 0)
            throw new InvalidPricePerUnitException();

        // Check if the Barcode is unique
        for (ProductType product : ezProducts.values()) {
            if (product.getBarCode().equals(productCode)) {
                return defaultID;
            }
        }

        if (note == null)
            note = "";

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
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager))
            throw new UnauthorizedException();

        if (id == null || id <= 0)
            throw new InvalidProductIdException();

        if (newCode == null || newCode.isEmpty() || !isValidBarCode(newCode))
            throw new InvalidProductCodeException();

        if (newDescription == null || newDescription.isEmpty())
            throw new InvalidProductDescriptionException();

        if(newPrice <= 0)
            throw new InvalidPricePerUnitException();

        if (!ezProducts.containsKey(id))
            return false;

        // Check if the Barcode is unique
        for (EZProductType product : ezProducts.values()) {
            if (product.getBarCode().equals(newCode) && !product.getId().equals(id))
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
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager))
            throw new UnauthorizedException();

        if (id == null || id <= 0)
            throw new InvalidProductIdException();

        if (!ezProducts.containsKey(id))
            return false;

        if (!shopDB.deleteProductType(id))
            return false;

        ezProducts.remove(id);

        return true;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();

        return ezProducts != null ? new LinkedList<>(ezProducts.values()) : new LinkedList<>();
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager))
            throw new UnauthorizedException();

        if (barCode == null || barCode.isEmpty() || !isValidBarCode(barCode))
            throw new InvalidProductCodeException();

        for (ProductType product : ezProducts.values()) {
            if (product.getBarCode().equals(barCode))
                return product;
        }

        return null;
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager))
            throw new UnauthorizedException();

        String desc = description != null ? description : "";

        List<ProductType> filteredList = getAllProductTypes();
        // If product doesn't match the description, remove it from the list.
        filteredList.removeIf(product -> !(product.getProductDescription().equals(desc)));

        return filteredList;
    }


    //================================================================================================================//
    //                                          Manage Inventory and Orders                                           //
    //================================================================================================================//
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


            boolean success = shopDB.updateProductType(productId, q+toBeAdded, prodType.getLocation(), prodType.getNote(), prodType.getProductDescription(), prodType.getBarCode(), prodType.getPricePerUnit());
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
        if (!(isValidPosition(newPos))){
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

        // issue new order:
        int id = issueOrder(productCode, quantity, pricePerUnit);
        EZOrder order = ezOrders.get(id);

        if(!recordBalanceUpdate(-pricePerUnit*quantity))
            return -1;

        shopDB.updateOrder(order.getOrderId(), accountingBook.nextBalanceId, productCode, pricePerUnit, quantity, OSPayed);
        order.setBalanceId(accountingBook.nextBalanceId);
        order.setStatus(OSPayed);

        return order.getOrderId();
    }

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

        // Additional check on balance current value (even if it is not requested by API):
        if(order.getPricePerUnit()*order.getQuantity() > accountingBook.currentBalance)
            return false;

        if(!order.getStatus().equals(OSPayed)) {
            if(!recordBalanceUpdate(-(order.getPricePerUnit() * order.getQuantity())))
                return false;

            shopDB.updateOrder(order.getOrderId(), accountingBook.nextBalanceId, order.getProductCode(), order.getPricePerUnit(), order.getQuantity(), OSPayed);
            order.setBalanceId(accountingBook.nextBalanceId);
            order.setStatus(OSPayed);
        }

        return true;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        if (orderId == null || orderId <=0)
            throw new InvalidOrderIdException();

        EZOrder order = ezOrders.get(orderId);

//        if(order == null || !(order.getStatus().equals(OSIssued) || order.getStatus().equals(OSCompleted)))
//            return false;

        if(order == null || !(order.getStatus().equals(OSPayed) || order.getStatus().equals(OSCompleted)))
            return false;

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager)){
            throw new UnauthorizedException();
        }

        EZProductType prod = null;
        for(EZProductType product : ezProducts.values()) {
            if(product.getBarCode().equals(order.getProductCode())) {
                prod = product;
                break;
            }
        }

        if (prod == null)
            return false;

        if( !isValidPosition(prod.getLocation()) )
            throw new InvalidLocationException();

        if(!order.getStatus().equals(OSCompleted)) {
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


    //================================================================================================================//
    //                                          Manage customers and cards                                            //
    //================================================================================================================//
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

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {

        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URCashier, URShopManager))
            throw new UnauthorizedException();

        if ( newCustomerName == null || newCustomerName.isEmpty() )
            throw new InvalidCustomerNameException();

        if ( newCustomerCard != null && !isValidCard(newCustomerCard))
            throw new InvalidCustomerCardException();

        EZCustomer customer = ezCustomers.get(id);
        String customerCard = customer.getCustomerCard();

        if (newCustomerCard != null) {
            if (newCustomerCard.isEmpty()) {
                if ( shopDB.deleteCard(customer.getCustomerCard()) ) {
                    customerCard="";
                    customer.setCustomerCard("");
                    customer.setPoints(0);

                }
                else
                    return false;
            }
            else if (this.ezCards.get(newCustomerCard) != null) {
                for (Customer c: ezCustomers.values()) {
                    if (c.getCustomerCard().equals(newCustomerCard))
                        return false;   // Card already assigned to another customer
                }

                customerCard = newCustomerCard; // Card can be assigned to given customer
            }
            else
                return false;
        }

        boolean updated = shopDB.updateCustomer(id, newCustomerName, customerCard);
        if (updated){
            customer.setCustomerName(newCustomerName);
            customer.setCustomerCard(customerCard);

            Integer points = this.ezCards.get(customerCard);
            customer.setPoints(points);
        }

        return updated;
    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URCashier, URShopManager)){
            throw new UnauthorizedException();
        }

        if (id == null || id <=0 ){
            throw new InvalidCustomerIdException();
        }

        if (!ezCustomers.containsKey(id)) {
            return false;
        }

        boolean success = shopDB.deleteCustomer(id);
        if (!success)
            return false;

        ezUsers.remove(id);
        return true;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URCashier, URShopManager)){
            throw new UnauthorizedException();
        }


        if ( id==null || id <=0) {
            throw new InvalidCustomerIdException();
        }

        if (!ezCustomers.containsKey(id)) {
            return null;
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

    @Override
    public String createCard() throws UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        String cardCode = shopDB.insertCard(defaultValue);
        if (!cardCode.isEmpty())
           this.ezCards.put(cardCode, defaultValue);

        return cardCode;
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();

        if (customerId == null || customerId <=0){
            throw new InvalidCustomerIdException();
        }

        if (!ezCustomers.containsKey(customerId)) {
            return false;
        }

        //verify if it's string with 10 digits!
        if (customerCard == null || customerCard.isEmpty() || !isValidCard(customerCard)){
            throw new InvalidCustomerCardException();
        }


        EZCustomer customer = ezCustomers.get(customerId); // This functions checks if the customers map contains the ID.

        for (Customer cstmr : ezCustomers.values()) {
            if (cstmr.getCustomerCard().equals(customerCard))
                return false; //There is a customer with the given card
        }

        boolean attached = this.shopDB.updateCustomer(customerId, customer.getCustomerName(), customerCard);
        if (attached) {
            this.shopDB.updateCard(customerCard, 0);
            customer.setCustomerCard(customerCard);
            customer.setPoints(0);
            ezCustomers.replace(customerId, customer);
        }

        return attached;
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();

        if (customerCard == null || customerCard.isEmpty() || !isValidCard(customerCard)){
            throw new InvalidCustomerCardException();
        }


        for (Customer customer : ezCustomers.values()) {
            if (customer.getCustomerCard().equals(customerCard)){
                if ((pointsToBeAdded > 0) || (pointsToBeAdded < 0 && customer.getPoints() > Math.abs(pointsToBeAdded))){
                    // If i need to remove 50 points (pointsToBeAdded = -50), i must have points > abs(50).
                    int newPoints = customer.getPoints() + pointsToBeAdded;
                    if ( shopDB.updateCard(customerCard, newPoints) ) {
                        customer.setPoints(newPoints);
                        this.ezCards.replace(customerCard, newPoints);
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    //================================================================================================================//
    //                                           Manage a sale transaction                                            //
    //================================================================================================================//
    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        Integer nextTicketNumber;
        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();
        nextTicketNumber = this.shopDB.insertSaleTransaction(new LinkedList<TicketEntry>(), 0, 0, EZSaleTransaction.STOpened); // update the DB
        this.ezSaleTransactions.put(nextTicketNumber, new EZSaleTransaction(nextTicketNumber)); // update locally
        /*
         if (ezSaleTransactions.isEmpty()) {
            nextTicketNumber = 0;
        }
        else {
            nextTicketNumber = Collections.max(this.ezSaleTransactions.keySet()) + 1;
        }
        tmpSaleTransaction = new EZSaleTransaction(nextTicketNumber);
        */
        return nextTicketNumber;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        EZTicketEntry ticketEntryToAdd;
        ProductType scannedProduct = null;
        EZSaleTransaction currentSaleTransaction;
        boolean result = false;

        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();
        if (amount < 0)
            throw new InvalidQuantityException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        if (productCode == null || productCode.isEmpty() || !isValidBarCode(productCode)) {
            throw new InvalidProductCodeException();
        }
        try {
            for (ProductType product : ezProducts.values()) {
                if (product.getBarCode().equals(productCode)) {
                    scannedProduct = product;
                }
            }
            currentSaleTransaction = this.getSaleTransactionById(transactionId);
            if (scannedProduct != null && currentSaleTransaction != null && currentSaleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened)) {
                if(this.updateQuantity(scannedProduct.getId(), -amount)) {
                    ticketEntryToAdd = new EZTicketEntry(productCode, scannedProduct.getProductDescription(), amount, scannedProduct.getPricePerUnit(), 0);
                    if(this.shopDB.insertProductPerSale(productCode, currentSaleTransaction.getTicketNumber(), amount, 0)) {
                        currentSaleTransaction.getEntries().add(ticketEntryToAdd);
                        double newPrice = currentSaleTransaction.getPrice() + scannedProduct.getPricePerUnit() * amount;
                        if(this.shopDB.updateSaleTransaction(currentSaleTransaction.getTicketNumber(), 0, newPrice, currentSaleTransaction.getStatus())) {
                            currentSaleTransaction.setPrice(currentSaleTransaction.getPrice() + scannedProduct.getPricePerUnit() * amount); // update total price
                            result = true;
                        }
                        else { // rollback
                            currentSaleTransaction.getEntries().remove(ticketEntryToAdd);
                            this.shopDB.deleteProductPerSale(productCode, currentSaleTransaction.getTicketNumber());
                            this.updateQuantity(scannedProduct.getId(), amount);
                        }
                    }
                    else { // rollback
                        this.updateQuantity(scannedProduct.getId(), amount);
                    }
                }
            }
        }
        catch (InvalidProductIdException e) { // the method returns false (does not modify result)
        }
        return result;
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        ProductType productToRemove = null;
        TicketEntry ticketToUpdate;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();
        if (amount <= 0)
            throw new InvalidQuantityException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        if (productCode == null || productCode.isEmpty() || !isValidBarCode(productCode)) {
            throw new InvalidProductCodeException();
        }
        /*
        try {
            saleTransaction = (EZSaleTransaction) this.getSaleTransactionById(transactionId);
            if (saleTransaction != null) {
                ticketToUpdate = saleTransaction.getEntries().stream().filter(product -> product.getBarCode().equals(productCode))
                        .findFirst().orElse(null);
                productToRemove = this.getProductTypeByBarCode(productCode);
                if (ticketToUpdate != null && ticketToUpdate.getAmount() >= amount && saleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened)) {
                    if (ticketToUpdate.getAmount() == amount) { // we have to remove all the products of this type
                        saleTransaction.getEntries().remove(ticketToUpdate);
                    }
                    if (ticketToUpdate.getAmount() > amount) {
                        ticketToUpdate.setAmount(ticketToUpdate.getAmount() - amount);
                    }
                    saleTransaction.setPrice(saleTransaction.getPrice() - productToRemove.getPricePerUnit()*amount); // update total price
                    this.updateQuantity(productToRemove.getId(), amount);
                    result = true;
                }
            }
        }
         */
        try {
            saleTransaction = this.getSaleTransactionById(transactionId);
            for (ProductType product : ezProducts.values()) {
                if (product.getBarCode().equals(productCode)) {
                    productToRemove = product;
                }
            }
            if (saleTransaction != null && productToRemove != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened)) {
                ticketToUpdate = saleTransaction.getEntries().stream().filter(product -> product.getBarCode().equals(productCode))
                        .findFirst().orElse(null);
                if (ticketToUpdate != null && ticketToUpdate.getAmount() >= amount) { // qty is sufficient
                    if (ticketToUpdate.getAmount() == amount) { // we have to remove all the products of this type
                        if (this.shopDB.deleteProductPerSale(productCode, transactionId)){
                            saleTransaction.getEntries().remove(ticketToUpdate);
                            result = true;
                        }
                    }
                    if (ticketToUpdate.getAmount() > amount) {
                        if (this.shopDB.updateProductPerSale(productCode, transactionId, ticketToUpdate.getAmount()-amount, ticketToUpdate.getDiscountRate())){
                            ticketToUpdate.setAmount(ticketToUpdate.getAmount() - amount);
                            result = true;
                        }
                    }
                    if (result) {
                        double newPrice = saleTransaction.getPrice() - productToRemove.getPricePerUnit()*amount;
                        if (this.shopDB.updateSaleTransaction(transactionId, saleTransaction.getDiscountRate(), newPrice, saleTransaction.getStatus())){
                            saleTransaction.setPrice(saleTransaction.getPrice() - productToRemove.getPricePerUnit()*amount); // update total price
                            if (this.updateQuantity(productToRemove.getId(), amount)) {
                                result = true;
                            }
                            else { // rollback
                                double oldPrice = saleTransaction.getPrice() + productToRemove.getPricePerUnit()*amount;
                                this.shopDB.updateSaleTransaction(transactionId, saleTransaction.getDiscountRate(), oldPrice, saleTransaction.getStatus());
                                saleTransaction.setPrice(saleTransaction.getPrice() + productToRemove.getPricePerUnit()*amount);
                                if (ticketToUpdate.getAmount() == amount) {
                                    this.shopDB.insertProductPerSale(productCode, transactionId, amount, ticketToUpdate.getDiscountRate());
                                    saleTransaction.getEntries().add(ticketToUpdate);
                                }
                                else {
                                    this.shopDB.updateProductPerSale(productCode, transactionId, ticketToUpdate.getAmount()+amount, ticketToUpdate.getDiscountRate());
                                    ticketToUpdate.setAmount(ticketToUpdate.getAmount() + amount);
                                }
                                result = false;
                            }
                        }
                        else { // rollback
                            if (ticketToUpdate.getAmount() == amount) {
                                this.shopDB.insertProductPerSale(productCode, transactionId, amount, ticketToUpdate.getDiscountRate());
                                saleTransaction.getEntries().add(ticketToUpdate);
                            }
                            else {
                                this.shopDB.updateProductPerSale(productCode, transactionId, ticketToUpdate.getAmount()+amount, ticketToUpdate.getDiscountRate());
                                ticketToUpdate.setAmount(ticketToUpdate.getAmount() + amount);
                            }
                            result = false;
                        }
                    }
                }
            }
        }
        catch (InvalidProductIdException e) { // if the Product Code is correct, the Product Id is correct too
        }
        return result;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        TicketEntry ticketToUpdate;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();
        if (discountRate < 0 || discountRate >= 1)
            throw new InvalidDiscountRateException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        if (productCode == null || productCode.isEmpty() || !isValidBarCode(productCode)) {
            throw new InvalidProductCodeException();
        }
        saleTransaction = this.getSaleTransactionById(transactionId);
        if (saleTransaction != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened)) {
            ticketToUpdate = saleTransaction.getEntries().stream().filter(product -> product.getBarCode().equals(productCode))
                    .findFirst().orElse(null);
            if (ticketToUpdate != null && this.shopDB.updateProductPerSale(productCode, transactionId, ticketToUpdate.getAmount(), discountRate)) {
                ticketToUpdate.setDiscountRate(discountRate);
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();
        if (discountRate < 0 || discountRate >= 1)
            throw new InvalidDiscountRateException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = this.getSaleTransactionById(transactionId);
        if (saleTransaction != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened, EZSaleTransaction.STClosed)) {
            if(this.shopDB.updateSaleTransaction(transactionId, discountRate, saleTransaction.getPrice(), saleTransaction.getStatus())){
                saleTransaction.setDiscountRate(discountRate); // Does this propagate to the list?
                result = true;
            }
        }
        return result;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        int pointsToAdd = -1;
        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = this.getSaleTransactionById(transactionId);
        if (saleTransaction != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened, EZSaleTransaction.STClosed, EZSaleTransaction.STPayed)) {
            pointsToAdd = (int) Math.floor(saleTransaction.getPrice()/10);
        }
        return pointsToAdd;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = this.getSaleTransactionById(transactionId);
        if (saleTransaction != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened)) {
            if (this.shopDB.updateSaleTransaction(transactionId, saleTransaction.getDiscountRate(), saleTransaction.getPrice(), EZSaleTransaction.STClosed)) {
                saleTransaction.setStatus(EZSaleTransaction.STClosed);
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();
        if (saleNumber == null || saleNumber <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = this.getSaleTransactionById(saleNumber);
        if (saleTransaction != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STClosed)){
            if (this.shopDB.deleteTransaction(saleNumber)){ // try to remove the SaleTransaction from the DB
                this.ezSaleTransactions.remove(saleNumber); // delete the SaleTransaction in the local collection
                result = true;
            }
        }
        return result;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        if (this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier))
            throw new UnauthorizedException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = this.ezSaleTransactions.values().stream()
                .filter(sale -> sale.getTicketNumber().equals(transactionId) && sale.hasRequiredStatus(EZSaleTransaction.STClosed))
                .findFirst().orElse(null);
        return saleTransaction;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {

        if(saleNumber == null || saleNumber <= 0) throw new InvalidTransactionIdException();

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        EZSaleTransaction sale = getSaleTransactionById(saleNumber);

        if(sale == null || !sale.hasRequiredStatus(EZSaleTransaction.STPayed))
            return defaultID;

        if(ezReturnTransactions == null)
            ezReturnTransactions = new HashMap<Integer, EZReturnTransaction>();

        List<TicketEntry> entries = new LinkedList<TicketEntry>();
        EZReturnTransaction retTr = new EZReturnTransaction(defaultID, saleNumber, entries, defaultValue, RTOpened);
        int id = shopDB.insertReturnTransaction(entries, saleNumber, defaultValue, RTOpened);
        if(id == -1) return -1;
        retTr.setReturnId(id);
        retTr.setStatus(RTOpened);
        tmpRetTr = retTr;
        // return transaction is inserted in the proper lists only in endReturnTransaction() method (if commit == true)
        return retTr.getReturnId();
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

        if(tmpRetTr == null)
            return false;

        EZSaleTransaction sale = getSaleTransactionById(tmpRetTr.getSaleTransactionId());
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

        List<TicketEntry> entries = tmpRetTr.getEntries();
        if(entries == null) {
            entries = new LinkedList<TicketEntry>();
            tmpRetTr.setEntries(entries);
        }
        tmpRetTr.getEntries().add(returnTicket);
        tmpRetTr.updateReturnedValue(returnTicket.getTotal());

        return true;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException();

        if(tmpRetTr == null || tmpRetTr.getStatus().equals(RTClosed))
            return false;

        EZSaleTransaction sale = getSaleTransactionById(tmpRetTr.getSaleTransactionId());

        if(commit) {
            EZReturnTransaction retToBeStored = new EZReturnTransaction(tmpRetTr); //copy the temporary return transaction

            // add ReturnTransaction to return transactions list
            if(ezReturnTransactions == null)
                ezReturnTransactions = new HashMap<Integer, EZReturnTransaction>();
            ezReturnTransactions.put(tmpRetTr.getReturnId(), retToBeStored);

            // add ReturnTransaction to SaleTransaction's list of returns
            List<EZReturnTransaction> returns = sale.getReturns();
            if(returns == null)
                 returns = new LinkedList<EZReturnTransaction>();
            sale.setReturns(returns);
            sale.getReturns().add(retToBeStored);

            EZTicketEntry ezticket;

            for ( TicketEntry ticket: tmpRetTr.getEntries()) {
                ezticket = (EZTicketEntry) ticket;
                EZProductType product = null;
                for (EZProductType p : ezProducts.values()) {
                    if(p.getBarCode().equals(ezticket.getBarCode())) {
                        product = p;
                        break;
                    }
                }

                if(product == null)
                    return false;
                // update (increase) quantity on the shelves
                if(!shopDB.updateProductType(product.getId(), product.getQuantity()+ezticket.amount, product.getLocation(),
                    product.getNote(), product.getProductDescription(), product.getBarCode(), product.getPricePerUnit()))
                    return false;
                product.editQuantity(+ezticket.getAmount());//re-place products on shelves

                // update (decrease) number of sold products (in related sale transaction)
                EZTicketEntry oldSaleTicket = getSaleTransactionById(tmpRetTr.getSaleTransactionId()).getTicketEntryByBarCode(ezticket.getBarCode());
                if(!shopDB.updateProductPerSale(product.getBarCode(), sale.getTicketNumber(), oldSaleTicket.getAmount()-ezticket.getAmount(), oldSaleTicket.getDiscountRate()))
                    return false;

                getSaleTransactionById(tmpRetTr.getSaleTransactionId()).getTicketEntryByBarCode(ezticket.getBarCode()).updateAmount(-ezticket.getAmount());

                // update (decrease) final price of related sale transaction
                double newPrice = sale.getPrice() - ezticket.getTotal();
                if(!shopDB.updateSaleTransaction(sale.getTicketNumber(), sale.getDiscountRate(), newPrice, sale.getStatus()))
                    return false;

                getSaleTransactionById(tmpRetTr.getSaleTransactionId()).updatePrice(-ezticket.getTotal()); //update final price of sale transaction
            }
            double saleDiscount = getSaleTransactionById(retToBeStored.getSaleTransactionId()).getDiscountRate();
            double oldReturnedValue = retToBeStored.getReturnedValue();
            retToBeStored.setReturnedValue(oldReturnedValue * (1-saleDiscount));
            retToBeStored.setStatus(RTClosed);
            // update ReturnTransaction in DB:
            if(!shopDB.updateReturnTransaction(retToBeStored.getReturnId(), retToBeStored.getReturnedValue(), RTClosed))
                return false;
            // clear the temporary transaction:
            tmpRetTr = null;
        }
        else {
            // Rollback
            EZTicketEntry ezticket;
            for ( TicketEntry ticket: tmpRetTr.getEntries()) {
                // Delete the return tickets from DB
                if(!shopDB.deleteProductPerSale(ticket.getBarCode(), tmpRetTr.getReturnId()))
                    return false;

                /*
                List<TicketEntry> saleEntries = sale.getEntries();
                for (TicketEntry saleEntry : saleEntries) {
                    if (saleEntry.getBarCode().equals(ticket.getBarCode())) {
                        int amount = saleEntry.getAmount() + ticket.getAmount();
                        saleEntry.setAmount(amount);
                    }
                }*/
            }

            // Delete the transaction from DB
            if(!shopDB.deleteTransaction(tmpRetTr.getReturnId()))
                return false;

            // Clear the temporary transaction
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

        EZSaleTransaction sale = getSaleTransactionById(getReturnTransactionById(returnId).getSaleTransactionId());
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

            if (product == null)
                return false;

            // re-update (decrease) quantity on the shelves
            if(!shopDB.updateProductType(product.getId(), product.getQuantity()-ezticket.getAmount(), product.getLocation(),
                product.getNote(), product.getProductDescription(), product.getBarCode(), product.getPricePerUnit()))
                return false;
            product.editQuantity(-ezticket.getAmount());

            // re-update (increase) number of sold products (in related sale transaction)
            EZTicketEntry oldSaleTicket = getSaleTransactionById(retTr.getSaleTransactionId()).getTicketEntryByBarCode(ezticket.getBarCode());
            if(!shopDB.updateProductPerSale(product.getBarCode(), sale.getTicketNumber(), oldSaleTicket.getAmount()+ezticket.getAmount(), oldSaleTicket.getDiscountRate()))
                return false;
            getSaleTransactionById(retTr.getSaleTransactionId()).getTicketEntryByBarCode(ezticket.getBarCode()).updateAmount(+ezticket.getAmount()) ;

            // re-update (increase) final price of related sale transaction
            double newPrice = sale.getPrice()+ezticket.getTotal();
            if(!shopDB.updateSaleTransaction(sale.getTicketNumber(), sale.getDiscountRate(), newPrice, sale.getStatus()))
                return false;

            getSaleTransactionById(retTr.getSaleTransactionId()).updatePrice(+ezticket.getTotal());

            //delete also the return ticket from DB:
            if(!shopDB.deleteProductPerSale(ezticket.getBarCode(), retTr.getReturnId()))
                return false;

        }
        // remove the almost deleted return transaction from related lists:
        // remove ReturnTransaction from SaleTransaction's list of returns
        sale.getReturns().remove(retTr);
        // remove ReturnTransaction from return transactions list
        ezReturnTransactions.remove(retTr.getReturnId(), retTr);

        if(!shopDB.deleteTransaction(retTr.getReturnId()))
            return false;

        return true;
    }


    //================================================================================================================//
    //                                                 Manage payment                                                 //
    //================================================================================================================//
    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {

        if(ticketNumber == null || ticketNumber <= 0) throw new InvalidTransactionIdException();

        SaleTransaction sale = getSaleTransaction(ticketNumber);
        double toBePayed;


        if(cash <= 0) throw new InvalidPaymentException();

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(sale == null)
            return -1;

        toBePayed = sale.getPrice()*(1-sale.getDiscountRate());

        if (cash < toBePayed)
            return -1;

        if(!this.shopDB.updateSaleTransaction(sale.getTicketNumber(), sale.getDiscountRate(), sale.getPrice(), EZSaleTransaction.STPayed))
            return -1;
        ezSaleTransactions.get(sale.getTicketNumber()).setStatus(EZSaleTransaction.STPayed);

        if(!recordBalanceUpdate(toBePayed)) {
            this.shopDB.updateSaleTransaction(sale.getTicketNumber(), sale.getDiscountRate(), sale.getPrice(), EZSaleTransaction.STClosed);
            ezSaleTransactions.get(sale.getTicketNumber()).setStatus(EZSaleTransaction.STClosed);
            return -1; // return -1 if DB connection problems occur
        }

        return (cash - toBePayed);
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {

        double toBePayed;

        if(ticketNumber == null || ticketNumber <= 0) throw new InvalidTransactionIdException();

        if(this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(creditCard == null || !isValidCreditCard(creditCard) || creditCard.equals("")) throw new InvalidCreditCardException();

        EZSaleTransaction sale = (EZSaleTransaction) getSaleTransaction(ticketNumber);
        if(sale == null ||
                getCreditInTXTbyCardNumber(creditCard) == -1 ||
                !isValidCreditCard(creditCard))
            return false;

        double cardBalance = getCreditInTXTbyCardNumber(creditCard.toString());

        if(cardBalance < (sale.getPrice() * (1-sale.getDiscountRate())))
            return false; // return false if there aren't enough money in the selected credit card

        toBePayed = sale.getPrice()*(1-sale.getDiscountRate());

        if(!this.shopDB.updateSaleTransaction(sale.getTicketNumber(), sale.getDiscountRate(), sale.getPrice(), EZSaleTransaction.STPayed))
            return false;
        ezSaleTransactions.get(sale.getTicketNumber()).setStatus(EZSaleTransaction.STPayed);

        if(!recordBalanceUpdate(sale.getPrice())) {
            this.shopDB.updateSaleTransaction(sale.getTicketNumber(), sale.getDiscountRate(), sale.getPrice(), EZSaleTransaction.STClosed);
            ezSaleTransactions.get(sale.getTicketNumber()).setStatus(EZSaleTransaction.STClosed);
            return false; // return false if DB connection problems occur
        }

        if(!updateCreditInTXTbyCardNumber(creditCard, -(sale.getPrice() * (1 - sale.getDiscountRate()))))
            return false;

        return true;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {

        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId <= 0)
            throw new InvalidTransactionIdException();

        EZReturnTransaction retTr = getReturnTransactionById(returnId);

        if(retTr == null)
            return -1;

        if(!retTr.getStatus().equals(RTClosed))
            return -1;

        double returnedMoney = retTr.getReturnedValue();

        if(!recordBalanceUpdate(-returnedMoney))
            return -1; // return -1 if DB connection problems occur

        return returnedMoney;

    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId <= 0)
            throw new InvalidTransactionIdException();

        if(creditCard == null || !isValidCreditCard(creditCard) || creditCard.equals(""))
            throw new InvalidCreditCardException();

        EZReturnTransaction retTr = getReturnTransactionById(returnId);

        if(retTr == null || !retTr.getStatus().equals(RTClosed))
            return defaultID;

        if(getCreditInTXTbyCardNumber(creditCard) == -1 || !isValidCreditCard(creditCard))
            return defaultID;

        double returnedMoney = retTr.getReturnedValue();

        if(!recordBalanceUpdate(-returnedMoney))
            return defaultID; // return -1 if DB connection problems occur

        if(!updateCreditInTXTbyCardNumber(creditCard, +(retTr.getReturnedValue())))
            return defaultID;

        return returnedMoney;
    }


    //================================================================================================================//
    //                                                   Accounting                                                   //
    //================================================================================================================//
    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager) )
            throw new UnauthorizedException();

        double balanceValue = (this.shopDB != null && this.shopDB.isConnected()) ? shopDB.selectTotalBalance() : 0;
        if(accountingBook == null)
            accountingBook = new EZAccountBook(balanceValue);
        return accountingBook.addBalanceOperation(shopDB, toBeAdded, ezBalanceOperations);
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
                    .filter( op -> op.getDate().isAfter(startingDate) && op.getDate().isBefore(endingDate) ||
                            op.getDate().isEqual(startingDate) || op.getDate().isEqual(endingDate))
                    .collect(Collectors.toList());
        }
        else if(from != null) {
            startingDate = from;
            filteredBalanceOperations = balanceOperations.stream()
                    .filter( op -> op.getDate().isAfter(startingDate) || op.getDate().isEqual(startingDate))
                    .collect(Collectors.toList());
        }
        else if(to != null) {
            endingDate = to;
            filteredBalanceOperations = balanceOperations.stream()
                    .filter( op -> op.getDate().isBefore(endingDate) || op.getDate().isEqual(endingDate))
                    .collect(Collectors.toList());
        }
        else //--> both startingDate and endingDate are null:
            filteredBalanceOperations = balanceOperations;

        return filteredBalanceOperations;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        if( this.currUser == null || !this.currUser.hasRequiredRole(URAdministrator, URShopManager) )
            throw new UnauthorizedException();

        if (accountingBook != null)
            return accountingBook.currentBalance;

        return defaultID;
    }


    //================================================================================================================//
    //                                                     Utility                                                    //
    //================================================================================================================//
    public void  loadDataFromDB() {
        if ( !this.shopDB.isConnected() )
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

    static public boolean isValidBarCode(String barCode){
        if (barCode == null)
            return false;

        if (barCode.matches("[0-9]{12,14}")){
            int sum=0;
            int number=0;
            for(int i=0; i<barCode.length()-1; i++){
                number=Integer.parseInt(Character.toString(barCode.charAt(i)));
                if (barCode.length() == 12 || barCode.length()==14){
                    if (!(i%2==1)){
                        number=number*3;
                    }
                }
                else {
                    if (!(i % 2 == 0)) {
                        number = number * 3;
                    }
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

    static public boolean isValidPosition(String newPos){
        if (newPos == null)
            return false;

        return newPos.matches("[0-9]+-[a-zA-z]+-[0-9]+");
    }

    static public boolean isValidCard(String card){
        if (card == null)
            return false;

        return ( card.isEmpty() || card.matches("\\b[0-9]{10}\\b") );
    }

    public BalanceOperation getBalanceOpById(Integer balanceId) {
        return ezBalanceOperations.get(balanceId);
    }

    public EZSaleTransaction getSaleTransactionById(Integer saleNumber) {
        /*
        EZSaleTransaction saleTransaction;
        if (this.tmpSaleTransaction.getTicketNumber().equals(saleNumber)) {

            saleTransaction = tmpSaleTransaction;
        }
        else
            saleTransaction = ezSaleTransactions.get(saleNumber);
         */
        EZSaleTransaction saleTransaction = this.ezSaleTransactions.get(saleNumber);
        return saleTransaction;
    }

    public EZReturnTransaction getReturnTransactionById(Integer returnId) {
        if(tmpRetTr != null && returnId.equals(tmpRetTr.getReturnId()))
            return tmpRetTr;

        if(ezReturnTransactions == null)
            return null;

        return ezReturnTransactions.get(returnId);
    }

    static public boolean isValidCreditCard(String cardNumber) { // Verification based upon Luhn algorithm
        if(cardNumber == null)
            return false;

        // old regexp:
        // "^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\d{3})\d{11})$"

        if(!cardNumber.matches("[0-9]*"))
            return false;

        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--)
        {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
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

    static public double getCreditInTXTbyCardNumber(String cardNumber) {
        double cardBalance = -1;

        if(cardNumber == null)
            return -1;

        try {
            FileReader reader = new FileReader(creditCardsFile);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("#"))
                    continue;
                if(line.matches(cardNumber+";[0-9]*.[0-9]*"))
                {
                    if(!isValidCreditCard(cardNumber))
                        return -1;
                    String creditAsString = line.split(";")[1];
                    cardBalance = Double.parseDouble(creditAsString);
                    break;
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cardBalance;
    }

    static public boolean updateCreditInTXTbyCardNumber(String cardNumber, double toBeAdded) {
        double cardBalance;

        if(cardNumber == null)
            return false;

        if(!isValidCreditCard(cardNumber))
            return false;

        try {
            FileReader reader = new FileReader(creditCardsFile);
            // input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(reader);
            StringBuffer inputBuffer = new StringBuffer();
            String line, newLine, newValue;

            while ((line = file.readLine()) != null) {
                if(line.matches(cardNumber+";[0-9]*.[0-9]*")) {
                    String creditAsString = line.split(";")[1];
                    cardBalance = Double.parseDouble(creditAsString);

                    cardBalance += toBeAdded;
                    newValue = Double.toString(cardBalance);

                    newLine = line.split(";")[0] + ";" + newValue;
                    inputBuffer.append(newLine);
                    inputBuffer.append('\n');
                }
                else {
                    inputBuffer.append(line);
                    inputBuffer.append('\n');
                }
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(creditCardsFile);
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }

        return true;
    }


    /*  Some valid barCodes (12 digits):
        1345334543427
        4532344529689
        5839274928315
        4778293942845
        8397489193845
        1627482847283
        3738456849238
        4482847392351
        7293829484929
        4738294729395
        4627828478338
        4892937849335
        4839947221225
        1881930382935
        4908382312833
        2141513141144
    */
}
