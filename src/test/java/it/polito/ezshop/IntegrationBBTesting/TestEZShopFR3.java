package it.polito.ezshop.IntegrationBBTesting;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SQLiteDB;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static it.polito.ezshop.data.EZUser.*;
import static it.polito.ezshop.data.SQLiteDB.defaultID;
import static org.junit.Assert.*;

public class TestEZShopFR3 {
    private EZShop ezShop;
    private SQLiteDB shopDB;
    private Integer productID;

    @Before
    public void initTest() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException {
        shopDB = new SQLiteDB();
        shopDB.connect();

        // Check connection
        assertTrue(shopDB.isConnected());

        // Init DB
        shopDB.initDatabase();

        // CLear every DB table
        // Init DB and clear all tables
        shopDB.initDatabase();
        shopDB.clearDatabase();

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

    private void addSomeProductToTest() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException {

        ezShop.createProductType("Description 1", "1345334543427", 1.00, "Note 1");
        ezShop.createProductType("Description 2", "4532344529689", 2.00, "Note 2");
        ezShop.createProductType("Description 3", "5839274928315", 3.00, "Note 3");
        ezShop.createProductType("Description", "1627482847283", 4.00, "Note 4");
        ezShop.createProductType("Description", "4778293942845", 5.00, "Note 5");
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

        // Check behavior if DB connection is not opened
        this.shopDB.closeConnection();
        productID = ezShop.createProductType(prodDescription, "4627828478338", pricePerUnit, "Note");
        assertEquals(defaultID, productID.intValue());

        // Logout in order to check that UnauthorizedException precedes any other exception
        ezShop.logout();

        // UnauthorizedException should precede other exceptions
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.createProductType(null, null, -2.50, prodNote);
        });
    }

   @Test
    public void testUpdateProduct() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException, InvalidProductIdException {
       final String prodDescription = "Test product's description";
       final String prodCode = "5839274928315";
       final double pricePerUnit = 4.50;
       final String prodNote = "Product's note";

       // Proper product creation
       productID = ezShop.createProductType(prodDescription, prodCode, pricePerUnit,prodNote);
       int productID2 = ezShop.createProductType(prodDescription, "3738456849238", pricePerUnit,prodNote);


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

       // Check invalid product code
       assertThrows(InvalidProductCodeException.class, () -> {
           ezShop.updateProduct(productID, "", "583927492315", 12.50, "New note");
       });

       // Check null product code
       assertThrows(InvalidProductCodeException.class, () -> {
           ezShop.updateProduct(productID, "", null, 12.50, "New note");
       });

       // Check invalid price
       assertThrows(InvalidPricePerUnitException.class, () -> {
           assertFalse(ezShop.updateProduct(productID, "New description", prodCode, -10.20, "Note2"));
       });

       // Check valid update
       assertTrue(ezShop.updateProduct(productID, "New description", prodCode, 10.20, "Note2"));

       // Check for barcode uniqueness
       assertFalse(ezShop.updateProduct(productID2, "New description", prodCode, 10.20, "Note2"));

       // Check inexistent id
       assertFalse(ezShop.updateProduct(5, "New description", prodCode, 10.20, "Note2"));

       // Check authorization if no user is logged in
       ezShop.logout();
       assertThrows(UnauthorizedException.class, () -> {
            ezShop.updateProduct(productID, "New description", "NEWCODE", 12.50, "New note");
       });

       // Check authorization if Cashier (which is not allowed to perform a product update) is logged in
       ezShop.login("cashier", "cashier");
       assertThrows(UnauthorizedException.class, () -> {
           ezShop.updateProduct(productID, prodDescription, prodCode, 12.50, "New note");
       });
       ezShop.logout();

       // Check authorization if Manager, which should be able to perform the update, is logged in
       ezShop.login("manager", "manager");
       assertTrue(ezShop.updateProduct(productID, "3rd description", prodCode, 3.60, "Note3"));

       // Check behavior if DB connection is not opened
       this.shopDB.closeConnection();
       assertFalse(ezShop.updateProduct(productID, "New description", prodCode, 10.20, "Note2"));
    }

    @Test
    public void testDeleteProduct() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException, InvalidPasswordException, InvalidUsernameException {
        // Check proper deletion as admin
        productID = ezShop.createProductType("Product description", "3738456849238", 2.50, "Prod note");
        assertTrue(ezShop.deleteProductType(productID));

        // Check deletion with inexistent id
        assertFalse(ezShop.deleteProductType(productID));

        // Check proper deletion as shop manager
        ezShop.logout();
        ezShop.login("manager", "manager");
        productID = ezShop.createProductType("Product description", "3738456849238", 2.50, "Prod note");
        assertTrue(ezShop.deleteProductType(productID));

        // Check deletion with null id
        assertThrows(InvalidProductIdException.class, () -> {
            ezShop.deleteProductType(null);
        });

        // Check deletion with invalid id
        assertThrows(InvalidProductIdException.class, () -> {
            ezShop.deleteProductType(defaultID);
        });

        // Check deletion if DB's connection is not opened
        productID = ezShop.createProductType("Product description", "3738456849238", 2.50, "Prod note");
        this.shopDB.closeConnection();
        assertFalse(ezShop.deleteProductType(productID));

        // Check unauthorizedException if logged user is Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.deleteProductType(defaultID);
        });

        // No user logged
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.deleteProductType(defaultID);
        });
    }

    @Test
    public void testGetAllProducts() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        // Check that does not return null even if there's no productType in EZShop
        assertNotNull(ezShop.getAllProductTypes());

        this.addSomeProductToTest();

        // Check that a non-empty list is returned
        assertNotNull(ezShop.getAllProductTypes());
        assertNotEquals(0, ezShop.getAllProductTypes().size());

        // Check Cashier's authorization
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertNotNull(ezShop.getAllProductTypes());

        // Check ShopManager's authorization
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertNotNull(ezShop.getAllProductTypes());

        // Check authorization with no user logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.getAllProductTypes();
        });
    }

    @Test
    public void testGetProductTypeByBarCode() throws InvalidProductCodeException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidPasswordException, InvalidUsernameException {
        this.addSomeProductToTest();

        // Check proper get
        assertNotNull(ezShop.getProductTypeByBarCode("4532344529689"));

        // Check with null barcode
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.getProductTypeByBarCode(null);
        });

        // Check with empty barcode
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.getProductTypeByBarCode("");
        });

        // Check with invalid barcode
        assertThrows(InvalidProductCodeException.class, () -> {
            ezShop.getProductTypeByBarCode("B4RC0D3");
        });

        // Check with valid barcode missing in the DB
        assertNull(ezShop.getProductTypeByBarCode("7293829484929"));

        // Check authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertNotNull(ezShop.getProductTypeByBarCode("1345334543427"));

        // Check authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.getProductTypeByBarCode("B4RC0D3");
        });

        // Check authorization when no user is logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.getProductTypeByBarCode("B4RC0D3");
        });
    }

    @Test
    public void testGetProductTypesByDescription() throws UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        this.addSomeProductToTest();

        // Check with empty description
        assertEquals(0, ezShop.getProductTypesByDescription("").size());

        // Check with null description (which should be treated as an empty string)
        assertEquals(0, ezShop.getProductTypesByDescription(null).size());

        // Check with existing description
        assertEquals(2, ezShop.getProductTypesByDescription("Description").size());

        // Check authorization for ShopManager
        ezShop.logout();
        ezShop.login("manager", "manager");
        assertEquals(2, ezShop.getProductTypesByDescription("Description").size());

        // Check authorization for Cashier
        ezShop.logout();
        ezShop.login("cashier", "cashier");
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.getProductTypesByDescription("Description");
        });

        // Check authorization when no user is logged in
        ezShop.logout();
        assertThrows(UnauthorizedException.class, () -> {
            ezShop.getProductTypesByDescription("Description");
        });
    }
}
