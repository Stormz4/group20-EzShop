package it.polito.ezshop.UnitTesting;

import it.polito.ezshop.data.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEZShop_getCreditInTXTbyCardNumber {

    double value;

    @Test
    public void testGetCredit_correct() {
        value = EZShop.getCreditInTXTbyCardNumber("4485370086510891");
        assertEquals(150.00, value, 0.001);
    }

    @Test
    public void testGetCredit_null() {
        value = EZShop.getCreditInTXTbyCardNumber(null);
        assertEquals(-1, value, 0);
    }

    @Test
    public void testGetCredit_alphanumeric() {
        value = EZShop.getCreditInTXTbyCardNumber("4Z85a70b8F51c89D1");
        assertEquals(-1, value, 0);
    }

    @Test
    public void testGetCredit_lessDigits() {
        value = EZShop.getCreditInTXTbyCardNumber("345");
        assertEquals(-1, value, 0);
    }

    @Test
    public void testGetCredit_hashtag() {
        value = EZShop.getCreditInTXTbyCardNumber("#");
        assertEquals(-1, value, 0);
    }

    @Test
    public void testGetCredit_specialCharacters() {
        value = EZShop.getCreditInTXTbyCardNumber(";-!");
        assertEquals(-1, value, 0);
    }

}
