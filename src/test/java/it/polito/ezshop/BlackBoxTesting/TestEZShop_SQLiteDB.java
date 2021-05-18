package it.polito.ezshop.BlackBoxTesting;

import static it.polito.ezshop.data.EZBalanceOperation.Credit;
import static it.polito.ezshop.data.EZBalanceOperation.Debit;
import static it.polito.ezshop.data.EZOrder.*;
import static it.polito.ezshop.data.EZOrder.OSCompleted;
import static it.polito.ezshop.data.EZUser.*;
import static it.polito.ezshop.data.EZUser.URCashier;
import static org.junit.Assert.*;

import it.polito.ezshop.data.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TestEZShop_SQLiteDB {
    private SQLiteDB shopDB;
    private HashMap<String, Integer> ezCards = new HashMap<>();
    private HashMap<Integer, EZUser> ezUsers = new HashMap<>();
    private HashMap<Integer, EZOrder> ezOrders = new HashMap<>();
    private HashMap<Integer, EZCustomer> ezCustomers = new HashMap<>();
    private HashMap<Integer, EZProductType> ezProducts = new HashMap<>();
    private HashMap<Integer, EZSaleTransaction> ezSaleTransactions = new HashMap<>();
    private HashMap<Integer, EZBalanceOperation> ezBalanceOperations = new HashMap<>();
    private HashMap<Integer, EZReturnTransaction> ezReturnTransactions = new HashMap<>();
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

        String card1 = this.shopDB.insertCard(1200);
        String card2 = this.shopDB.insertCard(defaultValue);
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
        prodA.setId( this.shopDB.insertProductType(prodA.getQuantity(), prodA.getLocation(), prodA.getNote(),
                prodA.getProductDescription(), prodA.getBarCode(), prodA.getPricePerUnit()) );
        prodB.setId( this.shopDB.insertProductType(prodB.getQuantity(), prodB.getLocation(), prodB.getNote(),
                prodB.getProductDescription(), prodB.getBarCode(), prodB.getPricePerUnit()) );
        prodC.setId( this.shopDB.insertProductType(prodC.getQuantity(), prodC.getLocation(), prodC.getNote(),
                prodC.getProductDescription(), prodC.getBarCode(), prodC.getPricePerUnit()) );
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
        ezSaleTransactions.put(tid, sale1);

        EZBalanceOperation bo1 = new EZBalanceOperation(defaultID, LocalDate.of(2019, 4, 22), 35, Debit);
        int id_bo1 = shopDB.insertBalanceOperation(LocalDate.of(2019, 4, 22), 35, Debit);
        bo1.setBalanceId(id_bo1);
        ezBalanceOperations.put(id_bo1, bo1);

        EZBalanceOperation bo2 = new EZBalanceOperation(defaultID, LocalDate.of(2020, 2, 13), 110, Credit);
        int id_bo2 = shopDB.insertBalanceOperation(LocalDate.of(2020, 2, 13), 35, Credit);
        bo1.setBalanceId(id_bo2);
        ezBalanceOperations.put(id_bo2, bo2);

        EZBalanceOperation bo3 = new EZBalanceOperation(defaultID, LocalDate.of(2021, 5, 11), 3500, Debit);
        int id_bo3 = shopDB.insertBalanceOperation(LocalDate.of(2021, 5, 11), 3500, Debit);
        bo1.setBalanceId(id_bo3);
        ezBalanceOperations.put(id_bo3, bo3);

        EZOrder o1 = new EZOrder(defaultID, id_bo1, "1345334543427", 12.60, 4, OSIssued);
        int id_o1 = shopDB.insertOrder(id_bo1, "1345334543427", 12.60, 4, OSIssued);
        o1.setOrderId(id_o1);
        ezOrders.put(id_o1, o1);

        EZOrder o3 = new EZOrder(defaultID, id_bo3, "5839274928315", 56.70, 6, OSCompleted);
        int id_o3 = shopDB.insertOrder(id_bo3, "5839274928315", 56.70, 6, OSCompleted);
        o1.setOrderId(id_o3);
        ezOrders.put(id_o3, o3);
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
    }

    @After
    public void closeConnection() {
        shopDB.closeConnection();

        // Check closeConnection
        assertFalse(shopDB.isConnected());

        shopDB = null;
    }

    @Test
    public void testSelectAllQueries() {

        if (ezBalanceOperations == null)
            ezBalanceOperations = shopDB.selectAllBalanceOperations();
        assertNotNull(ezBalanceOperations);

        if (ezCards == null)
            ezCards = shopDB.selectAllCards();
        assertNotNull(ezCards);

        if (ezCustomers == null)
            ezCustomers = shopDB.selectAllCustomers();
        assertNotNull(ezCustomers);

        if (ezOrders == null)
            ezOrders = shopDB.selectAllOrders();
        assertNotNull(ezOrders);

        if (ezProducts == null)
            ezProducts = shopDB.selectAllProductTypes();
        assertNotNull(ezProducts);

        if (ezSaleTransactions == null)
            ezSaleTransactions = shopDB.selectAllSaleTransactions();
        assertNotNull(ezSaleTransactions);

        if (ezUsers == null)
            ezUsers = shopDB.selectAllUsers();
        assertNotNull(ezUsers);

        if (ezReturnTransactions == null)
            ezReturnTransactions = shopDB.selectAllReturnTransactions();
        assertNotNull(ezReturnTransactions);
    }

    @Test
    public void testClearDatabase() {
        // Check if clearDatabase is performed without issues
        assertTrue(shopDB.clearDatabase());

        assertEquals(0, shopDB.selectAllBalanceOperations().size());
        assertEquals(0, shopDB.selectAllOrders().size());
        assertEquals(0, shopDB.selectAllProductTypes().size());
        assertEquals(0, shopDB.selectAllSaleTransactions().size());
        assertEquals(0, shopDB.selectAllReturnTransactions().size());
    }

    @Test
    public void testInsertDeleteCustomer() {
        String cName = "Johnny";
        String cCustomerCard = "0364829165";
        int cPoints = 185;

        EZCustomer customer = new EZCustomer(defaultID, cName, cCustomerCard, cPoints);
        int cID = shopDB.insertCustomer(cName, cCustomerCard);

        // Check if insertion was successful
        assertNotEquals(cID, defaultID);

        // Check if prevents insertion of another customer with same card
        int cstID = shopDB.insertCustomer(cName, cCustomerCard);
        assertEquals(cstID, defaultID);

        // Check deleteCustomer
        assertTrue(shopDB.deleteCustomer(cID));
    }

    @Test
    public void testUserDB(){
        int uID = shopDB.insertUser("Rosario", "testpwd", "ShopManager");
        boolean update = shopDB.updateUser(uID, "Rosario2", "testpwd2", "Cashier");
        boolean delete = shopDB.deleteUser(uID);

        assertNotEquals(uID, defaultID);
        assertFalse(shopDB.updateUser(null, "Rosario2", "testpwd2", "Cashier"));
        assertFalse(shopDB.deleteUser(null));
        assertTrue(update);
        assertTrue(delete);

        HashMap<Integer, EZUser> map = shopDB.selectAllUsers();
        System.out.println(map);
        assertTrue(map.size() > 0);

    }

    @Test
    public void testNoConnection() {
        shopDB.closeConnection();

        shopDB.initDatabase();
    }
}
