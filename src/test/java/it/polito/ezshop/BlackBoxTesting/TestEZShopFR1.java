package it.polito.ezshop.BlackBoxTesting;



import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestEZShopFR1 {

    EZShop ez;
    Integer uId;
    private final SQLiteDB shopDB2 = new SQLiteDB();
    Integer created1;


    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        ez = new EZShop();
        shopDB2.connect();
        shopDB2.initDatabase();
        created1 = null;
        uId=ez.createUser("deleteTest", "pwd", "Administrator");
    }

    @After
    public void teardown(){
        shopDB2.deleteUser(uId);
        if (created1 != null){
            shopDB2.deleteUser(created1);
        }
    }

    // Order: create->update->delete
    @Test
    public void testcreateUser() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException{
        try {
            ez.createUser(null, "pwd", "ShopManager");
            fail("InvalidUsernameException incoming");
        } catch (InvalidUsernameException e){
            assertNotNull(e);
        }
        try {
            ez.createUser("soloequijoin", null, "ShopManager");
            fail("InvalidPasswordException incoming");
        } catch (InvalidPasswordException e){
            assertNotNull(e);
        }
        try {
            ez.createUser("soloequijoin", "pwd", "Director");
            fail("InvalidRoleException incoming");
        } catch (InvalidRoleException e){
            assertNotNull(e);
        }

        // Duplicate username: -1
        created1 = ez.createUser("deleteTest3", "pwd", "Administrator");
        //ez.createUser("theCashier", "pwd");
        Integer created2 = ez.createUser("deleteTest3", "pwd", "Cashier");
        // will give false after a second try: always run deleteTest after createTest
        assertNotEquals(-1, (int) created1);
        assertEquals(-1, (int) created2);
    }
    @Test
    public void testLogin() throws InvalidUsernameException, InvalidPasswordException{
        try {
            ez.login(null, "pwd");
            fail("InvalidUsernameException incoming");
        } catch (InvalidUsernameException e){
            assertNotNull(e);
        }
        try {
            ez.login("soloequijoin5", null);
            fail("InvalidPasswordException incoming");
        } catch (InvalidPasswordException e){
            assertNotNull(e);
        }

        User u =ez.login("deleteTest", "pwd"); //Administrator
        assertNotNull(u);
    }

    @Test
    public void testUpdateUserRight() throws  InvalidUsernameException, InvalidPasswordException, InvalidUserIdException, InvalidRoleException, UnauthorizedException{
        EZShop ez = new EZShop();
        try {
            ez.updateUserRights(-5, "Administrator");
        } catch (InvalidUserIdException e){
            assertNotNull(e);
        }
        try {
            ez.updateUserRights(1, "Leader");
            fail("InvalidRoleException incoming");
        } catch (InvalidRoleException e){
            assertNotNull(e);
        }
        try {
            ez.updateUserRights(1, "ShopManager");
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }

        ez.login("deleteTest", "pwd"); //Administrato
        boolean upd1= ez.updateUserRights(1, "Administrator");
        boolean upd2= ez.updateUserRights(10000, "Administrator");
        assertTrue(upd1);
        assertFalse(upd2);
    }

    @Test
    public void testGetAllUsers() throws InvalidUsernameException, InvalidPasswordException, UnauthorizedException {
        EZShop ez = new EZShop();
        try {
            ez.getAllUsers();
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }
        ez.login("deleteTest", "pwd"); //Administrator
        System.out.println(ez.getAllUsers());
    }

    @Test
    public void testgetUser() throws InvalidUsernameException, InvalidPasswordException, InvalidUserIdException, UnauthorizedException{
        EZShop ez = new EZShop();
        try{
            ez.getUser(-5);
            fail("InvalidUserIdException incoming");
        } catch (InvalidUserIdException e){
            assertNotNull(e);
        }
        try {
            ez.getAllUsers();
            fail("UnauthorizedException incoming");
        } catch (UnauthorizedException e){
            assertNotNull(e);
        }
        User u = ez.login("deleteTest", "pwd"); //Administrator
        User get1 = ez.getUser(u.getId());
        User get2 = ez.getUser(1000000);
        assertNotNull(get1);
        assertNull(get2);
    }
}
