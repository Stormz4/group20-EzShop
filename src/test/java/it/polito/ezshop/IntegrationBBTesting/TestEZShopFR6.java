package it.polito.ezshop.IntegrationBBTesting;


import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class TestEZShopFR6 {
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
        assertEquals("OPENED", ez.getSaleTransactionById(s).getStatus());
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
    public void testDeleteProductFromSale() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPaymentException {
        EZShop ez = new EZShop();

        try {
            ez.endSaleTransaction(1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.deleteProductFromSale(0, "2345344543423", 2);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.deleteProductFromSale(-1, "2345344543423", 2);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.deleteProductFromSale(null, "2345344543423", 2);
        });

        assertThrows(InvalidProductCodeException.class, () -> {
            ez.deleteProductFromSale(1, "", 2);   // empty barCode
        });
        assertThrows(InvalidProductCodeException.class, () -> {
            ez.deleteProductFromSale(1, null, 2);   // null barCode
        });
        assertThrows(InvalidProductCodeException.class, () -> {
            ez.deleteProductFromSale(1, "4544334643418", 2);   // random number: invalid barCode
        });

        assertThrows(InvalidQuantityException.class, () -> {
            ez.deleteProductFromSale(1, "2345344543423", -1);
        });

        int q_beforeAinCatalogue = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        int q_beforeBinCatalogue = ez.getProductTypeByBarCode("1155678522411").getQuantity();

        int sid = ez.startSaleTransaction();
        ez.addProductToSale(sid, "2345344543423", 10);
        ez.addProductToSale(sid, "1155678522411", 2);
        ez.addProductToSale(sid, "2177878523417", 6);

        int q_beforeA = 10;
        int q_beforeB = 2;

        boolean ok = ez.deleteProductFromSale(sid, "71756985291145", 2); // product code does not exist in catalogue
        assertFalse(ok);

        ok = ez.deleteProductFromSale(sid, "3155678522419", 2); // product code does not exist in sale transaction but it exists in catalogue
        assertFalse(ok);

        ok = ez.deleteProductFromSale(sid, "2345344543423", 9000);
        assertFalse(ok);


        // correct deleting of a product from sale transaction: 2 over 10 items
        ok = ez.deleteProductFromSale(sid, "2345344543423", 2);
        assertTrue(ok);
        List<TicketEntry> entries = ez.getSaleTransactionById(sid).getEntries().stream()
                .filter(e -> e.getBarCode().equals("2345344543423")).collect(Collectors.toList());
        assertEquals(q_beforeA-2, entries.get(0).getAmount(), 0);
        int q_afterAinCatalogue = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(q_beforeAinCatalogue-q_beforeA+2, q_afterAinCatalogue);

        // correct deleting of a product from sale transaction: exactly 2 over 2 items
        ok = ez.deleteProductFromSale(sid, "1155678522411", 2);
        assertTrue(ok);
        entries = ez.getSaleTransactionById(sid).getEntries().stream()
                .filter(e -> e.getBarCode().equals("1155678522411")).collect(Collectors.toList());
        boolean empty = entries.isEmpty();
        assertTrue(empty);
        int q_afterBinCatalogue = ez.getProductTypeByBarCode("1155678522411").getQuantity();
        assertEquals(q_beforeBinCatalogue-q_beforeB+2, q_afterBinCatalogue);


        ez.endSaleTransaction(sid); // Sale Transaction status: CLOSED
        ok = ez.deleteProductFromSale(sid, "2345344543423", 2);
        assertFalse(ok);

        ez.receiveCashPayment(sid, 500); // Sale Transaction status: PAYED
        ok = ez.deleteProductFromSale(sid, "2345344543423", 2);
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
            ez.applyDiscountRateToProduct(1, "", 0.25);   // empty barCode
        });
        assertThrows(InvalidProductCodeException.class, () -> {
            ez.applyDiscountRateToProduct(1, null, 0.25);   // null barCode
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
        assertEquals("CLOSED", ez.getSaleTransactionById(sid).getStatus());

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
    public void testDeleteSaleTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, InvalidPaymentException, InvalidProductCodeException, InvalidQuantityException {
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

        // check if qty of products has been updated
        String barCode = "2345344543423";
        sid = ez.startSaleTransaction();
        Integer qtyBeforeSale = ez.getProductTypeByBarCode(barCode).getQuantity();
        ez.addProductToSale(sid, barCode, 1);
        ez.endSaleTransaction(sid);
        ok = ez.deleteSaleTransaction(sid);
        assertTrue(ok);
        assertEquals(qtyBeforeSale, ez.getProductTypeByBarCode(barCode).getQuantity());

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
    public void testStartReturnTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, InvalidPaymentException {
        EZShop ez = new EZShop();
        try {
            ez.startReturnTransaction(1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.startReturnTransaction(0);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.startReturnTransaction(-1);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.startReturnTransaction(null);
        });

        int sid = 9999;

        int rid = ez.startReturnTransaction(sid);
        assertEquals(-1, rid, 0);

        sid = ez.startSaleTransaction(); // status: OPENED
        rid = ez.startReturnTransaction(sid);
        assertEquals(-1, rid, 0);

        ez.endSaleTransaction(sid); // status: CLOSED
        rid = ez.startReturnTransaction(sid);
        assertEquals(-1, rid, 0);

        ez.receiveCashPayment(sid, 500); // status: PAYED
        rid = ez.startReturnTransaction(sid);
        assertNotEquals(-1, rid, 0);
        assertEquals("OPENED", ez.getReturnTransactionById(rid).getStatus());
    }

    @Test
    public void testReturnProduct() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPaymentException {
        EZShop ez = new EZShop();
        try {
            ez.returnProduct(1, "2345344543423", 3);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.returnProduct(0, "2345344543423", 3);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.returnProduct(-1, "2345344543423", 3);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.returnProduct(null, "2345344543423", 3);
        });

        assertThrows(InvalidProductCodeException.class, () -> {
            ez.returnProduct(1, "", 3);   // empty barCode
        });
        assertThrows(InvalidProductCodeException.class, () -> {
            ez.returnProduct(1, null, 3);   // null barCode
        });
        assertThrows(InvalidProductCodeException.class, () -> {
            ez.returnProduct(1, "4544334643418", 3);   // random number: invalid barCode
        });

        assertThrows(InvalidQuantityException.class, () -> {
            ez.returnProduct(1, "2345344543423", 0);
        });
        assertThrows(InvalidQuantityException.class, () -> {
            ez.returnProduct(1, "2345344543423", -1);
        });

        int sid = ez.startSaleTransaction();
        ez.addProductToSale(sid, "2345344543423", 10);
        ez.endSaleTransaction(sid);
        ez.receiveCashPayment(sid, 500); // Sale Transaction status: PAYED

        int rid = 5555;
        boolean ok = ez.returnProduct(rid, "2345344543423", 3);
        assertFalse(ok);

        rid = ez.startReturnTransaction(sid);
        ok = ez.returnProduct(rid, "2345344543423", 11);
        assertFalse(ok);

        ok = ez.returnProduct(rid, "5839276671295", 3); // product that does not exist in the catalogue
        assertFalse(ok);

        ok = ez.returnProduct(rid, "1155678522411", 3); // product that does not exist in the sale transaction (but it exists in the catalogue)
        assertFalse(ok);

        double prod_price = ez.getProductTypeByBarCode("2345344543423").getPricePerUnit();
        int q_before = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        ok = ez.returnProduct(rid, "2345344543423", 3);
        assertTrue(ok);
        assertEquals(prod_price*3, ez.getReturnTransactionById(rid).getReturnedValue(), 0.001);
        int q_after = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        assertEquals(q_before, q_after, 0);
    }

    @Test
    public void testEndReturnTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException {
        EZShop ez = new EZShop();
        try {
            ez.endReturnTransaction(1, true);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.endReturnTransaction(0, true);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.endReturnTransaction(-1, true);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.endReturnTransaction(null, true);
        });

        int sid = ez.startSaleTransaction();
        ez.addProductToSale(sid, "2345344543423", 10);
        ez.endSaleTransaction(sid);
        ez.receiveCashPayment(sid, 500); // Sale Transaction status: PAYED

        int rid = 5555;
        boolean ok = ez.endReturnTransaction(rid, true);
        assertFalse(ok);

        double prod_price = ez.getProductTypeByBarCode("2345344543423").getPricePerUnit();
        int q_before = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        double price_before = ez.getSaleTransactionById(sid).getPrice();
        int sale_q_before = -1;
        List<TicketEntry> saleEntries = ez.getSaleTransactionById(sid).getEntries();
        for( TicketEntry t : saleEntries ) {
            if(t.getBarCode().equals("2345344543423"))
            {
                sale_q_before = t.getAmount();
                break;
            }
        }
        rid = ez.startReturnTransaction(sid);
        ez.returnProduct(rid, "2345344543423", 3);
        ok = ez.endReturnTransaction(rid, true);
        assertTrue(ok);
        int q_after = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        double price_after = ez.getSaleTransactionById(sid).getPrice();
        int sale_q_after = -1;
        saleEntries = ez.getSaleTransactionById(sid).getEntries();
        for( TicketEntry t : saleEntries ) {
            if(t.getBarCode().equals("2345344543423"))
            {
                sale_q_after = t.getAmount();
                break;
            }
        }
        assertEquals(q_before+3, q_after, 0);
        assertEquals(price_before-(prod_price*3), price_after, 0.001);
        assertEquals(sale_q_before-3, sale_q_after, 0);
        assertEquals("CLOSED", ez.getReturnTransactionById(rid).getStatus());

        ez.deleteReturnTransaction(rid);
        ok = ez.endReturnTransaction(rid, true);
        assertFalse(ok);

        int sid2 = ez.startSaleTransaction();
        ez.addProductToSale(sid2, "2345344543423", 10);
        ez.endSaleTransaction(sid2);
        ez.receiveCashPayment(sid2, 500); // Sale Transaction status: PAYED
        int rid2 = ez.startReturnTransaction(sid2);
        ok = ez.endReturnTransaction(rid2, false); // testing rollback situation
        assertTrue(ok);
        assertNull(ez.getReturnTransactionById(rid2));
    }

    @Test
    public void testDeleteReturnTransaction() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidTransactionIdException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException {
        EZShop ez = new EZShop();
        try {
            ez.deleteReturnTransaction(1);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("TransactionsTest", "pwd"); // Administrator logged-in

        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.deleteReturnTransaction(0);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.deleteReturnTransaction(-1);
        });
        assertThrows(InvalidTransactionIdException.class, () -> {
            ez.deleteReturnTransaction(null);
        });

        int sid = ez.startSaleTransaction();
        ez.addProductToSale(sid, "2345344543423", 10);
        ez.endSaleTransaction(sid);
        ez.receiveCashPayment(sid, 500); // Sale Transaction status: PAYED

        int rid = 5555;
        boolean ok = ez.deleteReturnTransaction(rid);
        assertFalse(ok);

        double prod_price = ez.getProductTypeByBarCode("2345344543423").getPricePerUnit();
        rid = ez.startReturnTransaction(sid); // status: OPENED
        ez.returnProduct(rid, "2345344543423", 3);
        ok = ez.deleteReturnTransaction(rid);
        assertFalse(ok);
        ez.endReturnTransaction(rid, true); // status: CLOSED
        int q_before = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        double price_before = ez.getSaleTransactionById(sid).getPrice();
        int sale_q_before = -1;
        List<TicketEntry> saleEntries = ez.getSaleTransactionById(sid).getEntries();
        for( TicketEntry t : saleEntries ) {
            if(t.getBarCode().equals("2345344543423"))
            {
                sale_q_before = t.getAmount();
                break;
            }
        }
        ok = ez.deleteReturnTransaction(rid);
        assertTrue(ok);
        int q_after = ez.getProductTypeByBarCode("2345344543423").getQuantity();
        double price_after = ez.getSaleTransactionById(sid).getPrice();
        int sale_q_after = -1;
        saleEntries = ez.getSaleTransactionById(sid).getEntries();
        for( TicketEntry t : saleEntries ) {
            if(t.getBarCode().equals("2345344543423"))
            {
                sale_q_after = t.getAmount();
                break;
            }
        }
        assertEquals(q_before-3, q_after, 0);
        assertEquals(price_before+(prod_price*3), price_after, 0.001);
        assertEquals(sale_q_before+3, sale_q_after, 0);

        // Testing "return false if -the return transaction- has been payed":
        int sid2 = ez.startSaleTransaction();
        ez.addProductToSale(sid2, "2345344543423", 10);
        ez.endSaleTransaction(sid2);
        ez.receiveCashPayment(sid2, 500); // Sale Transaction status: PAYED
        int rid2 = ez.startReturnTransaction(sid2);
        ez.returnCashPayment(rid2); // Return Transaction status: PAYED
        ok = ez.deleteReturnTransaction(rid2);
        assertFalse(ok);

        // Testing "return false if there are some problems with the db":
        int sid3 = ez.startSaleTransaction();
        ez.addProductToSale(sid3, "2345344543423", 10);
        ez.endSaleTransaction(sid3);
        ez.receiveCashPayment(sid3, 500); // Sale Transaction status: PAYED
        int rid3 = ez.startReturnTransaction(sid3);
        shopDB2.deleteTransaction(rid3); // delete return transaction FROM DB in order to generate an error
        ok = ez.deleteReturnTransaction(rid3);
        assertFalse(ok);

        // Testing a particular case: Returning of exactly the sold products
        int sid4 = ez.startSaleTransaction();
        ez.addProductToSale(sid4, "1155678522411", 3); // used to test if we return exactly the same number of products
        ez.endSaleTransaction(sid4);
        ez.receiveCashPayment(sid4, 500); // Sale Transaction status: PAYED
        double prod_price4 = ez.getProductTypeByBarCode("1155678522411").getPricePerUnit();
        int rid4 = ez.startReturnTransaction(sid4); // status: OPENED
        ez.returnProduct(rid4, "1155678522411", 3);
        ez.endReturnTransaction(rid4, true); // status: CLOSED
        int sale_q_before4 = -1;
        List<TicketEntry> saleEntries4 = ez.getSaleTransactionById(sid4).getEntries();
        for( TicketEntry t : saleEntries4 ) {
            if(t.getBarCode().equals("1155678522411"))
            {
                sale_q_before4 = t.getAmount();
                break;
            }
        }
        ez.deleteReturnTransaction(rid4);
        int sale_q_after4 = -1;
        saleEntries4 = ez.getSaleTransactionById(sid4).getEntries();
        for( TicketEntry t : saleEntries4 ) {
            if(t.getBarCode().equals("1155678522411"))
            {
                sale_q_after4 = t.getAmount();
                break;
            }
        }
        assertEquals(sale_q_before4+3, sale_q_after4, 0);
    }

}
