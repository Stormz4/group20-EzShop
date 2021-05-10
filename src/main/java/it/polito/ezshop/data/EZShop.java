package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.lang.Math;
import java.util.stream.Collectors;

import static it.polito.ezshop.data.EZUser.*;


public class EZShop implements EZShopInterface {
    private final SQLiteDB shopDB = new SQLiteDB();
    private EZUser currUser = null;

    private HashMap<Integer, EZCustomer> ezCustomers;
    private HashMap<Integer, EZUser> ezUsers;
    private HashMap<Integer, EZProductType> ezProducts;

    // TODO verify is this map is needed
    private HashMap<String, EZCard> ezCards;

    private EZAccountBook accountingBook;
    private HashMap<Integer, EZBalanceOperation> ezBalanceOperations;
    private HashMap<Integer, EZSaleTransaction> ezSaleTransactions;
    private HashMap<Integer, EZReturnTransaction> ezReturnTransactions;
    private EZSaleTransaction tmpSaleTransaction;

    public void loadDataFromDB() {
        shopDB.connect();
        shopDB.initDatabase();

        if (ezBalanceOperations == null)
            ezBalanceOperations = shopDB.selectAllBalanceOperations();

        if (ezCards == null)
            ezCards = shopDB.selectAllCards();

        if (ezCustomers == null)
            ezCustomers = shopDB.selectAllCustomers();

        if (ezUsers == null)
            ezUsers = shopDB.selectAllUsers();

        if (ezProducts == null)
            ezProducts = shopDB.selectAllProductTypes();

        if (ezSaleTransactions == null)
            ezSaleTransactions = shopDB.selectAllSaleTransactions();
    }

