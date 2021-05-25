package it.polito.ezshop.IntegrationBBTesting;

import it.polito.ezshop.data.Customer;
import it.polito.ezshop.data.EZCustomer;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SQLiteDB;
import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestEZShopFR5 {
    // FR 5: manage customers and cards
    EZShop ez;
    Integer uId;
    Integer cId;
    Integer cId2;
    Integer cId3;
    Integer cId4;
    private final SQLiteDB shopDB2 = new SQLiteDB();

    @Before
    public void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        ez = new EZShop();
        shopDB2.connect();
        shopDB2.initDatabase();
        /*
        boolean cleared = shopDB2.clearTable(SQLiteDB.tBalanceOperations);
        cleared &= shopDB2.clearTable(SQLiteDB.tCards);
        */
        uId=ez.createUser("testFR5", "pwd", "Administrator");
    }

    @After
    public void teardown(){
        shopDB2.deleteUser(uId);
        shopDB2.deleteCustomer(cId);
        shopDB2.deleteCustomer(cId2);
        shopDB2.deleteCustomer(cId3);
        shopDB2.deleteCustomer(cId4);
        shopDB2.closeConnection();
    }

    @Test
    public void testCustomerEZShop() throws InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidCustomerCardException {
        assertThrows(UnauthorizedException.class, () -> {
            cId=ez.defineCustomer("customer");
        });
        assertThrows(UnauthorizedException.class, () -> {
            ez.deleteCustomer(2);
        });
        assertThrows(UnauthorizedException.class, () -> {
            ez.modifyCustomer(2, "testModify", "000000099");
        });
        assertThrows(UnauthorizedException.class, () -> {
            ez.getCustomer(cId);
        });
        assertThrows(UnauthorizedException.class, () -> {
            ez.createCard();
        });

        ez.login("testFR5", "pwd");
        assertThrows(InvalidCustomerNameException.class, () -> {
            cId=ez.defineCustomer("");
        });
        assertThrows(InvalidCustomerNameException.class, () -> {
            cId=ez.defineCustomer(null);
        });

        cId=ez.defineCustomer("testFR5Define");

        assertNotEquals(-1, (int) cId);

        // Can't add duplicate customer
        cId2=ez.defineCustomer("testFR5Define");

        assertEquals(-1, (int) cId2 );

        // -- MODIFY

        assertThrows(InvalidCustomerNameException.class, () -> {
            ez.modifyCustomer(cId,"", "000000099");
        });
        assertThrows(InvalidCustomerNameException.class, () -> {
            ez.modifyCustomer(cId,null,"000000099");
        });
        assertThrows(InvalidCustomerCardException.class, () -> {
            ez.modifyCustomer(cId,"test2","000099");
        });

        String card = ez.createCard();
        EZCustomer c = (EZCustomer) ez.getCustomer(cId);

        // Customer c has yet to obtain a card
        assertFalse(card == c.getCustomerCard());

        boolean modifySuccess = ez.modifyCustomer(cId, "testFRDefine2", card);
        assertTrue(modifySuccess);

        //Test if there is a check for duplicate cards
        cId2 = ez.defineCustomer("testModifyCards");
        boolean modifyFalse = ez.modifyCustomer(cId2, "testCard2", card);

        assertFalse(modifyFalse);

        //Modify with a null card: the card shouldn't be modified

        boolean modifyCard = ez.modifyCustomer(cId, "testFRDefine3", null);
        c = (EZCustomer) ez.getCustomer(cId);
        assertTrue(c.getCustomerCard().equals(card));
        assertTrue(modifyCard);

        //Modify with an empty card: the card should be deleted
        modifyCard = ez.modifyCustomer(cId, "testFRDefine3", "");
        c = (EZCustomer) ez.getCustomer(cId);
        assertTrue(c.getCustomerCard().equals(""));
        assertTrue(modifyCard);


        // -- DELETE
        // Delete on an id not valid
        assertThrows(InvalidCustomerIdException.class, () -> {
            ez.deleteCustomer(null);
        });
        assertThrows(InvalidCustomerIdException.class, () -> {
            ez.deleteCustomer(-1);
        });

        List<Customer> list = ez.getAllCustomers();
        assertTrue(list.size()>0);

        // Id not present
        boolean deleteFalse = ez.deleteCustomer(10000000);
        assertFalse(deleteFalse);
        boolean deleteSuccess = ez.deleteCustomer(cId);
        assertTrue(deleteSuccess);

        ez.logout();
    }

    @Test
    public void testCardEZShop() throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidCustomerNameException {
        assertThrows(UnauthorizedException.class, () -> {
            ez.attachCardToCustomer("000000099", 1);
        });
        assertThrows(UnauthorizedException.class, () -> {
            ez.modifyPointsOnCard("000000099", 1);
        });

        // ---- ATTACH CARD TO CUSTOMER ----

        ez.login("testFR5", "pwd");

        // Null or negative ID
        assertThrows(InvalidCustomerIdException.class, () -> {
            ez.attachCardToCustomer("000000099", -1);
        });
        assertThrows(InvalidCustomerIdException.class, () -> {
            ez.attachCardToCustomer("000000099", null);
        });

        // Empty, null or non valid string
        assertThrows(InvalidCustomerCardException.class, () -> {
            ez.attachCardToCustomer("", 1);
        });
        assertThrows(InvalidCustomerCardException.class, () -> {
            ez.attachCardToCustomer(null, 1);
        });
        assertThrows(InvalidCustomerCardException.class, () -> {
            ez.attachCardToCustomer("22", 1);
        });


        String card = ez.createCard();
        cId3 = ez.defineCustomer("TestWithCards");

        // false if customer with given id doesn't exist
        boolean attachFailure = ez.attachCardToCustomer(card, 100000000);
        assertFalse(attachFailure);

        // valid attach
        boolean attachSuccess = ez.attachCardToCustomer(card, cId3);
        assertTrue(attachSuccess);

        cId4 = ez.defineCustomer("TestCards2");
        // false since the same card already exists
        attachFailure = ez.attachCardToCustomer(card, cId4);

        assertFalse(attachFailure);

        // ---- MODIFY POINTS ON CARD ----
        // Empty, null or non valid string
        assertThrows(InvalidCustomerCardException.class, () -> {
            ez.modifyPointsOnCard("", 1);
        });
        assertThrows(InvalidCustomerCardException.class, () -> {
            ez.modifyPointsOnCard(null, 1);
        });
        assertThrows(InvalidCustomerCardException.class, () -> {
            ez.modifyPointsOnCard("22", 1);
        });

        //    false   if there is no card with given code,
        boolean modifyFalse = ez.modifyPointsOnCard("1000000000", 10);
        assertFalse(modifyFalse);

        //     *      if pointsToBeAdded is negative and there were not enough points on that card before this operation

        modifyFalse = ez.modifyPointsOnCard(card, -20000);
        assertFalse(modifyFalse);

        EZCustomer c = (EZCustomer) ez.getCustomer(cId3);
        int pointsActual = c.getPoints();
        // True modify
        boolean modifyTrue = ez.modifyPointsOnCard(card, 1500);
        assertTrue(modifyTrue);

        int points = c.getPoints();
        assertTrue(points ==1500+pointsActual);

        modifyTrue = ez.modifyPointsOnCard(card, -50);
        assertTrue(modifyTrue);

        int pointsAfter = c.getPoints();

        assertTrue(pointsAfter == points-50);


    }

}
