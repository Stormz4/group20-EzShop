package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.lang.StringBuilder;
import java.lang.Math;


public class EZShop implements EZShopInterface {

    EZCustomer c1 = new EZCustomer(1, "Ciccio", "00001", 0);
    User currUser = null;
    // populate customers

    HashMap<Integer, Customer> customers = new HashMap<>();
    HashMap<Integer, User> users = new HashMap<>();

    // TODO verify is this map is needed
    HashMap<Integer, String> loyaltyCards = new HashMap<>();

    EZAccountBook accountingBook = new EZAccountBook(0);
    List<BalanceOperation> allBalanceOperations = new LinkedList<>();

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

        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return -1;
            }
        }
        // TODO return -1 is there is an error while saving the user
        // Get the highest ID from the DB
        int maxKey = Collections.max(users.keySet());
        Integer id = maxKey+1;
        EZUser user = new EZUser(id, username, password, role);

        users.put(id, user);

        return id;
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if (!users.containsKey(id)) {
            return false;
        }
        if (id == null || id <=0) {
            throw new InvalidUserIdException();
        }

        if(!currUser.getRole().equals("Administrator") || currUser == null){
            throw new UnauthorizedException();
        }

        users.remove(id);

        // TODO delete from DB
        return true;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        if(!currUser.getRole().equals("Administrator") || currUser == null){
            throw new UnauthorizedException();
        }

        List<User> usersList = new LinkedList<>(users.values());
        return usersList;
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if (!users.containsKey(id)) {
            return null;
        }
        if (id == null || id <=0) {
            throw new InvalidUserIdException();
        }

        if(!currUser.getRole().equals("Administrator") || currUser == null){
            throw new UnauthorizedException();
        }

        User user = users.get(id);
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
        if (!users.containsKey(id)) {
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

        User user = users.get(id);
        user.setRole(role);
        users.replace(id, user);
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
        for (User user : users.values()) {
            if (user.getPassword().equals(password) && user.getUsername().equals(username)){
                currUser = user;
            }
        }

        // TODO return null if DB problems?
        return null;
    }

    @Override
    public boolean logout() {
        if (currUser == null){
            return false;
        }
        currUser = null;
        return true;
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        return false;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        List<ProductType> productTypes = new LinkedList<>();
        Integer id = 1;
        Integer quantity = 20;
        String location = "C";
        String note = "Nota";
        String productDescription = "Description";
        String barCode = "XYZ123";
        double pricePerUnit = 12.50;
        EZProductType myProdType = new EZProductType(id, quantity, location, note, productDescription, barCode, pricePerUnit);
        productTypes.add(myProdType);
        
        return productTypes;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        return null;
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        return null;
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        return false;
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

        for (Customer customer : customers.values()) {
            if (customer.getCustomerName().equals(customerName)) {
                // Name should be unique
                return -1;
            }
        }

        // Name is not present in the DB
        // Get the highest ID from the DB
        int maxKey = Collections.max(customers.keySet());
        Integer id = maxKey+1;
        EZCustomer customer = new EZCustomer(id, customerName, null, null);

        // TODO insert in the DB
        customers.put(id, customer);

        return id;
    }


    public boolean checkLoyalty(String card){
        if (card.length() != 10 || card.matches("[0-9]{10}")){
            return true;
        }
        return false;
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

        if (newCustomerName == null || newCustomerName.isEmpty() ){
            throw new InvalidCustomerNameException();
        }
        if (newCustomerCard == null || checkLoyalty(newCustomerCard) ){
            throw new InvalidCustomerCardException();
        }
        if(currUser==null || !currUser.getRole().equals("Administrator") || !currUser.getRole().equals("Cashier") || !currUser.getRole().equals("ShopManager")){
                throw new UnauthorizedException();

        }
        Customer customer = customers.get(id);
        if (newCustomerCard.isEmpty()){
            //if it is an empty string then any
            // existing card code connected to the
            // customer will be removed. Remove also from the DB?
            customer.setCustomerCard(null);
            customer.setPoints(0);
        }
        customer.setCustomerName(newCustomerName);
        customer.setCustomerCard(newCustomerCard);
        customers.replace(id, customer);
        return true;

    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if (!customers.containsKey(id)) {
            return false;
        }
        if (id == null || id <=0 ) {
            throw new InvalidCustomerIdException();
        }

        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        // TODO false if we have problems to reach the DB
        users.remove(id);
        return true;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if (!customers.containsKey(id)) {
            return null;
        }
        if ( id==null || id <=0) {
            throw new InvalidCustomerIdException();
        }
        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        Customer customer = customers.get(id);
        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        List<Customer> customerList = new LinkedList<Customer>(customers.values());
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
        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        // Template since we're not sure how to implements
        String s = "";

        loyaltyCards.put(1, s);


        // TODO get a string from the DB which isn't' related to a customer yet or generate one?
        //      https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/ how to generate one
        return s;
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
        if (customerCard == null || checkLoyalty(customerCard)){
            throw new InvalidCustomerCardException();
        }

        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        Customer c = getCustomer(customerId); // This functions checks if the customers map contains the ID.
        if (c==null){
            return false;
        }
        for (Customer customer : customers.values()) {
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

        if (customerCard == null || checkLoyalty(customerCard)){
            throw new InvalidCustomerCardException();
        }

        if(currUser==null || !(currUser.getRole().equals("Administrator") || currUser.getRole().equals("Cashier") || currUser.getRole().equals("ShopManager"))){
            throw new UnauthorizedException();
        }

        for (Customer customer : customers.values()) {
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

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    static boolean verifyUserRights(User currUser, UserRoleEnum requestedLevel)
    {
        switch (requestedLevel)
        {
            /*
                Cashier             privilege level: 1      Access to sale level functions only
                ShopManager         privilege level: 2      Access to sale and higher level functions
                Administrator       privilege level: 3      All the rights
            */
            case Cashier:
                return currUser != null &&
                        (currUser.getRole().equals("Administrator") ||
                                currUser.getRole().equals("ShopManager") ||
                                currUser.getRole().equals("Cashier"));
            case ShopManager:
                return currUser != null &&
                        (currUser.getRole().equals("Administrator") ||
                                currUser.getRole().equals("ShopManager"));
            case Administrator:
                return currUser != null &&
                        currUser.getRole().equals("Administrator");
            default:
                System.out.println("Privileges verification error.");
                return false;
        }
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

        if(!verifyUserRights(currUser, Cashier)) throw new UnauthorizedException();

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

        if(!verifyUserRights(currUser, Cashier)) throw new UnauthorizedException();

        if(ticketNumber == null || ticketNumber <= 0) throw new InvalidTransactionIdException();

        if(!verifyByLuhnAlgo(creditCard)) throw new InvalidCreditCardException();

        if(sale == null ||
            getCreditCardFromDB(ticketNumber) == null ||
            !verifyCreditCardBalance(ticketNumber, creditCard))
            return false; // TODO: + RITORNA false ANCHE SE HAI AVUTO PROBLEMI DI CONNESSIONE AL DB

        return recordBalanceUpdate(sale.getPrice());
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {

        if(!verifyUserRights(currUser, ShopManager)) throw new UnauthorizedException();

        return accountingBook.updateBalance(toBeAdded);
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {

        LocalDate tmp = from;
        List<BalanceOperation> filteredBalanceOperations;

        if(!verifyUserRights(currUser, ShopManager)) throw new UnauthorizedException(); // IT IS NOT SPECIFIED IN API!!!

        if(from.isAfter(to))
        {   // swap the dates:
            from = to;
            to = tmp;
        }

        filteredBalanceOperations = allBalanceOperations.stream().filter(); //todo: filter per dates (remember null values)


        return filteredBalanceOperations;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {

        if(!verifyUserRights(currUser, ShopManager)) throw new UnauthorizedException(); // IT IS NOT SPECIFIED IN API!!!

        return accountingBook.currentBalance;
    }
}
