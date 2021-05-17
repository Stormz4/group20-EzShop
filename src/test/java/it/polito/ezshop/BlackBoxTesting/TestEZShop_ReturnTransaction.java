package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.*;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TestEZShop_ReturnTransaction {
    EZReturnTransaction returnTransaction, returnTransaction2;
    @Test
    public void testReturnTransaction(){
        returnTransaction = new EZReturnTransaction(2, 1, new LinkedList<>(), 10, EZReturnTransaction.RTClosed);
        int id = returnTransaction.getReturnId();
        double d = returnTransaction.getReturnedValue();
        int trId = returnTransaction.getSaleTransactionId();
        LinkedList<TicketEntry> list = (LinkedList<TicketEntry>) returnTransaction.getEntries();
        String s = returnTransaction.getStatus();
        assertEquals(2, id);
        assertEquals(10, d, 0.1);
        assertEquals(1, trId);
        assertEquals("CLOSED", s);
        assertTrue(list.isEmpty());
        returnTransaction2 = new EZReturnTransaction(returnTransaction);
        id = returnTransaction2.getReturnId();
        d = returnTransaction2.getReturnedValue();
        trId = returnTransaction2.getSaleTransactionId();
        list = (LinkedList<TicketEntry>) returnTransaction2.getEntries();
        s = returnTransaction2.getStatus();
        assertEquals(2, id);
        assertEquals(10, d, 0.1);
        assertEquals(1, trId);
        assertEquals("CLOSED", s);
        assertTrue(list.isEmpty());
        returnTransaction.setReturnId(3);
        id = returnTransaction.getReturnId();
        returnTransaction.setReturnedValue(10);
        d = returnTransaction.getReturnedValue();
        returnTransaction.setItsSaleTransactionId(2);
        trId = returnTransaction.getSaleTransactionId();
        list = new LinkedList<>();
        EZTicketEntry entry = new EZTicketEntry("0000", "hello", 2, 98, 0.5);
        list.add(entry);
        returnTransaction.setEntries(list);
        List<TicketEntry> gotList = returnTransaction.getEntries();
        s = returnTransaction.getStatus();

        assertEquals(3, id);
        assertEquals(10, d, 0.1);
        assertEquals(2, trId);
        assertEquals("PAYED", s);
        assertTrue(gotList.contains(entry));
        assertEquals(1, gotList.size());

        returnTransaction.updateReturnedValue(25);
        d = returnTransaction.getReturnedValue();
        returnTransaction.setEntries(null);
        gotList = returnTransaction.getEntries();

        assertTrue(gotList.isEmpty());
        assertEquals(35, d, 0.1);
    }
}
