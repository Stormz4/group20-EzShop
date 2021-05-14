package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEZShop_updateCreditInTXTbyCardNumber {
    EZShop ez;
    boolean isCorrect;
    double value;

    @Before
    public void init(){
        ez = new EZShop();
        isCorrect = false;
    }

    @Test
    public void testUpdateCredit1_correct() { // 150.00 + 50.00 = 200.00
        isCorrect = ez.updateCreditInTXTbyCardNumber("4485370086510891", 50.0);
        value = ez.getCreditInTXTbyCardNumber("4485370086510891");
        assertTrue(isCorrect);
        assertEquals(200.00, value, 0.001);
        // take back money
        ez.updateCreditInTXTbyCardNumber("4485370086510891", -50.0);
    }

    @Test
    public void testUpdateCredit2_negativeDouble() { // 150.00 - 50.00 = 100.00
        isCorrect = ez.updateCreditInTXTbyCardNumber("4485370086510891", -50.0);
        value = ez.getCreditInTXTbyCardNumber("4485370086510891");
        assertTrue(isCorrect);
        assertEquals(100.00, value, 0.001);
        // give back money:
        ez.updateCreditInTXTbyCardNumber("4485370086510891", 50.0);
    }

    @Test
    public void testUpdateCredit3_nullCard() {
        isCorrect = ez.updateCreditInTXTbyCardNumber(null, 50.0);
        assertFalse(isCorrect);
    }

    @Test
    public void testUpdateCredit4_invalidCard() {
        isCorrect = ez.updateCreditInTXTbyCardNumber("A4DJSKID91864F", 50.0);
        assertFalse(isCorrect);
    }

    @Test
    public void testUpdateCredit5_hashtag() {
        isCorrect = ez.updateCreditInTXTbyCardNumber("#", -50.0);
        assertFalse(isCorrect);
    }

    @Test
    public void testUpdateCredit6_specialCharacters() {
        isCorrect = ez.updateCreditInTXTbyCardNumber(";-!", -50.0);
        assertFalse(isCorrect);
    }

    @Test
    public void testUpdateCredit7_Min() {
        isCorrect = ez.updateCreditInTXTbyCardNumber("4485370086510891", Double.MIN_VALUE);
        value = ez.getCreditInTXTbyCardNumber("4485370086510891");
        assertTrue(isCorrect);
        assertEquals(150.00 + Double.MIN_VALUE, value, 0.001);
        // take back money:
        ez.updateCreditInTXTbyCardNumber("4485370086510891", -Double.MIN_VALUE);
    }
    @Test
    public void testUpdateCredit8_Min_Neg() {
        isCorrect = ez.updateCreditInTXTbyCardNumber("4485370086510891", -Double.MIN_VALUE);
        value = ez.getCreditInTXTbyCardNumber("4485370086510891");
        assertTrue(isCorrect);
        assertEquals(150.00 - Double.MIN_VALUE, value, 0.001);
        // give back money:
        ez.updateCreditInTXTbyCardNumber("4485370086510891", Double.MIN_VALUE);
    }
}
