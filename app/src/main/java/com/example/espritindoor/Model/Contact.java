package com.example.espritindoor.Model;

public class Contact {
    private String name;
    private String title;
    private String email;
    private String number;
    private String linkedInUrl;
    private String pictureUrl;

    public Contact(String name, String title, String email, String number, String linkedInUrl, String pictureUrl) {
        this.name = name;
        this.title = title;
        this.email = email;
        this.number = number;
        this.linkedInUrl = linkedInUrl;
        this.pictureUrl = pictureUrl;
    }

    public String getId(){
        return name + " " + number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLinkedInUrl() {
        return linkedInUrl;
    }

    public void setLinkedInUrl(String linkedInUrl) {
        this.linkedInUrl = linkedInUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

}