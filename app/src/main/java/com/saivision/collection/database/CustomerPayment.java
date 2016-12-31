package com.saivision.collection.database;

import io.realm.RealmObject;

/**
 * Created by Admin on 31/12/2016.
 */

public class CustomerPayment extends RealmObject {
    private String id;
    private String amount;
    private String mode;
    private String chequeNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }
}
