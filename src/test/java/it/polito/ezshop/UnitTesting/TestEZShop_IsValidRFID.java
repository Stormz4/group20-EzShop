package it.polito.ezshop.UnitTesting;

import it.polito.ezshop.data.EZShop;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestEZShop_IsValidRFID {

    EZShop ez = new EZShop();

    @Test
    public void testValidRFID() {
        String testRFID = "000000000000";
        assertTrue(ez.isValidRFID(testRFID));
    }

    @Test
    public void testInvalidRFID() {
        String testRFID = "1010";
        assertFalse(ez.isValidRFID(testRFID));
    }

    @Test
    public void testNullRFID() {
        String testRFID = null;
        assertFalse(ez.isValidRFID(testRFID));
    }


}
