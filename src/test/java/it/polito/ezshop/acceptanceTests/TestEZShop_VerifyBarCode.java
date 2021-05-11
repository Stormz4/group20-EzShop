package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEZShop_VerifyBarCode {

    // Algorithm works as follows
    // https://www.gs1.org/services/how-calculate-check-digit-manually
    // 12 to 14 digits

    EZShop ez;
    boolean isValid;

    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        ez = new EZShop();
        isValid = false;
    }

    @Test
    public void testBarCode_notEnoughDigits(){
        isValid = ez.isValidBarCode("33235");
        assertFalse(isValid);
    }

    @Test
    public void testBarCode_nullInput(){
        isValid = ez.isValidBarCode(null);
        assertFalse(isValid);
    }

    @Test
    public void testBarCode_12digits(){
        isValid = ez.isValidBarCode("233254321519");
        assertTrue(isValid);
    }

    @Test
    public void testBarCode_13digits(){
        isValid = ez.isValidBarCode("6291041500213");
        assertTrue(isValid);
    }

    @Test
    public void testBarCode_14digits(){
        isValid = ez.isValidBarCode("54326476412231");
        assertTrue(isValid);
    }


}
