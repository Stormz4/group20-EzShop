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
import static org.junit.Assert.*;

public class TestEZShopFR8 {

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
        int id_bo2 = shopDB.insertBalanceOperation(LocalDate.of(2020, 2, 13), 110, Credit);
        bo1.setBalanceId(id_bo2);

        EZBalanceOperation bo3 = new EZBalanceOperation(defaultID, LocalDate.of(2021, 5, 11), 3500, Credit);
        int id_bo3 = shopDB.insertBalanceOperation(LocalDate.of(2021, 5, 11), 3500, Credit);
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
    }

    @After
    public void teardown() {
        this.shopDB.closeConnection();

        // Check closeConnection
        assertFalse(shopDB.isConnected());
    }

    /** This test method follows Scenario 9.2
     *
     */
    @Test
    public void testRecordDebit() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        double toBeAdded = -2;

        ez.login("testUser00", "pwd");
        // The DB has an initial balance > 2, so this call should return true
        assertTrue(ez.recordBalanceUpdate(toBeAdded));


    }

    /** This test method follows Scenario 9.3
     *
     */
    @Test
    public void testRecordCredit() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        double toBeAdded = 2;

        ez.login("testUser00", "pwd");
        // A Credit should never return false as we are increasing the total balance
        assertTrue(ez.recordBalanceUpdate(toBeAdded));
    }

    /** This test method follows Scenario 9.1
     *
     */
    @Test
    public void testGetAllCreditsAndDebits() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        List<BalanceOperation> allCreditsAndDebits;

        ez.login("testUser00", "pwd");
        // we only need to test if balanceOperation are returned: we decide to collect the whole accountBook
        allCreditsAndDebits = ez.getCreditsAndDebits(null, null);
        // the predefined DB has three balanceOperation, we want to observe if there are all of them
        assertEquals(3, allCreditsAndDebits.size());
    }

    /** This test method follows Scenario 9.4
     *
     */
    @Test
    public void testComputeBalance() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException {

        ez.login("testUser00", "pwd");
        // the initial sum of the balanceOperation in the predefined DB is 3575
        assertEquals(3575, ez.computeBalance(), 0.1);
    }

    /** This test method covers all other cases for the following API methods:
     *  recordBalanceUpdate, getAllCreditsAndDebits, computeBalance
     */
    @Test
    public void testAllCases() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        assertThrows(UnauthorizedException.class, () -> ez.recordBalanceUpdate(45000));
        assertThrows(UnauthorizedException.class, () -> ez.recordBalanceUpdate(-1));
        assertThrows(UnauthorizedException.class, () -> ez.getCreditsAndDebits(null, null));
        assertThrows(UnauthorizedException.class, () -> ez.computeBalance());

        // login with a Cashier, recordBalanceUpdate should not work
        ez.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> ez.recordBalanceUpdate(45));
        assertNotNull(ez.getCreditsAndDebits(null, null));
        assertEquals(3575, ez.computeBalance(), 0.1);


        // login with a ShopManager, all methods should work
        ez.login("aldo", "pwd");
        assertNotNull(ez.getCreditsAndDebits(null, null));
        assertEquals(3575, ez.computeBalance(), 0.1);
        assertTrue(ez.recordBalanceUpdate(50));
        ez.logout();
    }
    

}
