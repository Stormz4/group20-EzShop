package it.polito.ezshop.testChange;

import it.polito.ezshop.data.EZProduct;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.Order;
import it.polito.ezshop.data.SQLiteDB;
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
    String RFID1 = "0000000001";
    String RFID2 = "0000000002";
    String RFID3 = "0000000003";
    String RFID4 = "0000000004";
    String RFID5 = "0000000005";
    String RFID6 = "0000000006";
    String RFID7 = "0000000007";
    String RFID8 = "0000000008";
    String RFID9 = "0000000009";
    String RFID10 = "0000000010";
    String RFID11 = "0000000011";
    String RFID12 = "0000000012";
    String RFID13 = "0000000013";
    String RFID14 = "0000000014";
    String RFID15 = "0000000015";

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
            ez.deleteProductFromSaleRFID(1, "0000000001");
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("RFIDTest", "pwd"); // Administrator logged-in


        // add product to sale
        try {
            ez.addProductToSaleRFID(-1, "0000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSaleRFID(0, "0000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSaleRFID(null, "0000000001");
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
            ez.deleteProductFromSaleRFID(-1, "0000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.deleteProductFromSaleRFID(0, "0000000001");
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.deleteProductFromSaleRFID(null, "0000000001");
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
            ez.deleteProductFromSaleRFID(1, "000001");
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
        boolean addFalse2 = ez.addProductToSaleRFID(s, "0011000001");

        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityNoChange, quantityProd1);

        boolean addTrue1 = ez.addProductToSaleRFID(s, RFID1);

        // The quantity for the Product type should decrease
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityAfter1, quantityProd1);
        assertEquals(ez.getAllProducts().get(Long.parseLong(RFID1)).getSaleID(), s);

        // ************* TEST DELETE PRODUCT FROM SALE RFID ******************

        boolean deleteFalse1 = ez.deleteProductFromSaleRFID(500000000, "0000000001");
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityAfter1, quantityProd1);

        boolean deleteFalse2 = ez.deleteProductFromSaleRFID(s, "0011000001");
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityAfter1, quantityProd1);

        // product is not in the sale transaction
        boolean deleteFalse3 = ez.deleteProductFromSaleRFID(s, "0000000003");
        quantityProd1 = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(quantityAfter1, quantityProd1);

        boolean deleteTrue = ez.deleteProductFromSaleRFID(s, "0000000001");
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
        boolean addFalse3 = ez.addProductToSaleRFID(s2, "0000000001");
        boolean closedDelete = ez.deleteProductFromSaleRFID(s2, "0000000001");

        assertFalse(addFalse3);
        assertFalse(closedDelete);
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
        Integer orderID = ez.issueOrder("2141513141144", quantityOrdered,1.20);
        ez.payOrder(orderID);
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
        // Invalid Order ID
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

        // TODO missing "or is not unique"

        // Check if the quantity is updated after the record order
        HashMap<Long, EZProduct> products = shopDB.selectAllProducts();
        int sizeBefore = products.size();
        ez.payOrder(orderID); // order is now payed
        boolean trueArrival = ez.recordOrderArrivalRFID(orderID, RFID5);
        // now the order should be in state COMPLETED
        int sizeAfter= products.size();
        LinkedList<Order> orders = (LinkedList<Order>) ez.getAllOrders();
        assertTrue(orders.getLast().getStatus().equals("COMPLETED"));

        assertEquals(sizeAfter, sizeBefore + quantityOrdered);
        assertTrue(trueArrival);

        boolean falseArrival = ez.recordOrderArrivalRFID(20000, RFID5);
        assertFalse(falseArrival);

        // TODO Missing test "false it was not in an ORDERED/COMPLETED state"
    }

    @Test
    public void testReturnRFID() throws UnauthorizedException, InvalidTransactionIdException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException, InvalidRFIDException, InvalidPasswordException, InvalidUsernameException {
        assertThrows(UnauthorizedException.class, () -> {
            ez.returnProductRFID(1, "0000000001");
        });

        ez.login("RFIDTest", "pwd"); // Administrator logged-in


        int sid = ez.startSaleTransaction();
        ez.addProductToSaleRFID(sid, "0000000001");
        ez.addProductToSaleRFID(sid, "0000000002");
        ez.addProductToSaleRFID(sid, "0000000005");
        ez.addProductToSaleRFID(sid, "0000000006");
        ez.addProductToSaleRFID(sid, "0000000007");
        ez.addProductToSaleRFID(sid, "0000000008");
        ez.addProductToSaleRFID(sid, "0000000009");
        ez.addProductToSaleRFID(sid, "0000000012");
        ez.endSaleTransaction(sid);
        ez.receiveCashPayment(sid, 1000);

        int sid2 = ez.startSaleTransaction();
        ez.addProductToSaleRFID(sid2, "0000000011");



        // ************* Testing startReturnTransaction ******************
        int rid = ez.startReturnTransaction(sid);
        assertNotEquals(rid, -1, 0);

        int rid2 = ez.startReturnTransaction(sid2);


        // *************   Testing returnProductRFID    ******************
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.returnProductRFID(0, "0000000001");
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.returnProductRFID(-1, "0000000001");
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.returnProductRFID(null, "0000000001");
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
        HashMap<Long, EZProduct> products = shopDB.selectAllProducts();
        EZProduct p = products.get(Long.parseLong("0000000001"));
        assertNotNull(p);
        boolean ok = ez.returnProductRFID(rid, "0000000001");
        assertTrue(ok);
        products = shopDB.selectAllProducts();
        p = products.get(Long.parseLong("0000000001"));
        assertEquals("0000000001", p.getRFID());
        assertEquals(rid, p.getReturnID(), 0);
        assertEquals(sid, p.getSaleID(), 0);
        assertEquals(1, ez.getReturnTransactionById(rid).getEntries().size(), 0);
        int quantityAfter = shopDB.selectAllProductTypes().get(prodTypeId1).getQuantity();
        assertEquals(quantityBefore, quantityAfter, 0); // "This method DOES NOT update the product quantity"

        ok = ez.returnProductRFID(rid, "0000000002");
        assertTrue(ok);
        ok = ez.returnProductRFID(rid, "0000000005");
        assertTrue(ok);
        ok = ez.returnProductRFID(rid, "0000000005"); // returning it second time should not be possible
        assertFalse(ok);
        ok = ez.returnProductRFID(rid, "0000000003"); // item not present in return transaction
        assertFalse(ok);
        ok = ez.returnProductRFID(rid, "0000099999"); // item not present in catalogue
        assertFalse(ok);
        ok = ez.returnProductRFID(9999, "000000001");
        assertFalse(ok);

        ez.returnProductRFID(rid2, "0000000011");


        // *************  Testing endReturnTransaction  ******************
        ok = ez.endReturnTransaction(rid, true);
        assertTrue(ok);
        products = shopDB.selectAllProducts();
        p = products.get(Long.parseLong("0000000002"));
        assertEquals("0000000002", p.getRFID());
        assertEquals(rid, p.getReturnID(), 0);
        assertEquals(defaultID, p.getSaleID(), 0);

        ok = ez.endReturnTransaction(rid2, false); // testing rollback
        assertTrue(ok);
        products = shopDB.selectAllProducts();
        p = products.get(Long.parseLong("0000000011"));
        assertEquals("0000000011", p.getRFID());
        assertEquals(defaultID, p.getReturnID(), 0);
        assertEquals(sid2, p.getSaleID(), 0);


        // ************* Testing deleteReturnTransaction ******************
        ok = ez.deleteReturnTransaction(rid);
        assertTrue(ok);
        products = shopDB.selectAllProducts();
        p = products.get(Long.parseLong("0000000002"));
        assertEquals("0000000002", p.getRFID());
        assertEquals(defaultID, p.getReturnID(), 0);
        assertEquals(sid, p.getSaleID(), 0);

    }
}

