package it.polito.ezshop.IntegrationBBTesting;


import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class testEZShopFR6 {
    EZShop ez;
    Integer uId;
    private final SQLiteDB shopDB2 = new SQLiteDB();
    Integer created1;

    int p1_id;


    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        ez = new EZShop();
        shopDB2.connect();
        shopDB2.initDatabase();
        created1 = null;
        uId=ez.createUser("TransactionsTest", "pwd", "Administrator");

        p1_id = shopDB2.insertProductType(300, "89-XY-98", "Test note", "Test product", "2345344543423", 11.90);
    }

    @After
    public void teardown(){
        shopDB2.deleteUser(uId);
        if (created1 != null){
            shopDB2.deleteUser(created1);
        }

        shopDB2.deleteProductType(p1_id);

        shopDB2.closeConnection();
    }

    // Sale Transactions
    @Test
    public void testStartSaleTransaction() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        EZShop ez = new EZShop();
        try {
            ez.startSaleTransaction();
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in
        Integer s = ez.startSaleTransaction();
        boolean positive = s >= 0;
        assertTrue(positive);
    }

    @Test
    public void testAddProductToSale() throws InvalidTransactionIdException, InvalidQuantityException, UnauthorizedException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        EZShop ez = new EZShop();
        try {
            ez.addProductToSale(-1, "2345344543423", 5);
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSale(0, "2345344543423", 5);
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSale(null, "2345344543423", 5);
            fail("InvalidTransactionIdException incoming");
        } catch (InvalidTransactionIdException e) {
            assertNotNull(e);
        }

        try {
            ez.addProductToSale(1, "2345344543423", -5);
            fail("InvalidQuantityException incoming");
        } catch (InvalidQuantityException e) {
            assertNotNull(e);
        }

        try {
            ez.addProductToSale(1, "2345344543423", 5);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        try {
            ez.addProductToSale(1, "", -5);
            fail("InvalidProductCodeException incoming");
        } catch (InvalidProductCodeException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSale(1, null, -5);
            fail("InvalidProductCodeException incoming");
        } catch (InvalidProductCodeException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSale(1, "4544334643418", -5); // random number (invalid barCode)
            fail("InvalidProductCodeException incoming");
        } catch (InvalidProductCodeException e) {
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        EZProductType p1 = (EZProductType) ez.getAllProductTypes().stream()
                .filter(p -> p.getBarCode().equals("2345344543423"))
                .collect(Collectors.toList()).get(0);
        int q_before = p1.getQuantity();
        Integer s = ez.startSaleTransaction();
        boolean ok = ez.addProductToSale(s, "2345344543423", 5);
        assertTrue(ok);
        int q_after = p1.getQuantity(); // "...decreasing the temporary amount of product available on the shelves..."
        assertEquals(q_before, q_after-5, 0);

        ok = ez.addProductToSale(s, "9302832103012", 5);
        assertFalse(ok);

        ok = ez.addProductToSale(s, "2345344543423", 9000);
        assertFalse(ok);

        ez.endSaleTransaction(s);

        ok = ez.addProductToSale(s, "2345344543423", 5);
        assertFalse(ok);
    }

    @Test
    public void testApplyDiscountRateToProduct() {

    }

    @Test
    public void testApplyDiscountRateToSale() {

    }

    @Test
    public void testComputePointsForSale() {

    }

    @Test
    public void testEndSaleTransaction() {

    }

    @Test
    public void testDeleteSaleTransaction() {

    }

    @Test
    public void testGetSaleTransaction() {

    }

    // Return Transactions
    @Test
    public void testStartReturnTransaction() {

    }

    @Test
    public void testReturnProduct() {

    }

    @Test
    public void testEndReturnTransaction() {

    }

    @Test
    public void testDeleteReturnTransaction() {

    }

}
