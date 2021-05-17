package it.polito.ezshop.BlackBoxTesting;

import static org.junit.Assert.*;

import it.polito.ezshop.data.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sqlite.SQLiteException;

import java.util.HashMap;

public class TestEZShop_SQLiteDB {
    private SQLiteDB shopDB;
    private HashMap<String, Integer> ezCards = null;
    private HashMap<Integer, EZUser> ezUsers = null;
    private HashMap<Integer, EZOrder> ezOrders = null;
    private HashMap<Integer, EZCustomer> ezCustomers = null;
    private HashMap<Integer, EZProductType> ezProducts = null;
    private HashMap<Integer, EZSaleTransaction> ezSaleTransactions = null;
    private HashMap<Integer, EZBalanceOperation> ezBalanceOperations = null;
    private HashMap<Integer, EZReturnTransaction> ezReturnTransactions = null;
    private static final int defaultID = -1;
    private static final int defaultValue = 0;

    @Before
    public void connectAndInit() {
        shopDB = new SQLiteDB();
        assertTrue(shopDB.connect());

        // Check connection
        assertTrue(shopDB.isConnected());

        // Check selectAll queries
        this.testSelectAllQueries();

        shopDB.initDatabase();
    }

    @After
    public void closeConnection() {
        shopDB.closeConnection();

        // Check closeConnection
        assertFalse(shopDB.isConnected());

        shopDB = null;
    }

    private void testSelectAllQueries() {

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
    public void testNoConnection() {
        shopDB.closeConnection();

        shopDB.initDatabase();
    }
}
