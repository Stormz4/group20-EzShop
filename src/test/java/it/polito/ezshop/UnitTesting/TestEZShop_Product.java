package it.polito.ezshop.UnitTesting;

import it.polito.ezshop.data.EZProduct;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestEZShop_Product {

    @Test
    public void testProduct(){
        String testString = "000000000000";
        EZProduct p = new EZProduct(null, null, null, null);
        p.setRFID(testString);
        p.setProdTypeID(1);
        p.setSaleID(2);
        p.setReturnID(3);
        assertEquals(testString, p.getRFID());
        assertEquals(Integer.valueOf(1), p.getProdTypeID());
        assertEquals(Integer.valueOf(2), p.getSaleID());
        assertEquals(Integer.valueOf(3), p.getReturnID());
    }

}
