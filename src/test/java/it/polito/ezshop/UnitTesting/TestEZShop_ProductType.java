package it.polito.ezshop.UnitTesting;

import it.polito.ezshop.data.EZProductType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestEZShop_ProductType {
    EZProductType p;
    @Test
    public void testProductType(){
        p = new EZProductType(-1, 0, "55-AZE-22", "Expires on Monday", "Milk", "233254321519", 5);
        p.setProductDescription("Cookie");
        String s = p.getProductDescription();
        p.setBarCode("6291041500213");
        String s2 = p.getBarCode();
        p.setId(-2);
        int i = p.getId();
        p.setQuantity(5);
        int i2 = p.getQuantity();
        p.setNote("Expires on Saturday");
        String s3 = p.getNote();
        p.setLocation("32-ABU-45");
        String s4 = p.getLocation();
        p.setPricePerUnit(10.5);
        double d = p.getPricePerUnit();

        assertEquals("Cookie", s);
        assertEquals("6291041500213", s2);
        assertEquals(-2, i);
        assertEquals(5, i2);
        assertEquals("Expires on Saturday", s3);
        assertEquals("32-ABU-45", s4);
        assertEquals(10.5, d, 0.0);

        p.editQuantity(40);
        i2 = p.getQuantity();
        assertEquals(45, i2);
    }
}
