package it.polito.ezshop.IntegrationBBTesting;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SQLiteDB;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static it.polito.ezshop.data.EZUser.*;
import static org.junit.Assert.*;

public class TestEZShopFR3 {
    private EZShop ezShop;
    private SQLiteDB shopDB;
    private Integer productID;

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
        ezShop.createUser("cashier", "cashier", URCashier);
        ezShop.createUser("manager", "manager", URShopManager);

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
        productID = ezShop.createProductType(prodDescription, prodCode, pricePerUnit,prodNote);
        assertTrue(productID > 0);

        // Proper product creation
        productID = ezShop.createProductType(prodDescription, prodCode, pricePerUnit,prodNote);
        assertFalse(productID > 0);

        // Product creation with null note
        productID = ezShop.createProductType(prodDescription, "1627482847283", pricePerUnit, null);
        assertTrue(productID > 0);

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

   @Test
    public void testUpdateProduct() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
       final String prodDescription = "Test product's description";
       final  String prodCode = "5839274928315";
       final double pricePerUnit = 4.50;
       final String prodNote = "Product's note";

       // Proper product creation
       productID = ezShop.createProductType(prodDescription, prodCode, pricePerUnit,prodNote);

       // Check null id
       assertThrows(InvalidProductIdException.class, () -> {
           ezShop.updateProduct(null, "New description", "5839274928315", 12.50, "New note");
       });

       // Check invalid id (== 0)
       assertThrows(InvalidProductIdException.class, () -> {
           ezShop.updateProduct(0, "New description", "5839274928315", 12.50, "New note");
       });

       // Check invalid id (< 0)
       assertThrows(InvalidProductIdException.class, () -> {
           ezShop.updateProduct(-5, "New description", "5839274928315", 12.50, "New note");
       });

       // Check null description
       assertThrows(InvalidProductDescriptionException.class, () -> {
           ezShop.updateProduct(productID, null, "5839274928315", 12.50, "New note");
       });

       // Check invalid description
       assertThrows(InvalidProductDescriptionException.class, () -> {
           ezShop.updateProduct(productID, "", "5839274928315", 12.50, "New note");
       });

       // Check authorization if no user is logged in
       ezShop.logout();
       assertThrows(UnauthorizedException.class, () -> {
            ezShop.updateProduct(productID, "New description", "NEWCODE", 12.50, "New note");
       });
    }
}
