package com.example.espritindoor.Model;

public class Notification {
    private String nameNotification;
    private  String descriptionNotification;
    private int typeNotification;

    public Notification(String nameNotification, String descriptionNotification, int typeNotification) {
        this.nameNotification = nameNotification;
        this.descriptionNotification = descriptionNotification;
        this.typeNotification = typeNotification;
    }



    public String getNameNotification() {
        return nameNotification;
    }

    public String getDescriptionNotification() {
        return descriptionNotification;
    }

    public int getTypeNotification() {
        return typeNotification;
    }

    public void setNameNotification(String nameNotification) {
        this.nameNotification = nameNotification;
    }

    public void setDescriptionNotification(String descriptionNotification) {
        this.descriptionNotification = descriptionNotification;
    }

    public void setTypeNotification(int typeNotification) {
        this.typeNotification = typeNotification;
    }

}
