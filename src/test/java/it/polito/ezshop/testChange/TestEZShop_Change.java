package it.polito.ezshop.testChange;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.lang.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static it.polito.ezshop.data.SQLiteDB.defaultID;
import static org.junit.Assert.*;

public class TestEZShop_Change {
    private EZShop ez;
    private SQLiteDB shopDB;
    private Integer uId;

    int prodTypeId1, prodTypeId2, prodTypeId3, prodTypeId4, prodTypeId5;
    String RFID1 = "000000000001";
    String RFID2 = "000000000002";
    String RFID3 = "000000000003";
    String RFID4 = "000000000004";
    String RFID5 = "000000000005";
    String RFID6 = "000000000006";
    String RFID7 = "000000000007";
    String RFID8 = "000000000008";
    String RFID9 = "000000000009";
    String RFID10 = "000000000010";
    String RFID11 = "000000000011";
    String RFID12 = "000000000012";
    String RFID13 = "000000000013";
    String RFID14 = "000000000014";
    String RFID15 = "000000000015";

    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, UnauthorizedException {
        shopDB = new SQLiteDB();
        shopDB.connect();
        shopDB.initDatabase();
        shopDB.clearDatabase();

        prodTypeId1 = shopDB.insertProductType(8, "89-XY-98", "Test note 1", "Test product 1", "2345344543423", 11.90);
        prodTypeId2 = shopDB.insertProductType(3, "67-TT-54", "Test note 2", "Test product 2", "1155678522411", 5.00);
        prodTypeId3 = shopDB.insertProductType(2, "68-TT-54", "Test note 3", "Test product 3", "2177878523417", 5.00);
        prodTypeId4 = shopDB.insertProductType(1, "69-TT-54", "Test note 4", "Test product 4", "3155678522419", 5.00);
        prodTypeId5 = shopDB.insertProductType(1, "", "Test note 5", "Test product 5", "2141513141144", 5.00);

        shopDB.insertProduct(Long.parseLong(RFID1), 1, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID2), 1, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID3), 2, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID4), 3, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID5), 4, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID6), 1, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID7), 1, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID8), 1, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID9), 1, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID10), 1, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID11), 1, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID12), 2, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID13), 2, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID14), 3, defaultID, defaultID);
        shopDB.insertProduct(Long.parseLong(RFID15), 5, defaultID, defaultID);

        ez = new EZShop();
        uId=ez.createUser("RFIDTest", "pwd", "Administrator");

        // Add some money to the balance
        ez.login("RFIDTest", "pwd");
        ez.recordBalanceUpdate(50000);
        ez.logout();
    }

    @After
    public void teardown(){
        shopDB.deleteUser(uId);


        shopDB.deleteProductType(prodTypeId1);
        shopDB.deleteProductType(prodTypeId2);
        shopDB.deleteProductType(prodTypeId3);
        shopDB.deleteProductType(prodTypeId4);
        shopDB.deleteProductType(prodTypeId5);

        shopDB.deleteProduct(Long.parseLong(RFID1));
        shopDB.deleteProduct(Long.parseLong(RFID2));
        shopDB.deleteProduct(Long.parseLong(RFID3));
        shopDB.deleteProduct(Long.parseLong(RFID4));
        shopDB.deleteProduct(Long.parseLong(RFID5));

        shopDB.closeConnection();
    }

    /*When a product is sold an RFID reader reads the RFID, no bar code reader is used. From the RFID
    the application retrieves the product type of the product, and all related information (like the price).
    The sale transaction records each product sold.
    see addProductToSaleRFID()   on API
    */
    /**
     * This method adds a product to a sale transaction receiving  its RFID, decreasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @return  true if the operation is successful
     *          false   if the RFID does not exist,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidRFIDException if the RFID code is empty, null or invalid
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */

    /*
    When a product is returned, its RFID is read, the product is re inserted in the inventory.
    see functions deleteProductFromSaleRFID(),

     * This method deletes a product from a sale transaction , receiving its RFID, increasing the temporary amount of product available on the
     * shelves for other customers.
     * It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
     *
     * @param transactionId the id of the Sale transaction
     * @param RFID the RFID of the product to be deleted
     *
     * @return  true if the operation is successful
     *          false   if the product code does not exist,
     *                  if the transaction id does not identify a started and open transaction.
     *
     * @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
     * @throws InvalidRFIDException if the RFID is empty, null or invalid
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     */

    @Test
    public void testSaleRFID() throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidProductCodeException {

        // TODO check what to do with invalidquantityexception, when it should be thrown. API doesn't list it
        try {
            ez.addProductToSaleRFID(1, RFID1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }
        try {
            ez.deleteProductFromSaleRFID(1, "000000000001");
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("RFIDTest", "pwd"); // Administrator logged-in


        // add product to sale
        try {
            ez.addProductToSaleRFID(-1, "000000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSaleRFID(0, "000000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSaleRFID(null, "000000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSaleRFID(1, null);
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }

        try {
            ez.addProductToSaleRFID(1, "");
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }

        try {
            ez.addProductToSaleRFID(1, "000001");
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }

        // delete product from sale

        try {
            ez.deleteProductFromSaleRFID(-1, "000000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.deleteProductFromSaleRFID(0, "000000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.deleteProductFromSaleRFID(null, "000000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.deleteProductFromSaleRFID(1, null);
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }

        try {
            ez.deleteProductFromSaleRFID(1, "");
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }

        try {
            ez.deleteProductFromSaleRFID(1, "00000001");
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }


        Integer s = ez.startSaleTransaction();

        // ************* TEST ADD PRODUCT TO SALE RFID ******************
        ez.loadDataFromDB();
        int quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        int quantityAfter1 = quantityProd1-1;
        int quantityNoChange = quantityProd1;

        // Transaction not present in the DB
        boolean addFalse1 = ez.addProductToSaleRFID(50000, RFID1);

        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        // Quantity should not decrease
        assertEquals(quantityNoChange, quantityProd1);
        // RFID not present
        boolean addFalse2 = ez.addProductToSaleRFID(s, "001100000001");

        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityNoChange, quantityProd1);

        boolean addTrue1 = ez.addProductToSaleRFID(s, RFID1);

        // The quantity for the Product type should decrease
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityAfter1, quantityProd1);
        assertEquals(ez.getAllProducts().get(Long.parseLong(RFID1)).getSaleID(), s);

        // ************* TEST DELETE PRODUCT FROM SALE RFID ******************

        boolean deleteFalse1 = ez.deleteProductFromSaleRFID(500000000, "000000000001");
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityAfter1, quantityProd1);

        boolean deleteFalse2 = ez.deleteProductFromSaleRFID(s, "001100000001");
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityAfter1, quantityProd1);

        // product is not in the sale transaction
        boolean deleteFalse3 = ez.deleteProductFromSaleRFID(s, "000000000003");
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityAfter1, quantityProd1);

        boolean addTrue2 = ez.addProductToSaleRFID(s,RFID2);
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertTrue(addTrue2);
        assertEquals(quantityAfter1-1, quantityProd1);

        boolean deleteTrue = ez.deleteProductFromSaleRFID(s, "000000000001");
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityAfter1, quantityProd1);

        boolean deleteTrue2 = ez.deleteProductFromSaleRFID(s, RFID2);
        assertTrue(deleteTrue2);
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityNoChange, quantityProd1);
        assertEquals(ez.getAllProducts().get(Long.parseLong(RFID1)).getSaleID(), Integer.valueOf(defaultID));


        ez.endSaleTransaction(s);

        assertTrue(addTrue1);
        assertFalse(addFalse1);
        assertFalse(addFalse2);
        assertFalse(deleteFalse1);
        assertFalse(deleteFalse2);
        assertFalse(deleteFalse3);
        assertTrue(deleteTrue);
        Integer s2 = ez.startSaleTransaction();
        ez.endSaleTransaction(s2);

        // Transaction closed
        boolean addFalse3 = ez.addProductToSaleRFID(s2, "000000000001");
        boolean closedDelete = ez.deleteProductFromSaleRFID(s2, "000000000001");

        assertFalse(addFalse3);
        assertFalse(closedDelete);
    }

    /*
     * Testing Scenario 6.8 (until point 11)
     */
    @Test
    public void testSaleRFIDScenario() throws UnauthorizedException, InvalidRFIDException, InvalidQuantityException, InvalidTransactionIdException, InvalidPasswordException, InvalidUsernameException {

        boolean result;
        ez.login("RFIDTest", "pwd"); // Administrator logged-in
        Integer saleID = ez.startSaleTransaction();
        result = ez.addProductToSaleRFID(saleID, RFID1);
        Integer itemProdTypeID = ez.getAllProducts().get(Long.parseLong(RFID1)).getProdTypeID();
        EZProductType itemProdType = (EZProductType) ez.getAllProductTypes().stream().filter(p -> p.getId().equals(itemProdTypeID)).findFirst().orElse(null);
        assertNotNull(itemProdType);

        // check saleID, returnID and function result (successful or not)
        assertTrue(result);
        assertEquals(saleID, ez.getAllProducts().get(Long.parseLong(RFID1)).getSaleID());
        assertEquals(Integer.valueOf(defaultID), ez.getAllProducts().get(Long.parseLong(RFID1)).getReturnID());

        result = ez.endSaleTransaction(saleID);
        assertTrue(result);
        // check the content of the ticketEntry (barCode, amount and price)
        List<TicketEntry> entries = ez.getSaleTransaction(saleID).getEntries();
        assertEquals(1, entries.size());
        EZTicketEntry itemEntry = (EZTicketEntry) ez.getSaleTransaction(saleID).getEntries().stream().filter(e -> e.getBarCode().equals(itemProdType.getBarCode())).findFirst().orElse(null);
        assertNotNull(itemEntry);
        assertEquals(itemProdType.getBarCode(), itemEntry.getBarCode());
        assertEquals(1, itemEntry.getAmount());
        assertEquals(itemProdType.getPricePerUnit(), itemEntry.getPricePerUnit(), 0.0);

        // check sale Price
        double expected_price = itemProdType.getPricePerUnit();
        assertEquals(expected_price, ez.getSaleTransaction(saleID).getPrice(), 0.0);
    }

    /*
     * @return  true if the operation was successful
     *          false if the order does not exist or if it was not in an ORDERED/COMPLETED state
     *
     * @throws InvalidOrderIdException if the order id is less than or equal to 0 or if it is null.
     * @throws InvalidLocationException if the ordered product type has not an assigned location.
     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
     * @throws InvalidRFIDException if the RFID has invalid format or is not unique
     */
    @Test
    public void testOrderRFID() throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException, InvalidRFIDException, InvalidPasswordException, InvalidUsernameException, InvalidQuantityException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        try {
            ez.recordOrderArrivalRFID(1, RFID5);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("RFIDTest", "pwd"); // Administrator logged-in


        int quantityOrdered = 10;
        // Create an order for product 5, which doesn't have a location yet
        Integer orderID = ez.payOrderFor("2141513141144", quantityOrdered,1.20);

        try {
            ez.recordOrderArrivalRFID(orderID, RFID5);
            fail("InvalidLocationException incoming");
        } catch (InvalidLocationException e) {
            assertNotNull(e);
        }

        // Registering a position
        ez.updatePosition(prodTypeId5, "15-ZZ-50");

        // Invalid Order ID
        try {
            ez.recordOrderArrivalRFID(-1, RFID5);
            fail("InvalidOrderIdException incoming");
        } catch (InvalidOrderIdException e) {
            assertNotNull(e);
        }
        try {
            ez.recordOrderArrivalRFID(0, RFID5);
            fail("InvalidOrderIdException incoming");
        } catch (InvalidOrderIdException e) {
            assertNotNull(e);
        }
        try {
            ez.recordOrderArrivalRFID(null, RFID5);
            fail("InvalidOrderIdException incoming");
        } catch (InvalidOrderIdException e) {
            assertNotNull(e);
        }

        // Invalid RFID
        try {
            ez.recordOrderArrivalRFID(1, null);
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }
        try {
            ez.recordOrderArrivalRFID(1, "02");
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }
        try {
            ez.recordOrderArrivalRFID(1, RFID3);
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }

        // Test proper recordOrderArrival
        int sizeBefore = shopDB.selectAllProducts().size();
        assertTrue( ez.recordOrderArrivalRFID(orderID, "000000000020") );

        // Check if the quantity is updated after the record order
        int sizeAfter = shopDB.selectAllProducts().size();
        assertEquals(sizeAfter, sizeBefore + quantityOrdered);

        // Check that order's status was updated as expected
        LinkedList<Order> orders = (LinkedList<Order>) ez.getAllOrders();
        assertEquals("COMPLETED", orders.getLast().getStatus());

        // Issue and pay a new order, than record its arrival passing already used RFID
        Integer newOrderID = ez.payOrderFor("2141513141144", 5, 1.50);
        assertThrows(InvalidRFIDException.class, () -> {
            ez.recordOrderArrivalRFID(newOrderID, RFID5);
        });

        boolean falseArrival = ez.recordOrderArrivalRFID(20000, RFID5);
        assertFalse(falseArrival);

        // Test "false it was not in an ORDERED/COMPLETED state"
        Integer unpayedOrderID = ez.issueOrder("2141513141144", 3,1.20);
        assertFalse(ez.recordOrderArrival(unpayedOrderID));
    }

    /*
     * Testing Scenario 3.5
     */
    @Test
    public void testOrderRFIDScenario() throws UnauthorizedException, InvalidQuantityException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidOrderIdException, InvalidRFIDException, InvalidLocationException, InvalidPasswordException, InvalidUsernameException {

        ez.login("RFIDTest", "pwd"); // Administrator logged-in

        Integer itemProdTypeID = ez.getAllProducts().get(Long.parseLong(RFID1)).getProdTypeID();
        EZProductType itemProdType = (EZProductType) ez.getAllProductTypes().stream().filter(p -> p.getId().equals(itemProdTypeID)).findFirst().orElse(null);
        assertNotNull(itemProdType);

        Integer prodQtyBefore = itemProdType.getQuantity();
        String firstNewRFID = "000000000016";
        String secondNewRFID = "000000000017";
        Integer orderID = ez.issueOrder(itemProdType.getBarCode(), 2, 5.0);
        assertTrue(ez.payOrder(orderID));
        assertTrue(ez.recordOrderArrivalRFID(orderID, firstNewRFID));
        Integer prodQtyAfter = itemProdType.getQuantity();
        // check if both the new Products have been added
        assertTrue(ez.getAllProducts().containsKey(Long.parseLong(firstNewRFID)));
        assertTrue(ez.getAllProducts().containsKey(Long.parseLong(secondNewRFID)));

        // check the updated qty of their ProdType
        assertEquals(prodQtyBefore + 2, (int) prodQtyAfter);

        EZProduct firstNewProduct = ez.getAllProducts().get(Long.parseLong(firstNewRFID));
        EZProduct secondNewProduct = ez.getAllProducts().get(Long.parseLong(secondNewRFID));
        // check their saleID, returnID and ProdTypeID
        assertEquals(Integer.valueOf(defaultID), firstNewProduct.getSaleID());
        assertEquals(Integer.valueOf(defaultID), firstNewProduct.getReturnID());
        assertEquals(Integer.valueOf(defaultID), secondNewProduct.getSaleID());
        assertEquals(Integer.valueOf(defaultID), secondNewProduct.getReturnID());
        assertEquals(itemProdTypeID, firstNewProduct.getProdTypeID());
        assertEquals(itemProdTypeID, secondNewProduct.getProdTypeID());
    }

    @Test
    public void testReturnRFID() throws UnauthorizedException, InvalidTransactionIdException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException, InvalidRFIDException, InvalidPasswordException, InvalidUsernameException {
        assertThrows(UnauthorizedException.class, () -> {
            ez.returnProductRFID(1, "0000000001");
        });

        ez.login("RFIDTest", "pwd"); // Administrator logged-in


        int sid = ez.startSaleTransaction();
        ez.addProductToSaleRFID(sid, "000000000001");
        ez.addProductToSaleRFID(sid, "000000000002");
        ez.addProductToSaleRFID(sid, "000000000005");
        ez.addProductToSaleRFID(sid, "000000000006");
        ez.addProductToSaleRFID(sid, "000000000007");
        ez.addProductToSaleRFID(sid, "000000000008");
        ez.addProductToSaleRFID(sid, "000000000009");
        ez.addProductToSaleRFID(sid, "000000000012");
        ez.endSaleTransaction(sid);
        ez.receiveCashPayment(sid, 1000);

        int sid2 = ez.startSaleTransaction();
        ez.addProductToSaleRFID(sid2, "000000000011");
        ez.endSaleTransaction(sid2);
        ez.receiveCashPayment(sid2, 1000);


        // ************* Testing startReturnTransaction ******************
        int rid = ez.startReturnTransaction(sid);
        assertNotEquals(rid, -1, 0);


        // *************   Testing returnProductRFID    ******************
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.returnProductRFID(0, "000000000001");
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.returnProductRFID(-1, "000000000001");
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.returnProductRFID(null, "000000000001");
        });

        assertThrows(InvalidRFIDException.class, () -> {
            ez.returnProductRFID(1, "");
        });
        assertThrows(InvalidRFIDException.class, () -> {
            ez.returnProductRFID(1, null);
        });
        assertThrows(InvalidRFIDException.class, () -> {
            ez.returnProductRFID(1, "0001");
        });
        assertThrows(InvalidRFIDException.class, () -> {
            ez.returnProductRFID(1, "000000000000000001");
        });
        assertThrows(InvalidRFIDException.class, () -> {
            ez.returnProductRFID(1, "A1B2C3D4E5");
        });

        int quantityBefore = shopDB.selectAllProductTypes().get(prodTypeId1).getQuantity();
        HashMap<Long, EZProduct> productsDB = shopDB.selectAllProducts();
        EZProduct p = productsDB.get(Long.parseLong("000000000001"));
        assertNotNull(p);
        HashMap<Long, EZProduct> products = ez.getAllProducts();
        p = products.get(Long.parseLong("000000000001"));
        assertNotNull(p);
        boolean ok = ez.returnProductRFID(rid, "000000000001");
        assertTrue(ok);
        productsDB = shopDB.selectAllProducts();
        p = productsDB.get(Long.parseLong("000000000001"));
        assertEquals(rid, p.getReturnID(), 0);
        assertEquals(defaultID, p.getSaleID(), 0);
        assertEquals("000000000001", p.getRFID());
        assertEquals(1, ez.getReturnTransactionById(rid).getEntries().size(), 0);
        products = ez.getAllProducts();
        p = products.get(Long.parseLong("000000000001"));
        assertEquals(rid, p.getReturnID(), 0);
        assertEquals(defaultID, p.getSaleID(), 0);
        assertEquals("000000000001", p.getRFID());
        int quantityAfter = shopDB.selectAllProductTypes().get(prodTypeId1).getQuantity();
        assertEquals(quantityBefore, quantityAfter, 0); // "This method DOES NOT update the product quantity"

        ok = ez.returnProductRFID(rid, "000000000002");
        assertTrue(ok);
        ok = ez.returnProductRFID(rid, "000000000005");
        assertTrue(ok);
        ok = ez.returnProductRFID(rid, "000000000005"); // returning it second time should not be possible
        assertFalse(ok);
        ok = ez.returnProductRFID(rid, "000000000003"); // item not present in return transaction
        assertFalse(ok);
        ok = ez.returnProductRFID(rid, "000000099999"); // item not present in catalogue
        assertFalse(ok);
        ok = ez.returnProductRFID(9999, "000000000001");
        assertFalse(ok);

        // *************  Testing endReturnTransaction  ******************
        ok = ez.endReturnTransaction(rid, true);
        assertTrue(ok);
        products = shopDB.selectAllProducts();
        p = products.get(Long.parseLong("000000000002"));
        assertEquals("000000000002", p.getRFID());
        assertEquals(rid, p.getReturnID(), 0);
        assertEquals(defaultID, p.getSaleID(), 0);

        // Another test for testing rollback:
        int rid2 = ez.startReturnTransaction(sid2);
        ez.returnProductRFID(rid2, "000000000011");
        ok = ez.endReturnTransaction(rid2, false); // testing rollback
        assertTrue(ok);
        products = shopDB.selectAllProducts();
        p = products.get(Long.parseLong("000000000011"));
        assertEquals("000000000011", p.getRFID());
        assertEquals(defaultID, p.getReturnID(), 0);
        assertEquals(sid2, p.getSaleID(), 0);

        // ************* Testing deleteReturnTransaction ******************
        ok = ez.deleteReturnTransaction(rid);
        assertTrue(ok);
        productsDB = shopDB.selectAllProducts();
        p = productsDB.get(Long.parseLong("000000000002"));
        assertEquals("000000000002", p.getRFID());
        assertEquals(defaultID, p.getReturnID(), 0);
        assertEquals(sid, p.getSaleID(), 0);
        products = ez.getAllProducts();
        p = products.get(Long.parseLong("000000000002"));
        assertEquals("000000000002", p.getRFID());
        assertEquals(defaultID, p.getReturnID(), 0);
        assertEquals(sid, p.getSaleID(), 0);
    }

    /*
     * Testing Scenario 8.4
     */
    @Test
    public void testReturnRFIDScenario() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidRFIDException, InvalidQuantityException, InvalidTransactionIdException, InvalidPaymentException {
        ez.login("RFIDTest", "pwd"); // Administrator logged-in

        Integer itemProdTypeID = ez.getAllProducts().get(Long.parseLong(RFID1)).getProdTypeID();
        EZProductType itemProdType = (EZProductType) ez.getAllProductTypes().stream().filter(p -> p.getId().equals(itemProdTypeID)).findFirst().orElse(null);
        assertNotNull(itemProdType);

        // Create dummy saleTransaction for the returnTransaction to test
        Integer saleID = ez.startSaleTransaction();
        // Adding a product with given RFID to the sale
        assertTrue(ez.addProductToSaleRFID(saleID, RFID1));
        // closing and paying the transaction
        assertTrue(ez.endSaleTransaction(saleID));
        assertTrue(ez.receiveCashPayment(saleID, 50000) > 0);
        // starting the returnTransaction (the Scenario begins here)
        Integer returnID = ez.startReturnTransaction(saleID);
        // inserting product RFID
        assertTrue(ez.returnProductRFID(returnID, RFID1));
        assertFalse(ez.returnProductRFID(returnID, RFID2));
        // closing return transaction (Payment not considered in the Scenario)
        assertTrue(ez.endReturnTransaction(returnID, true));
        // check returnID and saleID of item
        assertEquals(returnID, ez.getAllProducts().get(Long.parseLong(RFID1)).getReturnID());
        assertEquals(defaultID, (int) ez.getAllProducts().get(Long.parseLong(RFID1)).getSaleID());
        // check returnTransaction list in saleTransaction
        assertEquals(1, ez.getSaleTransactionById(saleID).getReturns().size());
        assertEquals(returnID, ez.getSaleTransactionById(saleID).getReturns().get(0).getReturnId());
        assertEquals(itemProdType.getPricePerUnit(), ez.getSaleTransactionById(saleID).getReturns().get(0).getReturnedValue(), 0.0);
        assertEquals(1, ez.getSaleTransactionById(saleID).getReturns().get(0).getEntries().size());
        assertEquals(saleID, ez.getSaleTransactionById(saleID).getReturns().get(0).getSaleTransactionId());
    }
}

