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

        // Test connection
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

        // Test authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertTrue(ezShop.updateQuantity(this.products.get(1), 5));

        // Test missing DB's connection
        this.shopDB.closeConnection();
        assertFalse(ezShop.updateQuantity(this.products.get(0), 5));

        // Test authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.updateQuantity(this.products.get(1), 5);
        });

        // Test authorization when no user is logged in
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

        // Test authorization for ShopManager
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

        // Test authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.updatePosition(this.products.get(1), "15-JK-50");
        });

        // Test authorization when no user is logged in
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

        // Test authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertTrue(ezShop.issueOrder(prods.get(1).getBarCode(), 120,1.20) > 0);

        // Test missing DB's connection
        shopDB.closeConnection();
        assertEquals(defaultID, ezShop.issueOrder(prods.get(1).getBarCode(), 120,1.20).longValue());

        // Test authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.issueOrder("C0D3", -25, -1.20);
        });

        // Test authorization when no user is logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.issueOrder("C0D3", -25, -1.20);
        });

        // Test after ezShop's reset
        ezShop.reset();
        ezShop.login("manager", "manager");
        assertEquals(defaultID, ezShop.issueOrder(prods.get(1).getBarCode(), 120,1.20).longValue());
    }

    @Test
    public void testPayOrderFor() throws UnauthorizedException, InvalidQuantityException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        // Get products in EZShop
        List<ProductType> prods = ezShop.getAllProductTypes();

        // Test successful case
        assertTrue(ezShop.payOrderFor(prods.get(1).getBarCode(), 20, 1.50) > 0);

        // Test valid but inexistent barCode
        assertEquals(defaultID, ezShop.payOrderFor("4627828478338", 20, 1.50).longValue());

        // Test insufficient balance
        assertEquals(defaultID, ezShop.payOrderFor(prods.get(1).getBarCode(), 9000, 999).longValue());

        // Test null barCode
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.payOrderFor(null, 25, 1.20);
        });

        // Test empty barCode
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.payOrderFor("", 25, 1.20);
        });

        // Test invalid barCode
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.payOrderFor("C0D3", 25, 1.20);
        });

        // Test invalid quantity (== 0)
        assertThrows(InvalidQuantityException.class, () -> {
            ezShop.payOrderFor(prods.get(1).getBarCode(), 0, 1.20);
        });

        // Test invalid quantity (< 0)
        assertThrows(InvalidQuantityException.class, () -> {
            ezShop.payOrderFor(prods.get(1).getBarCode(), -10, 1.20);
        });

        // Test invalid pricePerUnit (== 0)
        assertThrows(InvalidPricePerUnitException.class, () -> {
            ezShop.payOrderFor(prods.get(1).getBarCode(), 10, 0);
        });

        // Test invalid pricePerUnit (< 0)
        assertThrows(InvalidPricePerUnitException.class, () -> {
            ezShop.payOrderFor(prods.get(1).getBarCode(), 10, -1.20);
        });

        // Test authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertTrue(ezShop.payOrderFor(prods.get(1).getBarCode(), 20, 1.50) > 0);

        // Test missing DB's connection
        shopDB.closeConnection();
        assertEquals(defaultID, ezShop.payOrderFor(prods.get(1).getBarCode(), 20, 1.50).longValue());

        // Test authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.payOrderFor("4627828478338", 20, 1.50);
        });

        // Test authorization when no user is logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.payOrderFor("4627828478338", -20, -1.50);
        });
    }

    @Test
    public void testPayOrder() throws UnauthorizedException, InvalidQuantityException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidOrderIdException, InvalidPasswordException, InvalidUsernameException {
        // Get products in EZShop
        List<ProductType> prods = ezShop.getAllProductTypes();

        // Add some order to pay
        List<Integer> orders = new LinkedList<>();
        orders.add(ezShop.issueOrder(prods.get(0).getBarCode(), 120,1.20));
        orders.add(ezShop.issueOrder(prods.get(1).getBarCode(), 30,3.50));
        orders.add(ezShop.issueOrder(prods.get(2).getBarCode(), 9000,999));
        orders.add(ezShop.issueOrder(prods.get(1).getBarCode(), 20,2.50));

        // Test successful case
        assertTrue(ezShop.payOrder(orders.get(0)));

        // Test payOrder on PAYED order
        assertFalse(ezShop.payOrder(orders.get(0)));

        // Test inexistent order
        assertFalse(ezShop.payOrder(30));

        // Test null orderID
        assertThrows(InvalidOrderIdException.class, () -> {
            ezShop.payOrder(null);
        });

        // Test invalid orderID (== 0)
        assertThrows(InvalidOrderIdException.class, () -> {
            ezShop.payOrder(0);
        });

        // Test invalid orderID (< 0)
        assertThrows(InvalidOrderIdException.class, () -> {
            ezShop.payOrder(-10);
        });

        // Test insufficient balance
        assertFalse(ezShop.payOrder(orders.get(2)));

        // Test authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertTrue(ezShop.payOrder(orders.get(1)));

        // Test missing DB's connection
        shopDB.closeConnection();
        assertFalse(ezShop.payOrder(orders.get(3)));

        // Test authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.payOrder(orders.get(3));
        });

        // Test authorization when no user is logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.payOrder(null);
        });
    }

    @Test
    public void testRecordOrderArrival() throws UnauthorizedException, InvalidQuantityException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidLocationException, InvalidOrderIdException, InvalidProductIdException, InvalidPasswordException, InvalidUsernameException {
        // Get products in EZShop
        List<ProductType> prods = ezShop.getAllProductTypes();

        // Add some order to pay
        List<Integer> orders = new LinkedList<>();
        orders.add(ezShop.issueOrder(prods.get(0).getBarCode(), 120,1.20));
        orders.add(ezShop.issueOrder(prods.get(1).getBarCode(), 30,3.50));
        orders.add(ezShop.issueOrder(prods.get(2).getBarCode(), 40,9.99));
        orders.add(ezShop.issueOrder(prods.get(3).getBarCode(), 20,2.50));

        // Test order with status != PAYED and != COMPLETED
        assertFalse(ezShop.recordOrderArrival(orders.get(1)));

        // Test successful case
        ezShop.payOrder(orders.get(0));
        ezShop.updatePosition(this.products.get(0), "15-GH-50");
        assertTrue(ezShop.recordOrderArrival(orders.get(0)));

        // Test null orderID
        assertThrows(InvalidOrderIdException.class, () -> {
            ezShop.recordOrderArrival(null);
        });

        // Test invalid orderID (== 0)
        assertThrows(InvalidOrderIdException.class, () -> {
            ezShop.recordOrderArrival(0);
        });

        // Test invalid orderID (< 0)
        assertThrows(InvalidOrderIdException.class, () -> {
            ezShop.recordOrderArrival(-10);
        });

        // Test valid but inexistent orderID
        assertFalse(ezShop.recordOrderArrival(30));

        // Test inexistent productType
        ezShop.payOrder(orders.get(1));
        ezShop.deleteProductType(prods.get(1).getId());
        assertFalse(ezShop.recordOrderArrival(orders.get(1)));

        // Test invalid location
        ezShop.payOrder(orders.get(3));
        assertThrows(InvalidLocationException.class, () -> {
            ezShop.recordOrderArrival(orders.get(3));
        });

        // Test authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        ezShop.updatePosition(this.products.get(3), "15-KK-50");
        assertTrue(ezShop.recordOrderArrival(orders.get(3)));

        // Test missing DB's connection
        ezShop.updatePosition(this.products.get(2), "15-YY-50");
        ezShop.payOrder(orders.get(2));
        shopDB.closeConnection();
        ezShop.payOrder(orders.get(2));
        assertFalse(ezShop.recordOrderArrival(orders.get(2)));

        // Test authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.recordOrderArrival(orders.get(3));
        });

        // Test authorization when no user is logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.recordOrderArrival(orders.get(3));
        });
    }
}
