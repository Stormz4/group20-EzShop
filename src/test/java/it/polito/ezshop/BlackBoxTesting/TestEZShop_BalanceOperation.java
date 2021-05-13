package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;

public class TestEZShop_BalanceOperation {
    EZBalanceOperation balanceOp;
    @Test
    public void testCustomer(){
        balanceOp = new EZBalanceOperation(2, LocalDate.now(), 10, EZBalanceOperation.Credit);
        balanceOp.setBalanceId(3);
        int i = balanceOp.getBalanceId();
        balanceOp.setDate(LocalDate.of(2015, 8, 10));
        LocalDate localDate = balanceOp.getDate();
        balanceOp.setMoney(20);
        double d = balanceOp.getMoney();
        balanceOp.setType(EZBalanceOperation.Debit);
        String s = balanceOp.getType();

        assertEquals(3, i);
        assertEquals(2015, localDate.getYear());
        assertEquals(8, localDate.getMonthValue());
        assertEquals(10, localDate.getDayOfMonth());
        assertEquals(20, d, 0.1);
        assertEquals("DEBIT", s);
    }
}
