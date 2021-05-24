package it.polito.ezshop.IntegrationBBTesting;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SQLiteDB;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEZShopFR7 {

    EZShop ez;
    Integer uId, productID, saleTransactionID, returnTransactionID;
    String barCode = "1234567890128";
    private final SQLiteDB shopDB2 = new SQLiteDB();

    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        ez = new EZShop();
        shopDB2.connect();
        shopDB2.initDatabase();
        saleTransactionID = null;
        returnTransactionID = null;
        uId=ez.createUser("testUser00", "pwd", "Administrator");
        ez.login("testUser00", "pwd");
        productID = ez.createProductType("Vic3", barCode, 12.5, "By Paradox"); // create dummy product
        ez.updateQuantity(ez.getProductTypeByBarCode(barCode).getId(), 50);
    }

    @After
    public void teardown() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidProductIdException, InvalidTransactionIdException, InvalidProductCodeException {
        ez.login("testUser00", "pwd");
        if (returnTransactionID != null) ez.deleteReturnTransaction(returnTransactionID);
        if (saleTransactionID != null) ez.deleteSaleTransaction(saleTransactionID);
        ez.deleteProductType(ez.getProductTypeByBarCode(barCode).getId());
        ez.logout();
        if (uId != null){
            shopDB2.deleteUser(uId);
        }
        shopDB2.closeConnection();
    }

    // TODO le delete ripristinano il DB (aggiornare i test di conseguenza)
    /* All the following test methods need to define a saleTransaction or a returnTransaction first
     * So, every following test will include the first part (without payment) either Scenario 6.1 (if a saleTransaction is needed)
     * or Scenario 8.1 (if a returnTransaction is needed)
     */
    /**
     * This test method covers Scenario 6.1 + Scenario 7.1
     */
    @Test
    public void testReceiveCreditCardPayment() throws UnauthorizedException, InvalidProductCodeException, InvalidQuantityException, InvalidTransactionIdException, InvalidCreditCardException, InvalidProductIdException {
        String creditCard = "4485370086510891";

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle Payment
        assertTrue(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
        ez.logout();
        // Since CreditCard Payment System is fake (Credit cards are in a .txt file), we do not test whether the amount of money in the Credit Card has changed
    }

    /**
     * This test method covers Scenario 6.1 + Scenario 7.2
     */
    @Test
    public void testInvalidCardPayment() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidCreditCardException, InvalidProductIdException {
        String creditCard = "1261924021131027"; //valid according to Luhn's algo, but not registered

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle payment
        assertFalse(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
        ez.logout();
    }

    /**
     * This test method covers Scenario 6.1 + Scenario 7.3
     */
    @Test
    public void testInsufficientCreditPayment() throws InvalidCreditCardException, InvalidTransactionIdException, UnauthorizedException, InvalidQuantityException, InvalidProductCodeException, InvalidProductIdException {
        String creditCard = "5100293991053009"; //present in the CreditCard file, but it has insufficient credit

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle payment
        assertFalse(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
        ez.logout();
    }

    /**
     * This test method covers Scenario 6.1 + Scenario 7.4
     */
    @Test
    public void testReceiveCashPayment() throws InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidQuantityException, InvalidProductCodeException, InvalidProductIdException {

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle payment
        assertTrue(ez.receiveCashPayment(saleTransactionID, 50.0) > 0);
        ez.logout();
    }

    /** This test method checks whether error return values or exception are raised as expected
    */
    @Test
    public void testAllCasesReceive() throws UnauthorizedException, InvalidTransactionIdException, InvalidCreditCardException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException, InvalidProductIdException {
        String creditCard = "4485370086510891";

        // Test receiveCreditCardPayment

        // Create a dummy SaleTransaction and then delete it
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        ez.endSaleTransaction(saleTransactionID);
        Integer finalSaleTransactionID = saleTransactionID;
        assertThrows(InvalidCreditCardException.class, () -> ez.receiveCreditCardPayment(finalSaleTransactionID, null));
        ez.deleteSaleTransaction(saleTransactionID);

        // Try to pay using a Credit Card: it should not be possible
        assertFalse(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
        assertThrows(InvalidTransactionIdException.class, () -> ez.receiveCreditCardPayment(-1, creditCard));

        // Test receiveCashPayment

        // Create a dummy saleTransaction to pay for (cash)
        assertFalse(ez.receiveCashPayment(saleTransactionID, 25) != -1); // trying to pay previous deleted transaction
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle Payment by Cash
        assertTrue(ez.receiveCashPayment(saleTransactionID, 0.25) == -1); // Insufficient money
        Integer finalSaleTransactionID1 = saleTransactionID;
        assertThrows(InvalidTransactionIdException.class, () -> ez.receiveCashPayment(-1, 25)); // Invalid Transaction ID
        assertThrows(InvalidPaymentException.class, () -> ez.receiveCashPayment(finalSaleTransactionID1, -1)); // Negative cash

        // Test User authentication on both receiveCreditCardPayment and receiveCashPayment
        ez.logout();
        assertThrows(UnauthorizedException.class, () -> ez.receiveCashPayment(finalSaleTransactionID1, 10.0));
        assertThrows(UnauthorizedException.class, () -> ez.receiveCreditCardPayment(finalSaleTransactionID1, creditCard));
    }

    /** This test method covers Scenario 8.1 + Scenario 10.1
     *
     */
    @Test
    public void testReturnCreditCardPayment() throws UnauthorizedException, InvalidTransactionIdException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException, InvalidCreditCardException {
        String creditCard = "4485370086510891"; // Credit Card is registered
        // Create and complete saleTransaction for the following returnTransaction to test
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));
        assertTrue(ez.receiveCashPayment(saleTransactionID, 10000) >= 0);

        // Create returnTransaction and close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));
        assertTrue(ez.endReturnTransaction(returnTransactionID, true));

        // Handle Payment (it should work)
        assertTrue(ez.returnCreditCardPayment(returnTransactionID, creditCard) > 0);
        ez.logout();
    }

    /** This test method covers Scenario 8.1 + Scenario 10.2
     *
     */
    @Test
    public void testReturnCashPayment() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPaymentException {
        // Create and complete saleTransaction for the following returnTransaction to test
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));
        assertTrue(ez.receiveCashPayment(saleTransactionID, 10000) >= 0);

        // Create returnTransaction and close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));
        assertTrue(ez.endReturnTransaction(returnTransactionID, true));

        // Handle Payment (it should work)
        assertTrue(ez.returnCashPayment(returnTransactionID) > 0);
        ez.logout();
    }

    @Test
    public void testReturnInvalidCardPayment() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPaymentException, InvalidCreditCardException {
        String creditCard = "1261924021131027"; //valid according to Luhn's algo, but not registered
        // Create and complete saleTransaction for the following returnTransaction to test
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 1));
        assertTrue(ez.endSaleTransaction(saleTransactionID));
        assertTrue(ez.receiveCashPayment(saleTransactionID, 10000) >= 0);

        // Create returnTransaction and close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));
        assertTrue(ez.endReturnTransaction(returnTransactionID, true));

        // Handle Payment (it should not work)
        assertFalse(ez.returnCreditCardPayment(returnTransactionID, creditCard) != -1);
        ez.logout();
    }

    /** This test method checks whether error return values or exception are raised as expected
     */
    @Test
    public void testAllCasesReturn() throws UnauthorizedException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidPaymentException, InvalidCreditCardException {
        String creditCard = "4485370086510891"; // Credit Card is registered
        // Create and complete saleTransaction for the following returnTransaction to test
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 2));
        assertTrue(ez.endSaleTransaction(saleTransactionID));
        assertTrue(ez.receiveCashPayment(saleTransactionID, 10000) >= 0);

        // Create returnTransaction but do not close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));

        // Test returnCreditCardPayment
        assertFalse(ez.returnCreditCardPayment(returnTransactionID, creditCard) != -1); // returnTransaction is not closed!
        assertTrue(ez.endReturnTransaction(returnTransactionID, true)); // close the returnTransaction
        assertTrue(ez.deleteReturnTransaction(returnTransactionID)); // delete the returnTransaction
        assertFalse(ez.returnCreditCardPayment(returnTransactionID, creditCard) != -1); // trying to pay a non existing Transaction
        assertThrows(InvalidTransactionIdException.class, () -> ez.returnCreditCardPayment(-1,creditCard));
        Integer finalReturnTransactionID = returnTransactionID;
        assertThrows(InvalidCreditCardException.class, () -> ez.returnCreditCardPayment(finalReturnTransactionID, null));

        // Recreate a returnTransaction but do not close it
        returnTransactionID = ez.startReturnTransaction(saleTransactionID);
        assertTrue(returnTransactionID > 0);
        assertTrue(ez.returnProduct(returnTransactionID, barCode, 1));

        // Test returnCashPayment
        assertFalse(ez.returnCashPayment(returnTransactionID) != -1); // the transaction is not closed
        assertTrue(ez.endReturnTransaction(returnTransactionID, true)); // close the returnTransaction
        assertTrue(ez.deleteReturnTransaction(returnTransactionID)); // delete the returnTransaction
        assertFalse(ez.returnCashPayment(returnTransactionID) != -1); // trying to return a non esisting transaction
        assertThrows(InvalidTransactionIdException.class, () -> ez.returnCashPayment(-1));

        // Test authentication for both returnCreditCardPayment and returnCashPayment
        ez.logout();
        Integer finalReturnTransactionID1 = returnTransactionID;
        assertThrows(UnauthorizedException.class, () -> ez.returnCreditCardPayment(finalReturnTransactionID1, creditCard));
        assertThrows(UnauthorizedException.class, () -> ez.returnCashPayment(finalReturnTransactionID1));
        returnTransactionID = null;
    }

}
