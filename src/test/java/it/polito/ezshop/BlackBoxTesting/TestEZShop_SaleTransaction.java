package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

public class TestEZShop_SaleTransaction {
    EZSaleTransaction sale, sale2;
    @Test
    public void testSaleTransaction() {
        sale = new EZSaleTransaction(2, new LinkedList<>(), 0.30, 15.60, EZSaleTransaction.STClosed);
        assertEquals(2, sale.getTicketNumber(), 0);
        assertTrue(sale.getEntries().isEmpty());
        assertEquals(0.30, sale.getDiscountRate(), 0.001);
        assertEquals(15.60, sale.getPrice(), 0.001);
        assertEquals("CLOSED", sale.getStatus());
        assertTrue(sale.getReturns().isEmpty());
        assertNull(sale.getAttachedCard());

        sale2 = new EZSaleTransaction(3);
        assertEquals(3, sale2.getTicketNumber(), 0);
        assertTrue(sale2.getEntries().isEmpty());
        assertEquals(0, sale2.getDiscountRate(), 0.001);
        assertEquals(0, sale2.getPrice(), 0.001);
        assertEquals("OPENED", sale2.getStatus());
        assertTrue(sale2.getReturns().isEmpty());
        assertNull(sale2.getAttachedCard());

        sale.setTicketNumber(200);
        assertEquals(200, sale.getTicketNumber(), 0);

        List<TicketEntry> list = new LinkedList<TicketEntry>();
        EZTicketEntry t1 = new EZTicketEntry("7293829484929", "test prod", 3, 11.20, 0.10);
        EZTicketEntry t2 = new EZTicketEntry("1627482847283", "test prod2", 1, 49.99, 0.25);
        list.add((TicketEntry) t1);
        list.add((TicketEntry) t2);
        sale.setEntries(list);
        assertEquals(2, sale.getEntries().size(), 0);

        sale.setDiscountRate(0.90);
        assertEquals(0.90, sale.getDiscountRate(), 0.001);

        sale.setPrice(400.12);
        assertEquals(400.12, sale.getPrice(), 0.001);

        sale.setStatus(EZSaleTransaction.STPayed);
        assertEquals("PAYED", sale.getStatus());

        sale.setAttachedCard("0000000001");
        assertEquals("0000000001", sale.getAttachedCard());

        EZReturnTransaction r1 = new EZReturnTransaction(4, sale.getTicketNumber(), new LinkedList<TicketEntry>(), 0, EZReturnTransaction.RTPayed);
        EZTicketEntry tr1 = new EZTicketEntry("7293829484929", "test prod", 1, 11.20, 0.10);
        List<TicketEntry> entries_r = r1.getEntries();
        entries_r.add(tr1);
        r1.setReturnedValue(11.20);
        r1.setEntries(entries_r);
        List<EZReturnTransaction> list_r = new LinkedList<EZReturnTransaction>();
        list_r.add(r1);
        sale.setReturns(list_r);
        assertEquals(1, sale.getReturns().size(), 0);

        TicketEntry gt = sale.getTicketEntryByBarCode("1627482847283");
        assertEquals(t2, gt);

        sale.updatePrice(30.20);
        assertEquals(430.32, sale.getPrice(), 0.001);
        sale.updatePrice(-30.20);
        assertEquals(400.12, sale.getPrice(), 0.001);

        assertFalse(sale.hasRequiredStatus(null));
        assertFalse(sale.hasRequiredStatus(EZSaleTransaction.STClosed));
        assertTrue(sale.hasRequiredStatus(EZSaleTransaction.STPayed));

        gt = sale.getTicketEntryByBarCode("162748284283");
        assertNull(gt);
    }

}
