package it.polito.ezshop.WhiteBoxTesting;


import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SQLiteDB;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEZShopFR1_DeleteUser {

    EZShop ez;
    Integer uId;
    private final SQLiteDB shopDB2 = new SQLiteDB();

    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        ez = new EZShop();
        shopDB2.connect();
        shopDB2.initDatabase();
        uId=ez.createUser("deleteTest", "pwd", "Administrator");
    }

    @After
    public void teardown(){
        shopDB2.deleteUser(uId);
    }

    @Test
    public void testDeleteUser() throws InvalidUserIdException, InvalidUsernameException, InvalidPasswordException, UnauthorizedException{
        try {
            ez.deleteUser(-1);
            fail("InvalidUserIdException incoming");
        } catch (InvalidUserIdException e){
            assertNotNull(e);
        }
        try {
            ez.deleteUser(7);
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }
        User u = ez.login("deleteTest", "pwd"); //Administrator
        boolean del1 = ez.deleteUser(u.getId());
        boolean del2 = ez.deleteUser(u.getId());
        assertTrue(del1);
        assertFalse(del2);
    }

}
