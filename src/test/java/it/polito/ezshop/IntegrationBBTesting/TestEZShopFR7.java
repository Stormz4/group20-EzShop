package it.polito.ezshop.IntegrationBBTesting;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static it.polito.ezshop.data.EZBalanceOperation.Credit;
import static it.polito.ezshop.data.EZBalanceOperation.Debit;
import static it.polito.ezshop.data.EZOrder.*;
import static it.polito.ezshop.data.EZOrder.OSCompleted;
import static it.polito.ezshop.data.EZUser.*;
import static it.polito.ezshop.data.EZUser.URCashier;
import static org.junit.Assert.*;

public class TestEZShopFR7 {

    EZShop ez;
    Integer saleTransactionID, returnTransactionID;
    String barCode = "1234567890128";
    private SQLiteDB shopDB;
    private static final int defaultID = -1;
    private static final int defaultValue = 0;

    public void populateDB() {
        this.shopDB.insertUser("admin", "admin", URAdministrator);
        this.shopDB.insertUser("sheldon", "pwd", URAdministrator);
        this.shopDB.insertUser("manager", "manager", URShopManager);
        this.shopDB.insertUser("aldo", "pwd", URShopManager);
        this.shopDB.insertUser("cashier", "cashier", URCashier);
        this.shopDB.insertUser("giovanni", "pwd", URCashier);
        this.shopDB.insertUser("giacomo", "pwd", URCashier);
        this.shopDB.insertUser("testUser00", "pwd", URAdministrator);

        String card1 = this.shopDB.insertCard(1200);
        String card2 = this.shopDB.insertCard(defaultID);
        this.shopDB.insertCustomer("Leonard", card1);
        this.shopDB.insertCustomer("Penny", card2);
        this.shopDB.insertCustomer("Raj", null);
        this.shopDB.insertCustomer("Cheerios", null);


        EZProductType prod1 = new EZProductType(defaultID, 5, "", "A simple note",
                "First product", "4627828478338", 12.50);
        prod1.setId( this.shopDB.insertProductType(prod1.getQuantity(), prod1.getLocation(), prod1.getNote(),
                prod1.getProductDescription(), prod1.getBarCode(), prod1.getPricePerUnit()) );

        EZProductType prod2 = new EZProductType(defaultID, 5, "", "A simple note",
                "First product", "4838283913450", 12.50);
        prod2.setId( this.shopDB.insertProductType(prod2.getQuantity(), prod2.getLocation(), prod2.getNote(),
                prod2.getProductDescription(), prod2.getBarCode(), prod2.getPricePerUnit()) );

        this.shopDB.insertOrder(defaultID, prod2.getBarCode(), prod2.getPricePerUnit(), prod2.getQuantity(), OSPayed);

        EZProductType prodA = new EZProductType(defaultID, 30, "", "A simple note",
                "prod A", "1345334543427", 12.60);
        EZProductType prodB = new EZProductType(defaultID, 30, "", "A simple note",
                "prod B", "4532344529689", 3.50);
        EZProductType prodC = new EZProductType(defaultID, 30, "", "A simple note",
                "prod C", "5839274928315", 56.70);
        EZProductType prodD = new EZProductType(defaultID, 30, "Sweden", "By PDX",
                "Vic3", "1234567890128", 12.50);
        prodA.setId( this.shopDB.insertProductType(prodA.getQuantity(), prodA.getLocation(), prodA.getNote(),
                prodA.getProductDescription(), prodA.getBarCode(), prodA.getPricePerUnit()) );
        prodB.setId( this.shopDB.insertProductType(prodB.getQuantity(), prodB.getLocation(), prodB.getNote(),
                prodB.getProductDescription(), prodB.getBarCode(), prodB.getPricePerUnit()) );
        prodC.setId( this.shopDB.insertProductType(prodC.getQuantity(), prodC.getLocation(), prodC.getNote(),
                prodC.getProductDescription(), prodC.getBarCode(), prodC.getPricePerUnit()) );
        prodD.setId( this.shopDB.insertProductType(prodD.getQuantity(), prodD.getLocation(), prodD.getNote(),
                prodD.getProductDescription(), prodD.getBarCode(), prodD.getPricePerUnit()) );
        List<TicketEntry> ticketList = new LinkedList<>();

        EZSaleTransaction sale1 = new EZSaleTransaction(defaultID, ticketList, defaultValue, defaultValue, EZSaleTransaction.STOpened);
        int tid = this.shopDB.insertSaleTransaction(ticketList, defaultValue, defaultValue, EZSaleTransaction.STOpened);
        sale1.setTicketNumber(tid);

        EZTicketEntry ticket1 = new EZTicketEntry("1345334543427", "prod A", 2, 12.60, 0);
        shopDB.insertProductPerSale("1345334543427", tid, 2, 0);

        EZTicketEntry ticket2 = new EZTicketEntry("4532344529689", "prod B", 6, 3.50, 0.1);
        shopDB.insertProductPerSale("4532344529689", tid, 6, 0.1);

        EZTicketEntry ticket3 = new EZTicketEntry("5839274928315", "prod C", 1, 56.70, 0.5);
        shopDB.insertProductPerSale("5839274928315", tid, 1, 0.5);
        ticketList.add((TicketEntry) ticket1);
        ticketList.add((TicketEntry) ticket2);
        ticketList.add((TicketEntry) ticket3);
        shopDB.updateSaleTransaction(tid, 0.6, 100, EZSaleTransaction.STClosed);
        sale1.setDiscountRate(0.6);
        sale1.setPrice(100);
        sale1.setStatus(EZSaleTransaction.STClosed);

        EZBalanceOperation bo1 = new EZBalanceOperation(defaultID, LocalDate.of(2019, 4, 22), 35, Debit);
        int id_bo1 = shopDB.insertBalanceOperation(LocalDate.of(2019, 4, 22), 35, Debit);
        bo1.setBalanceId(id_bo1);

        EZBalanceOperation bo2 = new EZBalanceOperation(defaultID, LocalDate.of(2020, 2, 13), 110, Credit);
        int id_bo2 = shopDB.insertBalanceOperation(LocalDate.of(2020, 2, 13), 35, Credit);
        bo1.setBalanceId(id_bo2);

        EZBalanceOperation bo3 = new EZBalanceOperation(defaultID, LocalDate.of(2021, 5, 11), 3500, Debit);
        int id_bo3 = shopDB.insertBalanceOperation(LocalDate.of(2021, 5, 11), 3500, Debit);
        bo1.setBalanceId(id_bo3);

        EZOrder o1 = new EZOrder(defaultID, id_bo1, "1345334543427", 12.60, 4, OSIssued);
        int id_o1 = shopDB.insertOrder(id_bo1, "1345334543427", 12.60, 4, OSIssued);
        o1.setOrderId(id_o1);

        EZOrder o3 = new EZOrder(defaultID, id_bo3, "5839274928315", 56.70, 6, OSCompleted);
        int id_o3 = shopDB.insertOrder(id_bo3, "5839274928315", 56.70, 6, OSCompleted);
        o1.setOrderId(id_o3);
    }

