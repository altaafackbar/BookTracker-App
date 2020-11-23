package com.example.booktracker;

import java.util.Date;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private String status;
    private String image;
    private String owner;
    private String requestStatus;
    private Date requestDate;
    private String requester;

    public Book(String title, String author, String isbn, String status, String owner) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
        this.owner = owner;
        this.requestStatus = null;
        this.requestDate = null;
        this.requester = null;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
