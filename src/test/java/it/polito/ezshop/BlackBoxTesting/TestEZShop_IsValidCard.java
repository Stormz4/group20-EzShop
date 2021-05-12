package it.polito.ezshop.BlackBoxTesting;


import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestEZShop_IsValidCard{
    EZShop ez;
    boolean isValid;

    @Before
    public void init(){
        ez = new EZShop();
        isValid = false;
    }

    @Test
    public void testLoyaltyCode_notEnoughDigits(){
        isValid = ez.isValidCard("33235");
        assertFalse(isValid);
    }

    @Test

    public void testLoyaltyCode_nullInput(){
        isValid = ez.isValidCard(null);
        assertFalse(isValid);
    }

    @Test
    public void testLoyaltyCode_10digits(){
        isValid = ez.isValidCard("2332543219");
        assertTrue(isValid);
    }

}
