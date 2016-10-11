package com.saivision.collection.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 11/10/2016.
 */

public class CustomerPOJO implements Parcelable {
    @SerializedName("cnt")
    @Expose
    private String cnt;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("remaining_amount")
    @Expose
    private String remainingAmount;
    @SerializedName("payment_date")
    @Expose
    private String paymentDate;

    /**
     *
     * @return
     * The cnt
     */
    public String getCnt() {
        return cnt;
    }

    /**
     *
     * @param cnt
     * The cnt
     */
    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    /**
     *
     * @return
     * The fname
     */
    public String getFname() {
        return fname;
    }

    /**
     *
     * @param fname
     * The fname
     */
    public void setFname(String fname) {
        this.fname = fname;
    }

    /**
     *
     * @return
     * The lname
     */
    public String getLname() {
        return lname;
    }

    /**
     *
     * @param lname
     * The lname
     */
    public void setLname(String lname) {
        this.lname = lname;
    }

    /**
     *
     * @return
     * The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     *
     * @param phoneNumber
     * The phone_number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     *
     * @return
     * The remainingAmount
     */
    public String getRemainingAmount() {
        return remainingAmount;
    }

    /**
     *
     * @param remainingAmount
     * The remaining_amount
     */
    public void setRemainingAmount(String remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    /**
     *
     * @return
     * The paymentDate
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     *
     * @param paymentDate
     * The payment_date
     */
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cnt);
        dest.writeString(this.fname);
        dest.writeString(this.lname);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.remainingAmount);
        dest.writeString(this.paymentDate);
    }

    public CustomerPOJO() {
    }

    protected CustomerPOJO(Parcel in) {
        this.cnt = in.readString();
        this.fname = in.readString();
        this.lname = in.readString();
        this.phoneNumber = in.readString();
        this.remainingAmount = in.readString();
        this.paymentDate = in.readString();
    }

    public static final Parcelable.Creator<CustomerPOJO> CREATOR = new Parcelable.Creator<CustomerPOJO>() {
        @Override
        public CustomerPOJO createFromParcel(Parcel source) {
            return new CustomerPOJO(source);
        }

        @Override
        public CustomerPOJO[] newArray(int size) {
            return new CustomerPOJO[size];
        }
    };
}
