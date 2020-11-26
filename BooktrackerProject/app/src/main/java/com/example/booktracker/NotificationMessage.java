package com.example.booktracker;

import java.util.Date;

public class NotificationMessage {
    private String title;
    private String message;
    private String receiveDate;

    public NotificationMessage(String title, String message, String receiveDate) {
        this.title = title;
        this.message = message;
        this.receiveDate = receiveDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }
}
