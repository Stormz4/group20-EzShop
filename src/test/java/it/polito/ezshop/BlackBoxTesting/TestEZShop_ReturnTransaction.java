package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.*;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class TestEZShop_ReturnTransaction {
    EZReturnTransaction returnTransaction;
    @Test
    public void testCustomer(){
        returnTransaction = new EZReturnTransaction(2, 1, new LinkedList<>(), 10, EZReturnTransaction.RTClosed);
        returnTransaction.setReturnId(3);
        int id = returnTransaction.getReturnId();
        returnTransaction.setReturnedValue(10);
        double d = returnTransaction.getReturnedValue();
        returnTransaction.setItsSaleTransactionId(2);
        int trId = returnTransaction.getSaleTransactionId();
        LinkedList<TicketEntry> list = new LinkedList<>();
        EZTicketEntry entry = new EZTicketEntry("0000", "hello", 2, 98, 0.5);
        list.add(entry);
        returnTransaction.setEntries(list);
        List<TicketEntry> gotList = returnTransaction.getEntries();
        returnTransaction.setStatus(EZReturnTransaction.RTPayed);
        String s = returnTransaction.getStatus();

        assertEquals(3, id);
        assertEquals(10, d, 0.1);
        assertEquals(2, trId);
        assertEquals("PAYED", s);
        assertTrue(gotList.contains(entry));
        assertEquals(1, gotList.size());

        returnTransaction.updateReturnedValue(25);
        d = returnTransaction.getReturnedValue();

        assertEquals(35, d, 0.1);
    }
}
