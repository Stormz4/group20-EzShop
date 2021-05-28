package it.polito.ezshop.UnitTesting;

import it.polito.ezshop.data.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEZShop_updateCreditInTXTbyCardNumber {
    boolean isCorrect;
    double value;

    @Before
    public void init(){
        isCorrect = false;
    }

    @Test
    public void testUpdateCredit1A_correct() { // 150.00 + 50.00 = 200.00
        isCorrect = EZShop.updateCreditInTXTbyCardNumber("4485370086510891", 50.0);
        value = EZShop.getCreditInTXTbyCardNumber("4485370086510891");
        assertTrue(isCorrect);
       // Value of the first 3 cards should not be modified
        // assertEquals(200.00, value, 0.001);

        // take back money
        EZShop.updateCreditInTXTbyCardNumber("4485370086510891", -50.0);
    }

    @Test
    public void testUpdateCredit1B_correct() { // 300.00 + 50.00 = 350.00
        isCorrect = EZShop.updateCreditInTXTbyCardNumber("1476020159328823", 50.0);
        value = EZShop.getCreditInTXTbyCardNumber("1476020159328823");
        assertTrue(isCorrect);
        assertEquals(350.00, value, 0.001);
        // take back money
        EZShop.updateCreditInTXTbyCardNumber("1476020159328823", -50.0);
    }

    @Test
    public void testUpdateCredit_notEnoughMoney() {
        isCorrect = EZShop.updateCreditInTXTbyCardNumber("8177206401238186", -50.0);
        value = EZShop.getCreditInTXTbyCardNumber("8177206401238186");
        assertFalse(isCorrect);
        assertEquals(0.00, value, 0.001);
    }

    @Test
    public void testUpdateCredit2_negativeDouble() { // 150.00 - 50.00 = 100.00
        isCorrect = EZShop.updateCreditInTXTbyCardNumber("4485370086510891", -50.0);
        value = EZShop.getCreditInTXTbyCardNumber("4485370086510891");
        assertTrue(isCorrect);
        // assertEquals(100.00, value, 0.001);
        // give back money:
        EZShop.updateCreditInTXTbyCardNumber("4485370086510891", 50.0);
    }

    @Test
    public void testUpdateCredit3_nullCard() {
        isCorrect = EZShop.updateCreditInTXTbyCardNumber(null, 50.0);
        assertFalse(isCorrect);
    }

    @Test
    public void testUpdateCredit4_invalidCard() {
        isCorrect = EZShop.updateCreditInTXTbyCardNumber("A4DJSKID91864F", 50.0);
        assertFalse(isCorrect);
    }

    @Test
    public void testUpdateCredit5_hashtag() {
        isCorrect = EZShop.updateCreditInTXTbyCardNumber("#", -50.0);
        assertFalse(isCorrect);
    }

    @Test
    public void testUpdateCredit6_specialCharacters() {
        isCorrect = EZShop.updateCreditInTXTbyCardNumber(";-!", -50.0);
        assertFalse(isCorrect);
    }

    @Test
    public void testUpdateCredit7_Min() {
        isCorrect = EZShop.updateCreditInTXTbyCardNumber("4485370086510891", Double.MIN_VALUE);
        value = EZShop.getCreditInTXTbyCardNumber("4485370086510891");
        assertTrue(isCorrect);
        assertEquals(150.00 + Double.MIN_VALUE, value, 0.001);
        // take back money:
        EZShop.updateCreditInTXTbyCardNumber("4485370086510891", -Double.MIN_VALUE);
    }
    @Test
    public void testUpdateCredit8_Min_Neg() {
        isCorrect = EZShop.updateCreditInTXTbyCardNumber("4485370086510891", -Double.MIN_VALUE);
        value = EZShop.getCreditInTXTbyCardNumber("4485370086510891");
        assertTrue(isCorrect);
        assertEquals(150.00 - Double.MIN_VALUE, value, 0.001);
        // give back money:
        EZShop.updateCreditInTXTbyCardNumber("4485370086510891", Double.MIN_VALUE);
    }
}
