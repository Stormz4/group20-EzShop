package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEZShop_getCreditInTXTbyCardNumber {
    EZShop ez;
    double value;

    @Before
    public void init(){
        ez = new EZShop();
    }

    @Test
    public void testGetCredit_correct() {
        value = ez.getCreditInTXTbyCardNumber("4485370086510891");
        assertEquals(150.00, value, 0.001);
    }

    @Test
    public void testGetCredit_null() {
        value = ez.getCreditInTXTbyCardNumber(null);
        assertEquals(-1, value, 0);
    }

    @Test
    public void testGetCredit_alphanumeric() {
        value = ez.getCreditInTXTbyCardNumber("4Z85a70b8F51c89D1");
        assertEquals(-1, value, 0);
    }

    @Test
    public void testGetCredit_lessDigits() {
        value = ez.getCreditInTXTbyCardNumber("345");
        assertEquals(-1, value, 0);
    }

    @Test
    public void testGetCredit_hashtag() {
        value = ez.getCreditInTXTbyCardNumber("#");
        assertEquals(-1, value, 0);
    }

    @Test
    public void testGetCredit_specialCharacters() {
        value = ez.getCreditInTXTbyCardNumber(";-!");
        assertEquals(-1, value, 0);
    }

}