    @Override
    public void reset() {

    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        if (username == null || username.isEmpty() ) {
            throw new InvalidUsernameException();
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException();
        }
        if(role.isEmpty() || !(role.equals("Administrator") || role.equals("Cashier") || role.equals("ShopManager"))){
            throw new InvalidRoleException();
        }

        for (User user : ezUsers.values()) {
            if (user.getUsername().equals(username)) {
                return -1;
            }
        }
        // TODO return -1 is there is an error while saving the user
        // Get the highest ID from the DB
        int maxKey = Collections.max(ezUsers.keySet());
        Integer id = maxKey+1;
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

        if(!currUser.getRole().equals("Administrator") || currUser == null){
            throw new UnauthorizedException();
        }

        ezUsers.remove(id);

        // TODO delete from DB
        return true;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        if(!currUser.getRole().equals("Administrator") || currUser == null){
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

        if(!currUser.getRole().equals("Administrator") || currUser == null){
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
        if(!currUser.getRole().equals("Administrator") || currUser == null){
            throw new UnauthorizedException();
        }

        EZUser user = ezUsers.get(id);
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

        this.loadDataFromDB();
        // this.testDB();

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
        // TODO insert a check" if it is not a number or if it is not a valid barcode": check if its unique
        if (productCode == null || productCode.isEmpty() || isValidBarCode(productCode)) {
            throw new InvalidProductCodeException();
        }
        if(pricePerUnit <=0){
            throw new InvalidPricePerUnitException();
        }
        if(!(currUser.getRole().equals("Administrator") || currUser.getRole().equals("ShopManager")) || currUser == null){
            throw new UnauthorizedException();
        }

        // Check if the Barcode is unique
        for (ProductType product : ezProducts.values()) {
            if (product.getBarCode().equals(productCode)) {
                return -1;
            }
        }
        // TODO return -1 is there is an error while saving the user
        // Get the highest ID from the DB
        int maxKey = Collections.max(ezProducts.keySet());
        Integer id = maxKey+1;

        EZProductType prodType;
        if (note==null){
            prodType = new EZProductType(id, 0, "", "", description, productCode, pricePerUnit);
        }
        else {
            prodType = new EZProductType(id, 0, "", note, description, productCode, pricePerUnit);
        }

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
        if(!(currUser.getRole().equals("Administrator") || currUser.getRole().equals("ShopManager")) || currUser == null){
            throw new UnauthorizedException();
        }

        // Check if the Barcode is unique
        for (EZProductType product : ezProducts.values()) {
            if (product.getBarCode().equals(newCode))
                return false;
        }

        // TODO update in the db
        EZProductType prodType = ezProducts.get(id);
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
        if(!(currUser.getRole().equals("Administrator") || currUser.getRole().equals("ShopManager")) || currUser == null){
            throw new UnauthorizedException();
        }

        ezProducts.remove(id);

        return false;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        if(!(currUser.getRole().equals("Administrator") || currUser.getRole().equals("ShopManager")) || currUser == null){
            throw new UnauthorizedException();
        }

        // TODO get it from the DB
        List<ProductType> prodList = new LinkedList<>(ezProducts.values());
        return prodList;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        if (barCode == null || barCode.isEmpty() || isValidBarCode(barCode)) {
            throw new InvalidProductCodeException();
        }

        if(!(currUser.getRole().equals("Administrator") || currUser.getRole().equals("ShopManager")) || currUser == null){
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

        if(!(currUser.getRole().equals("Administrator") || currUser.getRole().equals("ShopManager")) || currUser == null){
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
        if(!(currUser.getRole().equals("Administrator") || currUser.getRole().equals("ShopManager")) || currUser == null){
            throw new UnauthorizedException();
        }

        EZProductType product = ezProducts.get(productId);
        if ((toBeAdded > 0) || (toBeAdded < 0 && product.getQuantity() > Math.abs(toBeAdded))){
                    // If i need to remove 50 quantity (oBeAdded = -50), i must have quanity > abs(50).
            int q = product.getQuantity();
            product.setQuantity(toBeAdded+q);
            ezProducts.replace(productId, product);
            // TODO update in the DB
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
        //The position has the following format :
        //<aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>
        if (!(newPos.matches("[0-9]+-[a-zA-z]+-[0-9]+"))){
            // If it doens't match:
            throw new InvalidLocationException();
        }
        if(!(currUser.getRole().equals("Administrator") || currUser.getRole().equals("ShopManager")) || currUser == null){
            throw new UnauthorizedException();
        }

        if (newPos.isEmpty()){
            EZProductType prodType = ezProducts.get(productId);
            prodType.setLocation("");
            ezProducts.replace(productId, prodType);
            return false;
            // TODO update in the DB
        }
        else {
            // position has to be unique: check if it is
            for (ProductType product : ezProducts.values()) {
                if (product.getLocation().equals(newPos)) {
                    return false;
                }
            }
        }
        EZProductType prodType = ezProducts.get(productId);
        prodType.setLocation(newPos);
        ezProducts.replace(productId, prodType);
        return true;
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        return false;
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        return null;
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
        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        for (Customer customer : ezCustomers.values()) {
            if (customer.getCustomerName().equals(customerName)) {
                // Name should be unique
                return -1;
            }
        }

        // Name is not present in the DB
        // Get the highest ID from the DB
        int maxKey = Collections.max(ezCustomers.keySet());
        Integer id = maxKey+1;
        EZCustomer customer = new EZCustomer(id, customerName, null, null);

        // TODO insert in the DB
        ezCustomers.put(id, customer);

        return id;
    }


    public boolean isValidCard(String card){
        if (card == null)
            return false;

        return card.matches("\\b[0-9]{10}\\b");
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
        if ( newCustomerCard == null || (!newCustomerCard.isEmpty() && !isValidCard(newCustomerCard)) ){
            throw new InvalidCustomerCardException();
        }
        if( currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager")) ){
                throw new UnauthorizedException();
        }

        EZCustomer customer = ezCustomers.get(id);
        if (newCustomerCard.isEmpty()) {
            customer.setCustomerCard(null); // consider having a Card object inside Customer, instead of cardCode and Points
            customer.setPoints(0);

            if (customer.getCustomerCard() != null && !customer.getCustomerCard().isEmpty())
                shopDB.deleteCard(customer.getCustomerCard());
        }
        customer.setCustomerName(newCustomerName);
        customer.setCustomerCard(newCustomerCard);
        ezCustomers.replace(id, customer);
        return true;

    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if (!ezCustomers.containsKey(id)) {
            return false;
        }
        if (id == null || id <=0 ) {
            throw new InvalidCustomerIdException();
        }

        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        // TODO false if we have problems to reach the DB
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
        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        Customer customer = ezCustomers.get(id);
        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        List<Customer> customerList = new LinkedList<Customer>(ezCustomers.values());
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
        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        // Template since we're not sure how to implements
        String cardCode = shopDB.insertCard(null, null);
        EZCard card = new EZCard(cardCode, null, null);
        ezCards.put(cardCode, card);


        // TODO get a string from the DB which isn't' related to a customer yet or generate one?
        //      https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/ how to generate one
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

        //verify if it's string with 10 digits!
        if (customerCard == null || isValidCard(customerCard)){
            throw new InvalidCustomerCardException();
        }

        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        Customer c = getCustomer(customerId); // This functions checks if the customers map contains the ID.
        if (c==null){
            return false;
        }
        for (Customer customer : ezCustomers.values()) {
            if (customer.getCustomerCard().equals(customerCard)){
                return false; //There is a customer with the given card
            }
        }

        c.setCustomerCard(customerCard);
        return true;
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

        if (customerCard == null || isValidCard(customerCard)){
            throw new InvalidCustomerCardException();
        }

        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();

        for (Customer customer : ezCustomers.values()) {
            if (customer.getCustomerCard().equals(customerCard)){
                if ((pointsToBeAdded > 0) || (pointsToBeAdded < 0 && customer.getPoints() > Math.abs(pointsToBeAdded))){
                    // If i need to remove 50 points (pointsToBeAdded = -50), i must have points > abs(50).
                    int p = customer.getPoints();
                    customer.setPoints(pointsToBeAdded+p);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method starts a new sale transaction and returns its unique identifier.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return the id of the transaction (greater than or equal to 0)
     */

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        Integer nextTicketNumber;
        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();

        if (ezSaleTransactions.isEmpty()) {
            nextTicketNumber = 0;
        }
        else {
            nextTicketNumber = Collections.max(this.ezSaleTransactions.keySet()) + 1;
        }
        tmpSaleTransaction = new EZSaleTransaction(nextTicketNumber);
        return nextTicketNumber;
    }

    /**
     * This method adds a product to a sale transaction decreasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode the barcode of the product to be added
     * @param amount the quantity of product to be added
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the quantity of product cannot satisfy the request,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidQuantityException if the quantity is less than 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        EZTicketEntry ticketEntryToAdd;
        ProductType scannedProduct;
        EZSaleTransaction currentSaleTransaction;
        boolean result = false;

        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();
        if (amount < 0)
            throw new InvalidQuantityException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        try {
            scannedProduct = this.getProductTypeByBarCode(productCode); // TODO check if a Cashier could call this method (in that case, getProductTypeByBarCode cannot be used
            if (this.updateQuantity(scannedProduct.getId(), -amount)) { // true if the requested amount of product is available
             // TODO manage case where this method returns false (product not existing, quantity would become negative
                currentSaleTransaction = (EZSaleTransaction) this.getSaleTransaction(transactionId); // TODO the SaleTransaction should be the temporary one: modify getSaleTransaction accordingly
                if (currentSaleTransaction != null && currentSaleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened)) {
                    ticketEntryToAdd = new EZTicketEntry(productCode, scannedProduct.getProductDescription(), amount, scannedProduct.getPricePerUnit(), 0);
                    currentSaleTransaction.getEntries().add(ticketEntryToAdd);
                    currentSaleTransaction.setPrice(currentSaleTransaction.getPrice() + scannedProduct.getPricePerUnit()*amount); // update total price
                    result = true;
                }
            }
        }
        catch (InvalidProductIdException e) { // the method returns false (does not modify result)
        }
        return result;
    }

    /**
     * This method deletes a product from a sale transaction increasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode the barcode of the product to be deleted
     * @param amount the quantity of product to be deleted
     *
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the quantity of product cannot satisfy the request,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidQuantityException if the quantity is less than 0
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        ProductType productToRemove;
        TicketEntry ticketToUpdate;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();
        if (amount < 0) // TODO should amount == 0 raise an exception?
            throw new InvalidQuantityException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        try {
            saleTransaction = (EZSaleTransaction) this.getSaleTransaction(transactionId);
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
        catch (InvalidProductIdException e) { // if the Product Code is correct, the Product Id is correct too
        }
        return result;
    }

    /**
     * This method applies a discount rate to all units of a product type with given type in a sale transaction. The
     * discount rate should be greater than or equal to 0 and less than 1.
     * The sale transaction should be started and open.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param productCode the barcode of the product to be discounted
     * @param discountRate the discount rate of the product
     *
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidProductCodeException if the product code is empty, null or invalid
     * @throws InvalidDiscountRateException if the discount rate is less than 0 or if it greater than or equal to 1.00
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        TicketEntry ticketToUpdate;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();
        if (discountRate < 0 || discountRate >= 1)
            throw new InvalidDiscountRateException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        this.getProductTypeByBarCode(productCode); //used to check if product code is valid (otherwise, InvalidProductCodeException is raised)
        saleTransaction = (EZSaleTransaction) this.getSaleTransactionById(transactionId);
        if (saleTransaction != null) {
            ticketToUpdate = saleTransaction.getEntries().stream().filter(product -> product.getBarCode().equals(productCode))
                    .findFirst().orElse(null);
            if (ticketToUpdate != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened)) {
                ticketToUpdate.setDiscountRate(discountRate);
                result = true;
            }
        }
        return result;
    }

    /**
     * This method applies a discount rate to the whole sale transaction.
     * The discount rate should be greater than or equal to 0 and less than 1.
     * The sale transaction can be either started or closed but not already payed.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param discountRate the discount rate of the sale
     *
     * @return  true if the operation is successful
     *          false if the transaction does not exists
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidDiscountRateException if the discount rate is less than 0 or if it greater than or equal to 1.00
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();
        if (discountRate < 0 || discountRate >= 1)
            throw new InvalidDiscountRateException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = (EZSaleTransaction) this.getSaleTransactionById(transactionId);
        // TODO Manage case where saleTransaction is closed (implies DB update)
        if (saleTransaction != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened, EZSaleTransaction.STClosed)) {
            saleTransaction.setDiscountRate(discountRate);
            result = true;
        }
        return result;
    }

    /**
     * This method returns the number of points granted by a specific sale transaction.
     * Every 10€ the number of points is increased by 1 (i.e. 19.99€ returns 1 point, 20.00€ returns 2 points).
     * If the transaction with given id does not exist then the number of points returned should be -1.
     * The transaction may be in any state (open, closed, payed).
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     *
     * @return the points of the sale (1 point for each 10€) or -1 if the transaction does not exists
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        int pointsToAdd = -1;
        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = (EZSaleTransaction) this.getSaleTransactionById(transactionId);
        if (saleTransaction != null) {
            pointsToAdd = (int) Math.floor(saleTransaction.getPrice()/10);
        }
        return pointsToAdd;
    }

    /**
     * This method closes an opened transaction. After this operation the
     * transaction is persisted in the system's memory.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     *
     * @return  true    if the transaction was successfully closed
     *          false   if the transaction does not exist,
     *                  if it has already been closed,
     *                  if there was a problem in registering the data
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    // TODO where is the Opened SaleTransaction (in the DB or not) ????
    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = (EZSaleTransaction) this.getSaleTransactionById(transactionId);
        if (saleTransaction != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STOpened)) {
            saleTransaction.setStatus(EZSaleTransaction.STClosed);
            if (this.shopDB.insertSaleTransaction(saleTransaction.getEntries(), saleTransaction.getDiscountRate(), saleTransaction.getPrice()) != null) {
                this.ezSaleTransactions.put(saleTransaction.getTicketNumber(), saleTransaction);
                result = true;
            }
        }
        return result;
    }

    /**
     * This method deletes a sale transaction with given unique identifier from the system's data store.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the number of the transaction to be deleted
     *
     * @return  true if the transaction has been successfully deleted,
     *          false   if the transaction doesn't exist,
     *                  if it has been payed,
     *                  if there are some problems with the db
     *
     * @throws InvalidTransactionIdException if the transaction id number is less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        EZSaleTransaction saleTransaction;
        boolean result = false;
        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();
        if (saleNumber == null || saleNumber <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = (EZSaleTransaction) this.getSaleTransactionById(saleNumber);
        if (saleTransaction != null && saleTransaction.hasRequiredStatus(EZSaleTransaction.STClosed)){
            if (this.shopDB.deleteSaleTransaction(saleNumber)){ // try to remove the SaleTransaction from the DB
                this.ezSaleTransactions.remove(saleNumber); // delete the SaleTransaction in the local collection
                result = true;
            }
        }
        return result;
    }

    /**
     * This method returns  a closed sale transaction.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the CLOSED Sale transaction
     *
     * @return the transaction if it is available (transaction closed), null otherwise
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */
    // TODO modify all methods where an opened saleTransaction is obtained by calling this method (it's an error)
    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        EZSaleTransaction saleTransaction = null;
        if (this.currUser == null || !this.currUser.hasRequiredRole("Administrator", "ShopManager", "Cashier"))
            throw new UnauthorizedException();
        if (transactionId == null || transactionId <= 0)
            throw new InvalidTransactionIdException();
        saleTransaction = this.shopDB.selectAllSaleTransactions().get(transactionId); // TODO should I use getSaleTransactionById instead?
        return saleTransaction;
    }

    public BalanceOperation getBalanceOpById(Integer balanceId) {
        return ezBalanceOperations.get(balanceId);
    }

    public EZSaleTransaction getSaleTransactionById(Integer saleNumber) {
        return  ezSaleTransactions.get(saleNumber);
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {

        BalanceOperation op = getBalanceOpById(saleNumber); //or SaleTransaction?

        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(saleNumber == null || saleNumber <= 0) throw new InvalidTransactionIdException();

        //EZReturnTransaction retTr = new EZReturnTransaction(saleNumber, op.getDate(), ); // ???
        //returnTransactions.put(saleNumber, retTr);
        //TODO: Start a return transaction (just start it), related to a specific Sale Transaction and save it in the list of
        // returned transactions and also in the list of Balance operations !?

        return saleNumber;
        // or return -1 if transaction with ID saleNumber doesn't exist!!!
    }

    public EZReturnTransaction getReturnTransactionById(Integer returnId) {
        return ezReturnTransactions.get(returnId);
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId == null || returnId <= 0) throw new InvalidTransactionIdException();

        //todo: isValidBarCode() still missing/// if(productCode.length() == 0 || productCode == null || !isValidBarCode(productCode)) throw new InvalidProductCodeException();

        if(amount <= 0) throw new InvalidQuantityException();

        EZReturnTransaction retTr = getReturnTransactionById(returnId);
        ProductType product = getProductTypeByBarCode(productCode);

        if(product == null)
            return false;
        //     if( product is not in the transaction (not the returnTransaction) ) return false;  //     HOW ???
        //     if( product amount in the transaction is lower than 'amount' ) return false;  //     HOW ???
        //     if( the transaction does not exist ) return false;  //     HOW ???

        if(amount <= product.getQuantity())
        {
            retTr.setReturnedProduct(product);
            retTr.setQuantity(amount);
        }




        return true;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId == null || returnId <= 0) throw new InvalidTransactionIdException();

        EZReturnTransaction retTr = getReturnTransactionById(returnId);

        if(retTr == null || retTr.isClosed())
            return false;

        if(commit) {
            //TODO: update DB

            //if(problems with DB) return false;
        }
        else
        {
            //rollback ---?---> deleteReturnTransaction? (only CLOSED return transaction can be deleted)  ???
        }

        return true;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId == null || returnId <= 0) throw new InvalidTransactionIdException();

        EZReturnTransaction retTr = getReturnTransactionById(returnId);

        if(retTr == null) return false;

        // if(retTr.isPayed()) return false;   -OR-   // if(retTr.getStatus().equals("PAYED")) return false;

        // todo: delete return transaction with ID == returnId from DB
        /* "This method deletes a closed return transaction. It affects the quantity of product sold in the connected sale transaction
         * (and consequently its price) and the quantity of product available on the shelves." */

        // if(problems with DB) return false;

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
        SaleTransaction sale = getSaleTransaction(ticketNumber);

        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(ticketNumber == null || ticketNumber <= 0) throw new InvalidTransactionIdException();

        if(cash <= 0) throw new InvalidPaymentException();

        if(sale == null || cash < sale.getPrice())
            return -1; // TODO: + RITORNA -1 ANCHE SE HAI AVUTO PROBLEMI DI CONNESSIONE AL DB

        recordBalanceUpdate(sale.getPrice());

        return (cash - sale.getPrice());
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        SaleTransaction sale = getSaleTransaction(ticketNumber);

        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(ticketNumber == null || ticketNumber <= 0) throw new InvalidTransactionIdException();

        if(!verifyByLuhnAlgo(creditCard) || creditCard.equals("") || creditCard == null) throw new InvalidCreditCardException();

        /*if(sale == null ||
            getCreditCardFromDB(ticketNumber) == null ||
            !verifyCreditCardBalance(ticketNumber, creditCard))
            return false;*/ // TODO: + RITORNA false ANCHE SE HAI AVUTO PROBLEMI DI CONNESSIONE AL DB

        // todo: keep money from credit card
        return recordBalanceUpdate(sale.getPrice());
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId <= 0) throw new InvalidTransactionIdException(); // not "returnId == null ||" ???

        EZReturnTransaction retTr = getReturnTransactionById(returnId);

        if(!retTr.isClosed()) return -1;

        if(retTr == null) return -1;

        // TODO: + RITORNA -1 ANCHE SE HAI AVUTO PROBLEMI DI CONNESSIONE AL DB

        double returnedMoney = retTr.getMoney();

        recordBalanceUpdate(-returnedMoney);

        return returnedMoney;

    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager, URCashier) )
            throw new UnauthorizedException();

        if(returnId <= 0) throw new InvalidTransactionIdException(); // not "returnId == null ||" ???

        if(!verifyByLuhnAlgo(creditCard) || creditCard.equals("") || creditCard == null) throw new InvalidCreditCardException();

        EZReturnTransaction retTr = getReturnTransactionById(returnId);

        if(!retTr.isClosed()) return -1;
        if(retTr == null) return -1;
        //if(getCreditCardFromDB(ticketNumber) == null) return -1;
        // TODO: + RITORNA -1 ANCHE SE HAI AVUTO PROBLEMI DI CONNESSIONE AL DB

        double returnedMoney = retTr.getMoney();

        recordBalanceUpdate(-returnedMoney);
        // todo: give money to credit card

        return returnedMoney;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager) )
            throw new UnauthorizedException();

        return accountingBook.updateBalance(toBeAdded);
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        LinkedList<BalanceOperation> balanceOperations = new LinkedList<>(ezBalanceOperations.values());
        List<BalanceOperation> filteredBalanceOperations = new LinkedList<>();

        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager) )
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
        if( this.currUser != null && !this.currUser.hasRequiredRole(URAdministrator, URShopManager) )
            throw new UnauthorizedException();

        if (accountingBook != null)
            return accountingBook.currentBalance;

        return 0;
    }

    private void testDB() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException {
        // TODO: remove all this stuff before delivery

        this.createUser("christian", "pwd", "Administrator");

        EZCustomer c1 = new EZCustomer(-1, "Pippo", "XYZ123", 512);
        Integer idC1 = shopDB.insertCustomer(c1.getCustomerName(), c1.getCustomerCard(), c1.getPoints());
        c1.setId(idC1);

        Integer c2 = shopDB.insertCustomer("Pluto", "", 0);
        Integer c3 = shopDB.insertCustomer("Paperino", "XYZ456", 260);

        String card1 = shopDB.insertCard(c1.getId(), c1.getPoints());
        System.out.println("Questa è la carta 1: " + card1);
        String card2 = shopDB.insertCard(c1.getId()+1, c1.getPoints() + 30);
        System.out.println("Questa è la carta 2: " + card2);
        String card3 = shopDB.insertCard(c1.getId()+2, c1.getPoints() + 50);
        System.out.println("Questa è la carta 3: " + card3);
        shopDB.deleteCard(card2);
        System.out.println("Rimossa carta " + card2);
        shopDB.createSaleTransactionsTable();
        shopDB.createProductsPerSaleTable();

        EZUser newUser = new EZUser(-1, "admin", "admin", "Cashier");
        newUser.setId(shopDB.insertUser("admin", "admin", "Administrator"));
        this.ezUsers.put(newUser.getId(), newUser);
    }
}
