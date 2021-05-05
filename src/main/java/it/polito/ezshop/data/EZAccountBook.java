package it.polito.ezshop.data;

import java.util.LinkedList;

public class EZAccountBook {
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
        if(toBeAdded >= 0)
        {
            // TODO: Add this balance operation as a CREDIT in DB
        }
        else
        {
            // TODO: Add this balance operation as a DEBIT in DB
        }

        return !((toBeAdded + this.currentBalance) < 0);
    }

    LinkedList<BalanceOperation> getAllTransactions()
    {
        return null;
    }

    boolean updateBalanceOperation(Integer transactionID)
    {
        return false;
    }
}
