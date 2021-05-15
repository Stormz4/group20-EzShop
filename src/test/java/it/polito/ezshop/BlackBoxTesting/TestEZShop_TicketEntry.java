package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.EZTicketEntry;
import static org.junit.Assert.*;
import org.junit.Test;

public class TestEZShop_TicketEntry {

    @Test
    public void testTicketEntryConstructor() {
        EZTicketEntry ticketEntry;

        String cBarCode = "0000389028";
        String cDescription = "A simple description";
        int cAmount = 87362;
        double cPricePerUnit = 26.80;
        double cDiscountRate = 25.00;

        ticketEntry = new EZTicketEntry(cBarCode, cDescription, cAmount, cPricePerUnit, cDiscountRate);
        assertNotNull(ticketEntry);

        // Check barCode
        assertNotNull(ticketEntry.getBarCode());
        assertEquals(cBarCode, ticketEntry.getBarCode());

        // Check description
        assertNotNull(ticketEntry.getProductDescription());
        assertEquals(cDescription, ticketEntry.getProductDescription());

        // Check amount
        assertEquals(cAmount, ticketEntry.getAmount(), 0.1);

        // Check pricePerUnit
        assertEquals(cPricePerUnit, ticketEntry.getPricePerUnit(), 0.1);

        // Check discountRate
        assertEquals(cDiscountRate, ticketEntry.getDiscountRate(), 0.1);

        cBarCode = null;
        cDescription = null;
        cAmount = 236186;
        cPricePerUnit = 8.232;
        cDiscountRate = 34.34;
        ticketEntry = new EZTicketEntry(cBarCode, cDescription, cAmount, cPricePerUnit, cDiscountRate);

        // Check barCode
        assertNull(ticketEntry.getBarCode());

        // Check description
        assertNull(ticketEntry.getProductDescription());

        // Check amount
        assertEquals(cAmount, ticketEntry.getAmount(), 0.1);

        // Check pricePerUnit
        assertEquals(cPricePerUnit, ticketEntry.getPricePerUnit(), 0.1);

        // Check discountRate
        assertEquals(cDiscountRate, ticketEntry.getDiscountRate(), 0.1);
    }

    @Test
    public void testTicketEntrySetters() {
        EZTicketEntry ticketEntry;

        String cBarCode = "0000389028";
        String cDescription = "A simple description";
        int cAmount = 87362;
        double cPricePerUnit = 26.80;
        double cDiscountRate = 25.00;

        ticketEntry = new EZTicketEntry(cBarCode, cDescription, cAmount, cPricePerUnit, cDiscountRate);

        // Values for setters
        String sBarCode = "0000363928";
        String sDescription = "Another simple description";
        int sAmount = 1765;
        double sPricePerUnit = 3.80;
        double sDiscountRate = 38.50;

        ticketEntry.setBarCode(sBarCode);
        ticketEntry.setProductDescription(sDescription);
        ticketEntry.setAmount(sAmount);
        ticketEntry.setPricePerUnit(sPricePerUnit);
        ticketEntry.setDiscountRate(sDiscountRate);

        // Check barCode
        assertNotNull(ticketEntry.getBarCode());
        assertEquals(sBarCode, ticketEntry.getBarCode());

        // Check description
        assertNotNull(ticketEntry.getProductDescription());
        assertEquals(sDescription, ticketEntry.getProductDescription());

        // Check amount
        assertEquals(sAmount, ticketEntry.getAmount());

        // Check pricePerUnit
        assertEquals(sPricePerUnit, ticketEntry.getPricePerUnit(), 0.1);

        // Check discountRate
        assertEquals(sDiscountRate, ticketEntry.getDiscountRate(), 0.1);


        // Check behavior when setting null values
        ticketEntry.setBarCode(null);
        ticketEntry.setProductDescription(null);

        assertNull(ticketEntry.getBarCode());
        assertNull(ticketEntry.getProductDescription());
    }
}
