package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestEZShop_isValidCreditCard {

    boolean isValid;

    @Before
    public void init(){
        isValid = false;
    }

    @Test
    public void testCreditCardValidity_correct() {
        isValid = EZShop.isValidCreditCard("4485370086510891");
        assertTrue(isValid);
    }

    @Test
    public void testCreditCardValidity_null() {
        isValid = EZShop.isValidCreditCard(null);
        assertFalse(isValid);
    }

    @Test
    public void testCreditCardValidity_alphanumeric() {
        isValid = EZShop.isValidCreditCard("4Z85a70b8F51c89D1");
        assertFalse(isValid);
    }

    @Test
    public void testCreditCardValidity_lessDigits() {
        isValid = EZShop.isValidCreditCard("345");
        assertFalse(isValid);
    }

    @Test
    public void testCreditCardValidity_hashtag() {
        isValid = EZShop.isValidCreditCard("#4485370086510891");
        assertFalse(isValid);
    }

    @Test
    public void testCreditCardValidity_specialCharacters() {
        isValid = EZShop.isValidCreditCard(";-!");
        assertFalse(isValid);
    }

    @Test
    public void testCreditCardValidity_notRespectingLuhnAlgo() {
        isValid = EZShop.isValidCreditCard("1849264944268091"); // random number not respecting Luhn algorithm
        assertFalse(isValid);
    }
}


