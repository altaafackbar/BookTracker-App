/**
*NotificationMessage
* Used to create a notification object to be stored in database
* Notification contains title, message, and string of date received
 */
package com.example.booktracker;

public class NotificationMessage {
    private String title;
    private String message;
    private String receiveDate;

    public NotificationMessage(String title, String message, String receiveDate) {
        //Constructor
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
