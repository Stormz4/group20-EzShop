package it.polito.ezshop.UnitTesting;

import it.polito.ezshop.data.EZShop;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestEZShop_IsValidPosition {

    boolean isValid;

    @Before
    public void init(){
        isValid = false;
    }

    @Test
    public void testPosition_notValid(){
        isValid = EZShop.isValidPosition("33235");
        assertFalse(isValid);
    }

    @Test

    public void testPosition_nullInput(){
        isValid = EZShop.isValidPosition(null);
        assertFalse(isValid);
    }

    @Test
    public void testPosition_Valid(){
        isValid = EZShop.isValidPosition("55-ADB-44");
        assertTrue(isValid);
    }
}
