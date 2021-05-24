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
    Integer uId, productID;
    String barCode = "1345334543427";
    private final SQLiteDB shopDB2 = new SQLiteDB();


    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException, UnauthorizedException, InvalidProductDescriptionException, InvalidPricePerUnitException, InvalidProductCodeException, InvalidProductIdException {
        ez = new EZShop();
        shopDB2.connect();
        shopDB2.initDatabase();
        uId=ez.createUser("testUser00", "pwd", "Administrator");
        ez.login("testUser00", "pwd");
        productID = ez.createProductType("Vic3", barCode, 0.9, "by PI");
        ez.updateQuantity(productID, 50);
    }

    @After
    public void teardown(){
        if (uId != null){
            shopDB2.deleteUser(uId);
        }
        if (productID != null)
            shopDB2.deleteProductType(productID);
        shopDB2.closeConnection();
    }

    /** This test reconstructs Scenario 6.1 + Scenario 7.1
     */
    @Test
    public void testReceivePaymentCreditCard() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidProductCodeException, InvalidQuantityException, InvalidTransactionIdException, InvalidCreditCardException {
        Integer saleTransactionID;
        String creditCard = "4485370086510891";

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 25));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle Payment
        assertTrue(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
        // Since CreditCard Payment System is fake (Credit cards are in a .txt file), we do not test whether the amount of money in the Credit Card has changed
    }

    /**
     * This test covers Scenario 7.2
     */
    @Test
    public void testInvalidCardPayment() throws UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidQuantityException, InvalidTransactionIdException, InvalidProductCodeException, InvalidCreditCardException {
        Integer saleTransactionID;
        String creditCard = "1261924021131027"; //valid according to Luhn's algo, but not registered

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 25));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle payment
        assertFalse(ez.receiveCreditCardPayment(saleTransactionID, creditCard));

    }

    /**
     * This test covers Scenario 7.3
     */
    @Test
    public void testInsufficientCreditPayment() throws InvalidCreditCardException, InvalidTransactionIdException, UnauthorizedException, InvalidQuantityException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        Integer saleTransactionID;
        String creditCard = "5100293991053009"; //present in the CreditCard file, but it has insufficient credit

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 25));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle payment
        assertFalse(ez.receiveCreditCardPayment(saleTransactionID, creditCard));
    }

    /**
     * This test covers Scenario 7.4
     */
    @Test
    public void testReceivePaymentCash() throws InvalidTransactionIdException, UnauthorizedException, InvalidPaymentException, InvalidQuantityException, InvalidProductCodeException, InvalidPasswordException, InvalidUsernameException {
        Integer saleTransactionID;

        // Create a dummy SaleTransaction to pay for
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 25));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle payment
        assertTrue(ez.receiveCashPayment(saleTransactionID, 50.0) > 0);
    }

    @Test
    public void testAllCases() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidTransactionIdException, InvalidCreditCardException, InvalidQuantityException, InvalidProductCodeException, InvalidPaymentException {
        Integer saleTransactionID;
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
        // User authorization not checked since receiveCreditCardPayment can be called by any user

        // Test receiveCashPayment

        // Create a dummy saleTransaction to pay for (cash)
        assertFalse(ez.receiveCashPayment(saleTransactionID, 25) != -1); // trying to pay previous deleted transaction
        saleTransactionID = ez.startSaleTransaction();
        assertTrue(saleTransactionID > 0);
        assertTrue(ez.addProductToSale(saleTransactionID, barCode, 25));
        assertTrue(ez.endSaleTransaction(saleTransactionID));

        // Handle Payment by Cash
        assertTrue(ez.receiveCashPayment(saleTransactionID, 0.25) == -1); // Insufficient money
        Integer finalSaleTransactionID1 = saleTransactionID;
        assertThrows(InvalidTransactionIdException.class, () -> ez.receiveCashPayment(-1, 25)); // Invalid Transaction ID
        assertThrows(InvalidPaymentException.class, () -> ez.receiveCashPayment(finalSaleTransactionID1, -1)); // Negative cash
        // User authorization not checked since receiveCreditCardPayment can be called by any user

    }
}
