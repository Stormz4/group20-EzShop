package it.polito.ezshop.data;

import java.time.LocalDate;
import java.util.LinkedList;

//import static it.polito.ezshop.data.EZShop.*;
import static it.polito.ezshop.data.EZBalanceOperation.Credit;
import static it.polito.ezshop.data.EZBalanceOperation.Debit;

import static it.polito.ezshop.data.SQLiteDB.defaultID;

public class EZAccountBook { // NOT NEEDED !!!
    double currentBalance;

    public EZAccountBook(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    boolean addBalanceOperation(Integer transactionID)
    {
        return false;
    }

    boolean updateBalance(double toBeAdded)
    {
        return false;
    }

    LinkedList<BalanceOperation> getAllTransactions()
    {
        return null;
    }

    boolean updateBalanceOperation(Integer transactionID)
    {
        return false;
    }

    public double getCurrentBalance() { return currentBalance; }

    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }
}
