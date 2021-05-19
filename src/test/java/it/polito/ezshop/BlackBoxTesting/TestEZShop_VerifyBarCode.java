package it.polito.ezshop.BlackBoxTesting;


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

    boolean isValid;

    @Before
    public void init(){
        isValid = false;
    }

    @Test
    public void testBarCode_notEnoughDigits(){
        isValid = EZShop.isValidBarCode("33235");
        assertFalse(isValid);
    }

    @Test
    public void testBarCode_nullInput(){
        isValid = EZShop.isValidBarCode(null);
        assertFalse(isValid);
    }

    @Test
    public void testBarCode_alphanumeric(){
        isValid =  EZShop.isValidBarCode("54326476412b31");
        assertFalse(isValid);
    }

    @Test
    public void testBarCode_12digits(){
        isValid =  EZShop.isValidBarCode("233254321519");
        assertTrue(isValid);
    }

    @Test
    public void testBarCode_13digits(){
        isValid =  EZShop.isValidBarCode("6291041500213");
        assertTrue(isValid);
    }

    @Test
    public void testBarCode_14digits(){
        isValid = EZShop.isValidBarCode("54326476412231");
        assertTrue(isValid);
    }

    @Test
    public void testBarCode_algorithm(){
        isValid =  EZShop.isValidBarCode("54326476412234");
        assertFalse(isValid);
    }


}
