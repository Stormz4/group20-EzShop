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

    int p1_id, p2_id, p3_id, p4_id;


    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        ez = new EZShop();
        shopDB2.connect();
        shopDB2.initDatabase();
        created1 = null;
        uId=ez.createUser("TransactionsTest", "pwd", "Administrator");

        p1_id = shopDB2.insertProductType(300, "89-XY-98", "Test note", "Test product 1", "2345344543423", 11.90);
        p2_id = shopDB2.insertProductType(300, "67-TT-54", "Test note", "Test product 2", "1155678522411", 5.00);
        p3_id = shopDB2.insertProductType(300, "68-TT-54", "Test note", "Test product 3", "2177878523417", 5.00);
        p4_id = shopDB2.insertProductType(300, "69-TT-54", "Test note", "Test product 4", "3155678522419", 5.00);
    }

    @After
    public void teardown(){
        shopDB2.deleteUser(uId);
        if (created1 != null){
            shopDB2.deleteUser(created1);
        }

        shopDB2.deleteProductType(p1_id);
        shopDB2.deleteProductType(p2_id);
        shopDB2.deleteProductType(p3_id);
        shopDB2.deleteProductType(p4_id);

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
    public void testAddProductToSale() throws InvalidTransactionIdException, InvalidQuantityException, UnauthorizedException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidOrderIdException, InvalidLocationException, InvalidProductIdException {
        EZShop ez = new EZShop();
        try {
            ez.addProductToSale(1, "2345344543423", 5);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

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
            ez.addProductToSale(1, "", 5);
            fail("InvalidProductCodeException incoming");
        } catch (InvalidProductCodeException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSale(1, null, 5);
            fail("InvalidProductCodeException incoming");
        } catch (InvalidProductCodeException e) {
            assertNotNull(e);
        }
        try {
            ez.addProductToSale(1, "4544334643418", 5); // random number (invalid barCode)
            fail("InvalidProductCodeException incoming");
        } catch (InvalidProductCodeException e) {
            assertNotNull(e);
        }

        EZProductType p1 = (EZProductType) ez.getAllProductTypes().stream()
                .filter(p -> p.getBarCode().equals("2345344543423"))
                .collect(Collectors.toList()).get(0);
        int q_before = p1.getQuantity();
        Integer s = ez.startSaleTransaction();
        boolean ok = ez.addProductToSale(s, "2345344543423", 5);
        assertTrue(ok);
        int q_after = p1.getQuantity(); // "...decreasing the temporary amount of product available on the shelves..."
        assertEquals(q_before, q_after+5, 0);

        ok = ez.addProductToSale(s, "9302832103012", 5);
        assertFalse(ok);

        ok = ez.addProductToSale(s, "2345344543423", 9000);
        assertFalse(ok);

        ez.endSaleTransaction(s);

        ok = ez.addProductToSale(s, "2345344543423", 5);
        assertFalse(ok);
    }

    @Test
    public void testApplyDiscountRateToProduct() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, InvalidDiscountRateException, InvalidProductCodeException, InvalidQuantityException, InvalidPaymentException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidOrderIdException, InvalidLocationException, InvalidProductIdException {
        EZShop ez = new EZShop();
        try {
            ez.applyDiscountRateToProduct(1, "2345344543423", 0.25);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.applyDiscountRateToProduct(0, "2345344543423", 0.25);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.applyDiscountRateToProduct(-1, "2345344543423", 0.25);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.applyDiscountRateToProduct(null, "2345344543423", 0.25);
        });

        assertThrows(InvalidProductCodeException.class, () -> {
            ez.applyDiscountRateToProduct(1, "", 0.25);   // random number: invalid barCode
        });
        assertThrows(InvalidProductCodeException.class, () -> {
            ez.applyDiscountRateToProduct(1, null, 0.25);   // random number: invalid barCode
        });
        assertThrows(InvalidProductCodeException.class, () -> {
            ez.applyDiscountRateToProduct(1, "4544334643418", 0.25);   // random number: invalid barCode
        });

        assertThrows(InvalidDiscountRateException.class, () -> {
            ez.applyDiscountRateToProduct(1, "2345344543423", -1);
        });
        assertThrows(InvalidDiscountRateException.class, () -> {
            ez.applyDiscountRateToProduct(1, "2345344543423", 1.00);
        });
        assertThrows(InvalidDiscountRateException.class, () -> {
            ez.applyDiscountRateToProduct(1, "2345344543423", 2);
        });

        int sid = ez.startSaleTransaction(); // OPENED sale transaction
        ez.addProductToSale(sid, "2345344543423", 5);

        boolean ok = ez.applyDiscountRateToProduct(sid, "2345344543423", 0.25);
        assertTrue(ok);
        for (TicketEntry t : ez.getSaleTransactionById(sid).getEntries()) {
            if(t.getBarCode().equals("2345344543423")) {
                assertEquals(0.25, t.getDiscountRate(), 0.001);
            }
        }

        ok = ez.applyDiscountRateToProduct(sid, "1155994543411", 0.25); // product code that doesn't exist in the sale transaction
        assertFalse(ok);

        ez.endSaleTransaction(sid); // Sale transaction CLOSED
        ok = ez.applyDiscountRateToProduct(sid, "2345344543423", 0.25);
        assertFalse(ok);

        ez.receiveCashPayment(sid, 500); // Sale transaction PAYED
        ok = ez.applyDiscountRateToProduct(sid, "2345344543423", 0.25);
        assertFalse(ok);
    }

    @Test
    public void testApplyDiscountRateToSale() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, InvalidDiscountRateException, InvalidPaymentException {
        EZShop ez = new EZShop();
        try {
            ez.applyDiscountRateToSale(1, 0.10);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.applyDiscountRateToSale(0, 0.10);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.applyDiscountRateToSale(-1, 0.10);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.applyDiscountRateToSale(null, 0.10);
        });

        assertThrows(InvalidDiscountRateException.class, () -> {
            ez.applyDiscountRateToSale(1, -1);
        });
        assertThrows(InvalidDiscountRateException.class, () -> {
            ez.applyDiscountRateToSale(1,1.00);
        });
        assertThrows(InvalidDiscountRateException.class, () -> {
            ez.applyDiscountRateToSale(1, 2);
        });

        int sid = ez.startSaleTransaction(); // OPENED sale transaction

        boolean ok = ez.applyDiscountRateToSale(sid, 0.80);
        assertTrue(ok);
        assertEquals(0.80, ez.getSaleTransactionById(sid).getDiscountRate(), 0.001);

        ez.endSaleTransaction(sid); // Sale transaction CLOSED
        ok = ez.applyDiscountRateToSale(sid, 0.10);
        assertTrue(ok);
        assertEquals(0.10, ez.getSaleTransactionById(sid).getDiscountRate(), 0.001);

        ez.receiveCashPayment(sid, 500); // Sale transaction PAYED
        ok = ez.applyDiscountRateToSale(sid, 0.10);
        assertFalse(ok);
    }

    @Test
    public void testComputePointsForSale() throws UnauthorizedException, InvalidTransactionIdException, InvalidPasswordException, InvalidUsernameException, InvalidQuantityException, InvalidProductCodeException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidPaymentException, InvalidOrderIdException, InvalidLocationException, InvalidProductIdException {
        EZShop ez = new EZShop();
        try {
            ez.computePointsForSale(1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.computePointsForSale(0);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.computePointsForSale(-1);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.computePointsForSale(null);
        });

        int sid = 9999;

        int x = ez.computePointsForSale(sid);
        assertEquals(-1, x, 0);

        sid = ez.startSaleTransaction(); // OPENED sale transaction

        x = ez.computePointsForSale(sid);
        assertEquals(0, x, 0);

        ez.addProductToSale(sid, "1155678522411", 3);
        x = ez.computePointsForSale(sid);
        assertEquals(1, x, 0);

        ez.addProductToSale(sid, "2177878523417", 2);
        x = ez.computePointsForSale(sid);
        assertEquals(2, x, 0);

        ez.addProductToSale(sid, "3155678522419", 1);
        x = ez.computePointsForSale(sid);
        assertEquals(3, x, 0);

        ez.endSaleTransaction(sid); // Sale transaction CLOSED

        x = ez.computePointsForSale(sid);
        assertEquals(3, x, 0);

        ez.receiveCashPayment(sid, 500); // Sale transaction PAYED

        x = ez.computePointsForSale(sid);
        assertEquals(3, x, 0);
    }

    @Test
    public void testEndSaleTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException {
        EZShop ez = new EZShop();
        try {
            ez.endSaleTransaction(1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.endSaleTransaction(0);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.endSaleTransaction(-1);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.endSaleTransaction(null);
        });

        int sid = 9999;

        boolean ok = ez.endSaleTransaction(sid);
        assertFalse(ok);

        sid = ez.startSaleTransaction(); // Status: OPENED
        ok = ez.endSaleTransaction(sid);
        assertTrue(ok);

        // Status: CLOSED
        ok = ez.endSaleTransaction(sid);
        assertFalse(ok);

        // TEST ON "return false if there was a problem in registering the data"
        int sid2 = ez.startSaleTransaction();
        ez.endSaleTransaction(sid2);
        ez.deleteSaleTransaction(sid2); // removing the sale transaction in order to generate an error
        ok = ez.endSaleTransaction(sid2);
        assertFalse(ok);
    }

    @Test
    public void testDeleteSaleTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, InvalidPaymentException {
        EZShop ez = new EZShop();
        try {
            ez.deleteSaleTransaction(1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.deleteSaleTransaction(0);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.deleteSaleTransaction(-1);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.deleteSaleTransaction(null);
        });

        int sid = 9999;

        boolean ok = ez.deleteSaleTransaction(sid);
        assertFalse(ok);

        sid = ez.startSaleTransaction(); // Status: OPENED
        ez.endSaleTransaction(sid); // Status: CLOSED
        ok = ez.deleteSaleTransaction(sid);
        assertTrue(ok);

        sid = ez.startSaleTransaction(); // Status: OPENED
        ez.endSaleTransaction(sid); // Status: CLOSED
        ez.receiveCashPayment(sid, 500); // Status: PAYED
        ok = ez.deleteSaleTransaction(sid);
        assertFalse(ok);
    }

    @Test
    public void testGetSaleTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, InvalidPaymentException {
        EZShop ez = new EZShop();
        try {
            ez.getSaleTransaction(1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.getSaleTransaction(0);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.getSaleTransaction(-1);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.getSaleTransaction(null);
        });

        int sid = ez.startSaleTransaction(); // status: OPENED
        EZSaleTransaction sale = ez.getSaleTransactionById(sid);

        EZSaleTransaction s = (EZSaleTransaction) ez.getSaleTransaction(sid);
        assertNull(s);

        ez.endSaleTransaction(sid); // status: CLOSED

        s = (EZSaleTransaction) ez.getSaleTransaction(sid);
        assertEquals(sale, s);

        ez.receiveCashPayment(sid, 500); // status: PAYED
        assertEquals(sale, s);
    }

    // Return Transactions
    @Test
    public void testStartReturnTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException {
        EZShop ez = new EZShop();
        try {
            ez.startReturnTransaction(1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in


    }

    @Test
    public void testReturnProduct() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException {
        EZShop ez = new EZShop();
        try {
            ez.returnProduct(1, "2345344543423", 3);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in


    }

    @Test
    public void testEndReturnTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException {
        EZShop ez = new EZShop();
        try {
            ez.endReturnTransaction(1, true);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in


    }

    @Test
    public void testDeleteReturnTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException {
        EZShop ez = new EZShop();
        try {
            ez.deleteReturnTransaction(1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in


    }

}
