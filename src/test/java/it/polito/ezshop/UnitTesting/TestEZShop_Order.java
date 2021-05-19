package it.polito.ezshop.UnitTesting;


import it.polito.ezshop.data.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEZShop_Order {
    EZOrder order;

    @Test
    public void testOrder() {
        Integer cID = 128;
        Integer cBalanceID = 15;
        String cProductCode = "0003470389";
        double cPricePerUnit = 12.50;
        int cQuantity = 85;
        String cStatus = "ISSUED";

        order = new EZOrder(cID, cBalanceID, cProductCode, cPricePerUnit, cQuantity, cStatus);
        assertEquals(order.getOrderId(), cID);
        assertEquals(order.getBalanceId(), cBalanceID);
        assertEquals(order.getProductCode(), cProductCode);
        assertEquals(order.getPricePerUnit(), cPricePerUnit, 0.1);
        assertEquals(order.getQuantity(), cQuantity);
        assertEquals(order.getStatus(), cStatus);

        Integer sID = 150;
        Integer sBalanceID = 20;
        String sProductCode = "0003490529";
        double sPricePerUnit = 24.10;
        int sQuantity = 70;
        String sStatus = "COMPLETED";

        order.setOrderId(sID);
        order.setBalanceId(sBalanceID);
        order.setProductCode(sProductCode);
        order.setPricePerUnit(sPricePerUnit);
        order.setQuantity(sQuantity);
        order.setStatus(sStatus);
        
        assertEquals(order.getOrderId(), sID);
        assertEquals(order.getBalanceId(), sBalanceID);
        assertEquals(order.getProductCode(), sProductCode);
        assertEquals(order.getPricePerUnit(), sPricePerUnit, 0.1);
        assertEquals(order.getQuantity(), sQuantity);
        assertEquals(order.getStatus(), sStatus);
    }
}
