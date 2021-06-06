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

    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, UnauthorizedException {
        ez = new EZShop();
        shopDB.connect();
        shopDB.initDatabase();
        uId=ez.createUser("RFIDTest", "pwd", "Administrator");

        prodTypeId1 = shopDB.insertProductType(2, "89-XY-98", "Test note 1", "Test product 1", "2345344543423", 11.90);
        prodTypeId2 = shopDB.insertProductType(1, "67-TT-54", "Test note 2", "Test product 2", "1155678522411", 5.00);
        prodTypeId3 = shopDB.insertProductType(1, "68-TT-54", "Test note 3", "Test product 3", "2177878523417", 5.00);
        prodTypeId4 = shopDB.insertProductType(10, "69-TT-54", "Test note 4", "Test product 4", "3155678522419", 5.00);
        prodTypeId5 = shopDB.insertProductType(10, "", "Test note 5", "Test product 5", "2141513141144", 5.00);

        shopDB.insertProduct(Long.parseLong("RFID1"), 1);
        shopDB.insertProduct(Long.parseLong("RFID2"), 1);
        shopDB.insertProduct(Long.parseLong("RFID3"), 2);
        shopDB.insertProduct(Long.parseLong("RFID4"), 3);
        shopDB.insertProduct(Long.parseLong("RFID5"), 4);

        // Add some money to the balance
        ez.recordBalanceUpdate(50000);
    }

    @After
    public void teardown(){
        shopDB.deleteUser(uId);


        shopDB.deleteProductType(prodTypeId1);
        shopDB.deleteProductType(prodTypeId2);
        shopDB.deleteProductType(prodTypeId3);
        shopDB.deleteProductType(prodTypeId4);
        shopDB.deleteProductType(prodTypeId5);

        shopDB.deleteProduct(Long.parseLong("RFID1"));
        shopDB.deleteProduct(Long.parseLong("RFID2"));
        shopDB.deleteProduct(Long.parseLong("RFID3"));
        shopDB.deleteProduct(Long.parseLong("RFID4"));
        shopDB.deleteProduct(Long.parseLong("RFID5"));

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
            ez.addProductToSaleRFID(1, "0000000001");
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
        Integer order = ez.issueOrder("2141513141144", quantityOrdered,1.20);
        try {
            ez.recordOrderArrivalRFID(order, RFID5);
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
            ez.recordOrderArrivalRFID(1, "1000000000");
            fail("InvalidRFIDException incoming");
        } catch (InvalidRFIDException e) {
            assertNotNull(e);
        }

        // TODO missing "or is not unique"

        // Check if the quantity is updated after the record order
        HashMap<Long, EZProduct> products = shopDB.selectAllProducts();
        int sizeBefore = products.size();
        ez.payOrder(order); // order is now payed
        boolean trueArrival = ez.recordOrderArrivalRFID(order, RFID5);
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
}

