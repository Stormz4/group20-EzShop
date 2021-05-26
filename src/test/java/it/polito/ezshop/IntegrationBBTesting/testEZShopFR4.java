package it.polito.ezshop.IntegrationBBTesting;

import it.polito.ezshop.data.EZProductType;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.SQLiteDB;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static it.polito.ezshop.data.EZUser.*;
import static org.junit.Assert.*;
import static it.polito.ezshop.data.SQLiteDB.defaultID;

public class testEZShopFR4 {
    private EZShop ezShop;
    private SQLiteDB shopDB;
    private LinkedList<Integer> products;

    @Before
    public void initTest() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
        shopDB = new SQLiteDB();
        shopDB.connect();

        // Check connection
        assertTrue(shopDB.isConnected());

        // Init DB and clear all tables
        shopDB.initDatabase();
        shopDB.clearAllTables();

        ezShop = new EZShop();

        // Create users
        ezShop.createUser("admin", "admin", URAdministrator);
        ezShop.createUser("cashier", "cashier", URCashier);
        ezShop.createUser("manager", "manager", URShopManager);

        // Login as admin
        ezShop.login("admin", "admin");

        // Add some product for tests
        this.products = new LinkedList<>();
        this.addSomeProductToTest();
    }

    @After
    public void endTest() {
        if (shopDB != null) {
            shopDB.closeConnection();
            shopDB = null;
        }

        ezShop = null;
    }

    private void addSomeProductToTest() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {
        this.products.add(ezShop.createProductType("Description 1", "1345334543427", 1.00, "Note 1"));
        this.products.add(ezShop.createProductType("Description 2", "4532344529689", 2.00, "Note 2"));
        this.products.add(ezShop.createProductType("Description 3", "5839274928315", 3.00, "Note 3"));
        this.products.add(ezShop.createProductType("Description", "1627482847283", 4.00, "Note 4"));
        this.products.add(ezShop.createProductType("Description", "4778293942845", 5.00, "Note 5"));
    }

    @Test
    public void testUpdateQuantity() throws UnauthorizedException, InvalidProductIdException, InvalidPasswordException, InvalidUsernameException {
        // Test proper update
        assertTrue(ezShop.updateQuantity(this.products.get(1), 5));

        // Test null product id
        assertThrows(InvalidProductIdException.class, () -> {
            ezShop.updateQuantity(null, 5);
        });

        // Test with product id == 0
        assertThrows(InvalidProductIdException.class, () -> {
            ezShop.updateQuantity(0, 5);
        });

        // Test with product id < 0
        assertThrows(InvalidProductIdException.class, () -> {
            ezShop.updateQuantity(-2, 5);
        });

        // Test valid but inexistent id
        assertFalse(ezShop.updateQuantity(12, 5));

        // Test insufficient quantity
        assertFalse(ezShop.updateQuantity(this.products.get(2), -25));

        // Check authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertTrue(ezShop.updateQuantity(this.products.get(1), 5));

        // Test missing DB's connection
        this.shopDB.closeConnection();
        assertFalse(ezShop.updateQuantity(this.products.get(0), 5));

        // Check authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.updateQuantity(this.products.get(1), 5);
        });

        // Check authorization when no user is logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.updateQuantity(this.products.get(1), 5);
        });
    }

    @Test
    public void testUpdatePosition() throws InvalidLocationException, UnauthorizedException, InvalidProductIdException, InvalidPasswordException, InvalidUsernameException {
        // Test proper update
        assertTrue(ezShop.updatePosition(this.products.get(1), "15-GH-50"));

        // Test null productID
        assertThrows(InvalidProductIdException.class, () -> {
            ezShop.updatePosition(null, "15-GH-50");
        });

        // Test productID == 0
        assertThrows(InvalidProductIdException.class, () -> {
            ezShop.updatePosition(0, "15-GH-50");
        });

        // Test productID < 0
        assertThrows(InvalidProductIdException.class, () -> {
            ezShop.updatePosition(-3, "15-GH-50");
        });

        // Test valid but inexistent id
        assertFalse(ezShop.updatePosition(20, "15-GH-50"));

        // Test null newPos and empty newPos
        assertTrue(ezShop.updatePosition(this.products.get(2), null));
        assertTrue(ezShop.updatePosition(this.products.get(2), ""));

        // Test uniqueness of position
        assertFalse(ezShop.updatePosition(this.products.get(3), "15-GH-50"));

        // Check authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertTrue(ezShop.updatePosition(this.products.get(1), "15-JK-50"));

        // Test missing DB's connection
        shopDB.closeConnection();
        assertFalse(ezShop.updatePosition(this.products.get(2), ""));
        assertFalse(ezShop.updatePosition(this.products.get(2), "16-GH-60"));

        // Test invalid newPos
        assertThrows(InvalidLocationException.class, () -> {
            ezShop.updatePosition(this.products.get(2), "N3WP0S");
        });

        // Check authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.updatePosition(this.products.get(1), "15-JK-50");
        });

        // Check authorization when no user is logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.updateQuantity(this.products.get(1), 5);
        });
    }

    @Test
    public void testIssueOrder() throws UnauthorizedException, InvalidQuantityException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        // Get products in EZShop
        List<ProductType> prods = ezShop.getAllProductTypes();

        // Test successful case
        assertTrue(ezShop.issueOrder(prods.get(1).getBarCode(), 120,1.20) > 0);

        // Test invalid quantity (== 0)
        assertThrows(InvalidQuantityException.class, () -> {
            ezShop.issueOrder(prods.get(1).getBarCode(), 0, 1.20);
        });

        // Test invalid quantity (< 0)
        assertThrows(InvalidQuantityException.class, () -> {
            ezShop.issueOrder(prods.get(1).getBarCode(), -15, 1.20);
        });

        // Test inexistent productCode
        assertEquals(defaultID, ezShop.issueOrder("4892937849335", 10, 1.20).longValue());

        // Test null productCode
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.issueOrder(null, 25, 1.20);
        });

        // Test empty productCode
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.issueOrder("", 25, 1.20);
        });

        // Test invalid productCode
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.issueOrder("C0D3", 25, 1.20);
        });

        // Test invalid pricePerUnit (== 0)
        assertThrows(InvalidPricePerUnitException.class, () -> {
            ezShop.issueOrder(prods.get(1).getBarCode(), 25, 0);
        });

        // Test invalid pricePerUnit (< 0)
        assertThrows(InvalidPricePerUnitException.class, () -> {
            ezShop.issueOrder(prods.get(1).getBarCode(), 25, -1.20);
        });

        // Check authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertTrue(ezShop.issueOrder(prods.get(1).getBarCode(), 120,1.20) > 0);

        // Test missing DB's connection
        shopDB.closeConnection();
        assertEquals(defaultID, ezShop.issueOrder(prods.get(1).getBarCode(), 120,1.20).longValue());

        // Check authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.issueOrder("C0D3", -25, -1.20);
        });

        // Check authorization when no user is logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.issueOrder("C0D3", -25, -1.20);
        });

        // Check after ezShop's reset
        ezShop.reset();
        ezShop.login("manager", "manager");
        assertEquals(defaultID, ezShop.issueOrder(prods.get(1).getBarCode(), 120,1.20).longValue());
    }
}
