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
    boolean isValid;

    @Before
    public void init(){
        isValid = false;
    }

    @Test
    public void testLoyaltyCode_notEnoughDigits(){
        isValid = EZShop.isValidCard("33235");
        assertFalse(isValid);
    }

    @Test

    public void testLoyaltyCode_nullInput(){
        isValid = EZShop.isValidCard(null);
        assertFalse(isValid);
    }

    @Test
    public void testLoyaltyCode_10digits(){
        isValid = EZShop.isValidCard("2332543219");
        assertTrue(isValid);
    }

}
