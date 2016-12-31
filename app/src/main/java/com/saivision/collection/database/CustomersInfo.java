package com.saivision.collection.database;

import io.realm.RealmObject;

/**
 * Created by Admin on 11/10/2016.
 */

public class CustomersInfo extends RealmObject {
    private String cnt;
    private String fname;
    private String lname;
    private String phoneNumber;
    private String remainingAmount;
    private String paymentDate;
    private String boxNo;

    /**
     * @return The cnt
     */
    public String getCnt() {
        return cnt;
    }

    /**
     * @param cnt The cnt
     */
    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    /**
     * @return The fname
     */
    public String getFname() {
        return fname;
    }

    /**
     * @param fname The fname
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     * @return The lname
     */
    public String getLname() {
        return lname;
    }

    /**
     * @param lname The lname
     */
    public void setLname(String lname) {
        this.lname = lname;
    }

    /**
     * @return The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber The phone_number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return The remainingAmount
     */
    public String getRemainingAmount() {
        return remainingAmount;
    }

    /**
     * @param remainingAmount The remaining_amount
     */
    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    /**
     * @return The paymentDate
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     * @param paymentDate The payment_date
     */
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(String boxNo) {
        this.boxNo = boxNo;
    }
}
