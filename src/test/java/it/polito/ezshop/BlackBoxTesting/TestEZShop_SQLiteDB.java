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

import java.sql.SQLException;
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
    public void testCustomer() {
        String cName = "Johnny";
        String cCustomerCard = "0364829165";
        int cPoints = 185;

        EZCustomer customer = new EZCustomer(defaultID, cName, cCustomerCard, cPoints);
        int cID = shopDB.insertCustomer(cName, cCustomerCard);

        // Check if insertion was successful
        assertNotEquals(cID, defaultID);

        assertEquals((int) shopDB.insertCustomer(null, cCustomerCard), defaultID);
        assertEquals((int) shopDB.insertCustomer(cName, null), defaultID);


        // Check if prevents insertion of another customer with same card/name
        int cstID = shopDB.insertCustomer(cName, "0364829166");
        assertEquals(defaultID, cstID);
        int cstID2 = shopDB.insertCustomer("Johnny2", cCustomerCard);
        assertEquals(defaultID, cstID2);

        int c3 = shopDB.insertCustomer("Marcus", "0364829999");
        // Check updateCustomer
        assertTrue(shopDB.updateCustomer(cID,  "Jack", "0002938475"));    // Valid params
        assertFalse(shopDB.updateCustomer(null, "Jack", "0002938475")); // Invalid ID
        assertFalse(shopDB.updateCustomer(cID, "Al", null)); // Null card
        assertTrue(shopDB.updateCustomer(cID, "Jack", "")); // Empty card
        assertFalse(shopDB.updateCustomer(cID, null, "0002938475")); // Null name
        assertFalse(shopDB.updateCustomer(cID, "", "0002938475")); // Null name
        assertFalse(shopDB.updateCustomer(cID, "Marcus", cCustomerCard));
        assertFalse(shopDB.updateCustomer(cID, "Alphonse", "0364829999"));
        // Check deleteCustomer
        assertTrue(shopDB.deleteCustomer(cID));
        assertFalse(shopDB.deleteCustomer(null));
    }

    @Test
    public void testBalanceOperation() {
        LocalDate cDate = LocalDate.now();
        double cMoney = 12.50;
        String cType = "CREDIT";

        // Proper balanceOperation insertion
        int id = shopDB.insertBalanceOperation(cDate, cMoney, cType);
        assertNotEquals(id, defaultID);

        // Insert with null date
        int failID = shopDB.insertBalanceOperation(null, cMoney, cType);
        assertEquals(failID, defaultID);

        // Insert with null type
        failID = shopDB.insertBalanceOperation(cDate, cMoney, null);
        assertEquals(failID, defaultID);

        // Proper balanceOperation update
        boolean updated = shopDB.updateBalanceOperation(id, LocalDate.now(), 16.80, cType);
        assertTrue(updated);

        // Update with null date
        updated = shopDB.updateBalanceOperation(id, null, 16.80, cType);
        assertFalse(updated);

        // Update with null type
        updated = shopDB.updateBalanceOperation(id, LocalDate.now(), 16.80, null);
        assertFalse(updated);

        // Update with inexistent ID
        updated = shopDB.updateBalanceOperation(failID, LocalDate.now(), 16.80, cType);
        assertTrue(updated);

        // Delete balanceOperation with existent ID
        boolean deleted = shopDB.deleteBalanceOperation(id);
        assertTrue(deleted);

        // Delete balanceOperation with inexistent DB
        deleted = shopDB.deleteBalanceOperation(failID);
        assertTrue(deleted);

        // Testing here totalBalance because there's not so much to test about it
        double balance = shopDB.selectTotalBalance();
    }

    @Test
    public void testOrder() {
        int cBalanceID = 2;
        String cProductCode = "7293829484929";
        double cPricePerUnit = 8.40;
        int cQuantity = 12;
        String cStatus = "COMPLETED";

        // Proper Order insertion
        int id = shopDB.insertOrder(cBalanceID, cProductCode, cPricePerUnit, cQuantity, cStatus);
        assertNotEquals(id, defaultID);

        // Insertion with null balanceID
        int failID = shopDB.insertOrder(null, cProductCode, cPricePerUnit, cQuantity, cStatus);
        assertEquals(failID, defaultID);

        // Insertion with inexistent balanceID
        failID = shopDB.insertOrder(87658876, cProductCode, cPricePerUnit, cQuantity, cStatus);
        assertNotEquals(failID, defaultID);

        // Insertion with inexistent productCode
        failID = shopDB.insertOrder(cBalanceID, "4762834629", cPricePerUnit, cQuantity, cStatus);
        assertNotEquals(failID, defaultID);

        // Insertion with null productCode
        failID = shopDB.insertOrder(cBalanceID, null, cPricePerUnit, cQuantity, cStatus);
        assertEquals(failID, defaultID);

        // Insertion with null status
        failID = shopDB.insertOrder(cBalanceID, cProductCode, cPricePerUnit, cQuantity, null);
        assertEquals(failID, defaultID);

        // Proper Order update
        boolean updated = shopDB.updateOrder(id, cBalanceID, "2747364827", cPricePerUnit, cQuantity, cStatus);
        assertTrue(updated);

        // Order update with inexistent id
        updated = shopDB.updateOrder(failID, cBalanceID, "2749964827", cPricePerUnit, cQuantity, cStatus);
        assertTrue(updated);

        // Delete order
        boolean deleted = shopDB.deleteOrder(id);
        assertTrue(deleted);

        // Delete order with inexistent id
        deleted = shopDB.deleteOrder(failID);
        assertTrue(deleted);
    }

    @Test
    public void testCard() {
        Integer cPoints = 125;

        // Proper Card insertions
        String validCard = shopDB.insertCard(cPoints);
        assertNotNull(validCard);
        assertTrue(EZShop.isValidCard(validCard));

        // Insert Card with null points
        String invalidCard = shopDB.insertCard(null);
        assertTrue(invalidCard.isEmpty());

        // Proper Card update
        boolean updated = shopDB.updateCard(validCard, cPoints);
        assertTrue(updated);

        // Update Card with null points
        updated = shopDB.updateCard(validCard, null);
        assertFalse(updated);

        // Update Card with inexistent cardCode
        updated = shopDB.updateCard(invalidCard, cPoints);
        assertFalse(updated);

        // Delete Card
        boolean deleted = shopDB.deleteCard(validCard);
        assertTrue(deleted);

        // Delete Card with invalid code
        deleted = shopDB.deleteCard(invalidCard);
        assertFalse(deleted);
    }

    @Test
    public void testUserDB() throws SQLException {
        int uID = shopDB.insertUser("Rosario", "testpwd", "ShopManager");
        assertEquals(defaultID, (int) shopDB.insertUser(null, null, null));
        assertEquals(defaultID, (int) shopDB.insertUser("Rosario", null, null));
        assertEquals(defaultID, (int) shopDB.insertUser("Rosario", "testpwd", null));
        int uID2 = shopDB.insertUser("Rosario", "testpwd", "ShopManager");
        boolean update = shopDB.updateUser(uID, "Rosario2", "testpwd2", "Cashier");

        assertFalse(shopDB.updateUser(null, null, null, null));
        assertFalse(shopDB.updateUser(uID, "Rosario", null, null));
        assertFalse(shopDB.updateUser(uID, "Rosario", "testpwd", null));

        assertNotEquals(defaultID, uID);
        assertEquals(defaultID, uID2);
        assertFalse(shopDB.updateUser(null, "Rosario2", "testpwd2", "Cashier"));
        assertFalse(shopDB.deleteUser(null));
        assertTrue(update);

        HashMap<Integer, EZUser> map = shopDB.selectAllUsers();
        System.out.println(map);
        assertTrue(map.size() > 0);

        int uID3 = shopDB.insertUser("sheldon", "testpwd", "ShopManager");
        assertEquals(defaultID, uID3);
        boolean update2 = shopDB.updateUser(uID, "sheldon", "testpwd2", "Cashier");
        assertFalse(update2); // User already exists

        boolean delete = shopDB.deleteUser(uID);
        assertTrue(delete);
    }

    @Test
    public void testNoConnection() {
        shopDB.closeConnection();

        shopDB.initDatabase();
    }

    @Test
    public void testProdType(){
        // test deleteProductType
        int id = shopDB.insertProductType(10, "abc", "abc", "abc", "4627828478338", 0.9);
        assertTrue(shopDB.deleteProductType(id));
        assertFalse(shopDB.deleteProductType(null));

        //test updateProductType
        id = shopDB.insertProductType(10, "abc", "abc", "abc", "4627828478338", 0.9);
        assertTrue(shopDB.updateProductType(id, 2, "abc", "abc", "niceProd", "6291041500213" , 1.0));
        assertFalse(shopDB.updateProductType(null, 2, "abc", "abc", "niceProd", "6291041500213", 1.0));
        assertFalse(shopDB.updateProductType(id, null, "abc", "abc", "niceProd", "6291041500213", 1.0));
        assertFalse(shopDB.updateProductType(id, 2, null, "abc", "niceProd", "6291041500213", 1.0));
        assertFalse(shopDB.updateProductType(id, 2, "abc", null, "niceProd", "6291041500213", 1.0));
        assertFalse(shopDB.updateProductType(id, 0, "abc", "abc", null, "6291041500213", 1.0));
        assertFalse(shopDB.updateProductType(id, -1, "abc", "abc", "niceProd", null, 1.0));
    }

    @Test
    public void testInsDelTransaction(){
        // test InsertReturnTransaction
        LinkedList<TicketEntry> list = new LinkedList<>();
        EZTicketEntry entry = new EZTicketEntry("6291041500213", "abc", 10, 0.5, 0.9);
        list.add(entry);
        int id1 = shopDB.insertReturnTransaction(list, 1, 1.0, "OPENED");
        assertTrue(id1 > 0);
        int id2 = shopDB.insertReturnTransaction(list, 1, 1.0, null);
        assertEquals(-1, id2);

        // test deleteTransaction
        assertTrue(shopDB.deleteTransaction(id1));
        assertFalse(shopDB.deleteTransaction(null));
    }

    @Test
    public void testUpdateReturnTransaction() {
        //todo: to be added to testReturnTransactions method (ALSO update UnitTestReport.md on the location of this method!)

        Integer st1 = shopDB.insertSaleTransaction(null, defaultValue, defaultValue, EZSaleTransaction.STOpened);
        Integer rt1 = shopDB.insertReturnTransaction(null, st1, defaultValue, EZReturnTransaction.RTOpened);

        boolean update = shopDB.updateReturnTransaction(rt1, 45.60, EZReturnTransaction.RTClosed);
        assertTrue(update);
        update = shopDB.updateReturnTransaction(null, 45.60, EZReturnTransaction.RTClosed);
        assertFalse(update);
        update = shopDB.updateReturnTransaction(rt1, 45.60, null);
        assertFalse(update); //??? Should be an assertTrue?

    }

    @Test
    public void testProductPerSales() {
        //todo: lack of test on insertProductPerSale ???

        Integer st1 = shopDB.insertSaleTransaction(null, defaultValue, defaultValue, EZSaleTransaction.STOpened);
        Integer st2 = shopDB.insertSaleTransaction(null, defaultValue, defaultValue, EZSaleTransaction.STOpened);

        shopDB.insertProductPerSale("4627828478338", st1, 3, 0.30);
        boolean delete = shopDB.deleteProductPerSale("4627828478338", st1);
        assertTrue(delete);
        delete = shopDB.deleteProductPerSale(null, st1);
        assertFalse(delete);
        delete = shopDB.deleteProductPerSale("4627828478338", null);
        assertFalse(delete);

        shopDB.insertProductPerSale("4627828478338", st1, 3, 0.30);
        shopDB.insertProductPerSale("2141513141144", st1, 5, 0.70);
        delete = shopDB.deleteAllProductsPerSale(null);
        assertFalse(delete);
        delete = shopDB.deleteAllProductsPerSale(st1);
        assertTrue(delete);

        shopDB.insertProductPerSale("4627828478338", st2, 3, 0.30);
        shopDB.insertProductPerSale("2141513141144", st2, 5, 0.70);
        boolean update = shopDB.updateProductPerSale("4627828478338", st2, 5, 0.60);
        assertTrue(update);
        update = shopDB.updateProductPerSale(null, st2, 5, 0.60);
        assertFalse(update);
        update = shopDB.updateProductPerSale("4627828478338", null, 5, 0.60);
        assertFalse(update);
    }
}
