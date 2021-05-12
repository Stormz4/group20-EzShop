package it.polito.ezshop.WhiteBoxTesting;

import it.polito.ezshop.data.EZCustomer;
import it.polito.ezshop.data.EZUser;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEZShop_User {

    EZUser user;
    @Test
    public void testCustomer(){
        user = new EZUser(-1, "Ciccio", "superpwd", "Administrator");

        user.setUsername("Francesco");
        String s = user.getUsername();
        user.setPassword("nopwd");
        String s2 = user.getPassword();
        user.setId(-2);
        int i = user.getId();

        user.setRole(null);
        user.setRole("ShopManager");
        String s3 = user.getRole();

        boolean b = user.hasRequiredRole(null);
        boolean b2 = user.hasRequiredRole("ShopManager");
        boolean b3 = user.hasRequiredRole("Cashier");

        assertEquals("Francesco", s);
        assertEquals("nopwd", s2);
        assertEquals(-2, i);
        assertEquals("ShopManager", s3);
        assertFalse(b);
        assertTrue(b2);
        assertFalse(b3);
    }
}
