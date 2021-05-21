package it.polito.ezshop.IntegrationBBTesting;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.SQLiteDB;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static it.polito.ezshop.data.EZUser.URAdministrator;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestEZShopFR3 {
    private EZShop ezShop;
    private SQLiteDB shopDB;

    @Before
    public void init() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException {
        shopDB = new SQLiteDB();
        shopDB.connect();

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

        ezShop = new EZShop();

        ezShop.createUser("admin", "admin", URAdministrator);
        ezShop.login("admin", "admin");
    }

    @After
    public void endTest() {
        if (shopDB != null) {
            shopDB.closeConnection();
            shopDB = null;
        }

        ezShop = null;
    }


    @Test
    public void testAddProduct() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
        final String prodDescription = "Test product's description";
        final  String prodCode = "5839274928315";
        final double pricePerUnit = 4.50;
        final String prodNote = "Product's note";

        // Proper product creation
        Integer prodID = ezShop.createProductType(prodDescription, prodCode, pricePerUnit,prodNote);
        assertTrue(prodID > 0);

        // Product creation with null note
        prodID = ezShop.createProductType(prodDescription, "1627482847283", pricePerUnit, null);
        assertTrue(prodID > 0);

        // Product creation with null description
        assertThrows(InvalidProductDescriptionException.class, () -> {
            ezShop.createProductType(null, prodCode, pricePerUnit,prodNote);
        });

        // Product creation with null product code
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.createProductType(prodDescription, null, pricePerUnit, prodNote);
        });

        // Product creation with invalid product code
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.createProductType(prodDescription, "5839274928316", pricePerUnit, prodNote);
        });

        // Product creation with invalid pricePerUnit (< 0)
        assertThrows(InvalidPricePerUnitException.class, () -> {
            ezShop.createProductType(prodDescription, prodCode, -2.50, prodNote);
        });

        // Product creation with invalid pricePerUnit (== 0)
        assertThrows(InvalidPricePerUnitException.class, () -> {
            ezShop.createProductType(prodDescription, prodCode, 0, prodNote);
        });

        // Logout in order to check that UnauthorizedException precedes any other exception
        ezShop.logout();

        // UnauthorizedException should precede InvalidProductDescriptionException
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.createProductType(null, prodCode, pricePerUnit,prodNote);
        });

        // UnauthorizedException should precede InvalidProductCodeException
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.createProductType(prodDescription, null, pricePerUnit, prodNote);
        });

        // UnauthorizedException should precede InvalidPricePerUnitException
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.createProductType(prodDescription, prodCode, -2.50, prodNote);
        });
    }
}