    @Before
    public void connectAndInit() {
        shopDB = new SQLiteDB();
        assertTrue(shopDB.connect());

        // Check connection
        assertTrue(shopDB.isConnected());

        // Init DB
        shopDB.initDatabase();

        // CLear every DB table
        boolean cleared = shopDB.clearTable(SQLiteDB.tBalanceOperations);
        cleared &= shopDB.clearTable(SQLiteDB.tCards);
        cleared &= shopDB.clearTable(SQLiteDB.tCustomers);
        cleared &= shopDB.clearTable(SQLiteDB.tOrders);
        cleared &= shopDB.clearTable(SQLiteDB.tProductTypes);
        cleared &= shopDB.clearTable(SQLiteDB.tProductsPerSale);
        cleared &= shopDB.clearTable(SQLiteDB.tTransactions);
        cleared &= shopDB.clearTable(SQLiteDB.tUsers);
        assertTrue(cleared);

        // Populate DB
        this.populateDB();

        ez = new EZShop();
    }
    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        ez = new EZShop();
        ez.login("testUser00", "pwd");
    }

    @After
    public void teardown() {
        this.shopDB.closeConnection();

        // Check closeConnection
        assertFalse(shopDB.isConnected());
    }

    /* All the following test methods need to define a saleTransaction or a returnTransaction first
     * So, every following test will include the first part (without payment) either Scenario 6.1 (if a saleTransaction is needed)
     * or Scenario 8.1 (if a returnTransaction is needed)
     */
    /**
     * This test method covers Scenario 6.1 + Scenario 7.1
     */
    @Test
    public void testReceiveCreditCardPayment() throws UnauthorizedException, InvalidProductCodeException, InvalidQuantityException, InvalidTransactionIdException, InvalidCreditCardException, InvalidProductIdException {
        String creditCard = "4485370086510891";

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle Payment
        assertTrue(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
        ez.logout();
        // Since CreditCard Payment System is fake (Credit cards are in a .txt file), we do not test whether the amount of money in the Credit Card has changed
    }

    /**
     * This test method covers Scenario 6.1 + Scenario 7.2
     */
    @Test
    public void testInvalidCardPayment() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidCreditCardException, InvalidProductIdException {
        String creditCard = "1261924021131027"; //valid according to Luhn's algo, but not registered

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle payment
        assertFalse(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
        ez.logout();
    }

    /**
     * This test method covers Scenario 6.1 + Scenario 7.3
     */
    @Test
    public void testInsufficientCreditPayment() throws InvalidCreditCardException, InvalidTransactionIdException, UnauthorizedException, InvalidQuantityException, InvalidProductCodeException, InvalidProductIdException {
        String creditCard = "5100293991053009"; //present in the CreditCard file, but it has insufficient credit

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle payment
        assertFalse(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
        ez.logout();
    }

    /**
     * This test method covers Scenario 6.1 + Scenario 7.4
     */
    @Test
    public void testReceiveCashPayment() throws InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidQuantityException, InvalidProductCodeException, InvalidProductIdException {

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle payment
        assertTrue(ez.receiveCashPayment(saleTransactionID, 50.0) > 0);
        ez.logout();
    }

    /** This test method checks whether error return values or exception are raised as expected
    */
    @Test
    public void testAllCasesReceive() throws UnauthorizedException, InvalidTransactionIdException, InvalidCreditCardException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException, InvalidProductIdException {
        String creditCard = "4485370086510891";

        // Test receiveCreditCardPayment

        // Create a dummy SaleTransaction and then delete it
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        ez.endSaleTransaction(saleTransactionID);
        Integer finalSaleTransactionID = saleTransactionID;
        assertThrows(InvalidCreditCardException.class, () -> ez.receiveCreditCardPayment(finalSaleTransactionID, null));
        ez.deleteSaleTransaction(saleTransactionID);

        // Try to pay using a Credit Card: it should not be possible
        assertFalse(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
        assertThrows(InvalidTransactionIdException.class, () -> ez.receiveCreditCardPayment(-1, creditCard));

        // Test receiveCashPayment

        // Create a dummy saleTransaction to pay for (cash)
        assertFalse(ez.receiveCashPayment(saleTransactionID, 25) != -1); // trying to pay previous deleted transaction
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle Payment by Cash
        assertTrue(ez.receiveCashPayment(saleTransactionID, 0.25) == -1); // Insufficient money
        Integer finalSaleTransactionID1 = saleTransactionID;
        assertThrows(InvalidTransactionIdException.class, () -> ez.receiveCashPayment(-1, 25)); // Invalid Transaction ID
        assertThrows(InvalidPaymentException.class, () -> ez.receiveCashPayment(finalSaleTransactionID1, -1)); // Negative cash

        // Test User authentication on both receiveCreditCardPayment and receiveCashPayment
        ez.logout();
        assertThrows(UnauthorizedException.class, () -> ez.receiveCashPayment(finalSaleTransactionID1, 10.0));
        assertThrows(UnauthorizedException.class, () -> ez.receiveCreditCardPayment(finalSaleTransactionID1, creditCard));
    }

    /** This test method covers Scenario 8.1 + Scenario 10.1
     *
     */
    @Test
    public void testReturnCreditCardPayment() throws UnauthorizedException, InvalidTransactionIdException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException, InvalidCreditCardException {
        String creditCard = "4485370086510891"; // Credit Card is registered
        // Create and complete saleTransaction for the following returnTransaction to test
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));
        assertTrue(ez.receiveCashPayment(saleTransactionID, 10000) >= 0);

        // Create returnTransaction and close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));
        assertTrue(ez.endReturnTransaction(returnTransactionID, true));

        // Handle Payment (it should work)
        assertTrue(ez.returnCreditCardPayment(returnTransactionID, creditCard) > 0);
        ez.logout();
    }

    /** This test method covers Scenario 8.1 + Scenario 10.2
     *
     */
    @Test
    public void testReturnCashPayment() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPaymentException {
        // Create and complete saleTransaction for the following returnTransaction to test
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));
        assertTrue(ez.receiveCashPayment(saleTransactionID, 10000) >= 0);

        // Create returnTransaction and close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));
        assertTrue(ez.endReturnTransaction(returnTransactionID, true));

        // Handle Payment (it should work)
        assertTrue(ez.returnCashPayment(returnTransactionID) > 0);
        ez.logout();
    }

    @Test
    public void testReturnInvalidCardPayment() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPaymentException, InvalidCreditCardException {
        String creditCard = "1261924021131027"; //valid according to Luhn's algo, but not registered
        // Create and complete saleTransaction for the following returnTransaction to test
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));
        assertTrue(ez.receiveCashPayment(saleTransactionID, 10000) >= 0);

        // Create returnTransaction and close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));
        assertTrue(ez.endReturnTransaction(returnTransactionID, true));

        // Handle Payment (it should not work)
        assertFalse(ez.returnCreditCardPayment(returnTransactionID, creditCard) != -1);
        ez.logout();
    }

    /** This test method checks whether error return values or exception are raised as expected
     */
    @Test
    public void testAllCasesReturn() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPaymentException, InvalidCreditCardException {
        String creditCard = "4485370086510891"; // Credit Card is registered
        // Create and complete saleTransaction for the following returnTransaction to test
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 2));
        assertTrue(ez.endSaleTransaction(saleTransactionID));
        assertTrue(ez.receiveCashPayment(saleTransactionID, 10000) >= 0);

        // Create returnTransaction but do not close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));

        // Test returnCreditCardPayment
        assertFalse(ez.returnCreditCardPayment(returnTransactionID, creditCard) != -1); // returnTransaction is not closed!
        assertTrue(ez.endReturnTransaction(returnTransactionID, true)); // close the returnTransaction
        assertTrue(ez.deleteReturnTransaction(returnTransactionID)); // delete the returnTransaction
        assertFalse(ez.returnCreditCardPayment(returnTransactionID, creditCard) != -1); // trying to pay a non existing Transaction
        assertThrows(InvalidTransactionIdException.class, () -> ez.returnCreditCardPayment(-1,creditCard));
        Integer finalReturnTransactionID = returnTransactionID;
        assertThrows(InvalidCreditCardException.class, () -> ez.returnCreditCardPayment(finalReturnTransactionID, null));

        // Recreate a returnTransaction but do not close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));

        // Test returnCashPayment
        assertFalse(ez.returnCashPayment(returnTransactionID) != -1); // the transaction is not closed
        assertTrue(ez.endReturnTransaction(returnTransactionID, true)); // close the returnTransaction
        assertTrue(ez.deleteReturnTransaction(returnTransactionID)); // delete the returnTransaction
        assertFalse(ez.returnCashPayment(returnTransactionID) != -1); // trying to return a non esisting transaction
        assertThrows(InvalidTransactionIdException.class, () -> ez.returnCashPayment(-1));

        // Test authentication for both returnCreditCardPayment and returnCashPayment
        ez.logout();
        Integer finalReturnTransactionID1 = returnTransactionID;
        assertThrows(UnauthorizedException.class, () -> ez.returnCreditCardPayment(finalReturnTransactionID1, creditCard));
        assertThrows(UnauthorizedException.class, () -> ez.returnCashPayment(finalReturnTransactionID1));
        returnTransactionID = null;
    }

}
