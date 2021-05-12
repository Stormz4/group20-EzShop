package it.polito.ezshop.WhiteBoxTesting;


import it.polito.ezshop.data.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEZShop_Customer {
    EZCustomer customer;
    @Test
    public void testCustomer(){
        customer = new EZCustomer(-1, "Marco","0000000500", 0);
        customer.setCustomerName("Francesco");
        String s = customer.getCustomerName();
        customer.setCustomerCard("0000000501");
        String s2 = customer.getCustomerCard();
        customer.setId(-2);
        int i = customer.getId();
        customer.setPoints(50);
        int i2 = customer.getPoints();

        assertEquals("Francesco", s);
        assertEquals("0000000501", s2);

        assertEquals(-2, i);
        assertEquals(50, i2);
    }
}
