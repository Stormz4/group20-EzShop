package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.EZShop;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestEZShop_IsValidPosition {
    EZShop ez;
    boolean isValid;

    @Before
    public void init(){
        ez = new EZShop();
        isValid = false;
    }

    @Test
    public void testPosition_notValid(){
        isValid = ez.isValidPosition("33235");
        assertFalse(isValid);
    }

    @Test

    public void testPosition_nullInput(){
        isValid = ez.isValidPosition(null);
        assertFalse(isValid);
    }

    @Test
    public void testPosition_Valid(){
        isValid = ez.isValidPosition("55-ADB-44");
        assertTrue(isValid);
    }
}
