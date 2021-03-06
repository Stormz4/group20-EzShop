package it.polito.ezshop.data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;

import static it.polito.ezshop.data.EZBalanceOperation.Credit;
import static it.polito.ezshop.data.EZBalanceOperation.Debit;

import static it.polito.ezshop.data.SQLiteDB.defaultID;

public class EZAccountBook {
    double currentBalance;
    int nextBalanceId;

    public EZAccountBook(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public boolean updateBalance(double toBeAdded)
    {
        currentBalance += toBeAdded;
        return true;
    }

    public double getCurrentBalance() { return currentBalance; }

    public void setCurrentBalance(double currentBalance) { this.currentBalance = currentBalance; }

    public boolean addBalanceOperation(SQLiteDB shopDB, double toBeAdded, HashMap<Integer, EZBalanceOperation> ezBalanceOperations)
    {
        String type;

        if((toBeAdded + this.currentBalance) < 0)
            return false;

        if(shopDB == null || ezBalanceOperations == null)
            return false;

        if(toBeAdded >= 0)
            type = Credit;
        else
            type = Debit;

        LocalDate time = LocalDate.now();
        // create new balance operation
        EZBalanceOperation op = new EZBalanceOperation(defaultID, time, toBeAdded, type);
        int id = shopDB.insertBalanceOperation(time, toBeAdded, type);
        if(id == -1) return false; //return false if DB connection problem occurs
        op.setBalanceId(id);
        ezBalanceOperations.put(op.getBalanceId(), op);
        nextBalanceId = op.getBalanceId();

        this.updateBalance(toBeAdded);

        return true;
    }
}
