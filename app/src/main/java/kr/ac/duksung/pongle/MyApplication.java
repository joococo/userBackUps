package kr.ac.duksung.pongle;

import android.app.Application;

public class MyApplication extends Application {
    private String stdName;
    private String orderID;
    private String stdID;

    private String seatID;

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String value) {
        this.stdName = value;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String value) {
        this.orderID = value;
    }

    public String getStdID() {
        return stdID;
    }

    public void setStdID(String value) {
        this.stdID = value;
    }


    public String getSeatID() {
        return seatID;
    }

    public void setSeatID(String value) {
        this.seatID = value;
    }
}
