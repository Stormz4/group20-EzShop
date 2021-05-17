package it.polito.ezshop.BlackBoxTesting;

import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class TestEZShop_AccountBook {
    EZAccountBook book;
    EZShop ez;
    private final SQLiteDB shopDB2 = new SQLiteDB();
    HashMap<Integer, EZBalanceOperation> balanceOperations = new HashMap<Integer, EZBalanceOperation>();

    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        ez = new EZShop();
        shopDB2.connect();
        shopDB2.initDatabase();
    }

    @Test
    public void testAccountBook() {
        book = new EZAccountBook(20000);
        assertEquals(20000.0, book.getCurrentBalance(), 0.001);

        book.updateBalance(100.55);
        assertEquals(20100.55, book.getCurrentBalance(), 0.001);

        book.updateBalance(-100.55);
        assertEquals(20000.0, book.getCurrentBalance(), 0.001);

        book.setCurrentBalance(12345.67);
        assertEquals(12345.67, book.getCurrentBalance(), 0.001);

        book.setCurrentBalance(0);

        assertTrue(book.addBalanceOperation(shopDB2, 150.33, balanceOperations));
        assertEquals(1, balanceOperations.size(), 0);

        assertTrue(book.addBalanceOperation(shopDB2, -150.33, balanceOperations));
        assertEquals(2, balanceOperations.size(), 0);

        assertFalse(book.addBalanceOperation(shopDB2, -10.0, balanceOperations));
        assertEquals(2, balanceOperations.size(), 0);

        assertFalse(book.addBalanceOperation(shopDB2, 150.33, null));

        assertFalse(book.addBalanceOperation(null, 150.33, balanceOperations));
    }
}
