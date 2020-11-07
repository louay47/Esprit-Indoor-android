package com.example.espritindoor.Model;

import java.util.List;

public class Salle {

    private List<Comment> comments ;
    private String salleName ;
    private String description ;
    private String floor ;
    private  float lat ;
    private float lon ;

    public Salle(List<Comment> comments, String salleName, String description, String floor, float lat, float lon) {
        this.comments = comments;
        this.salleName = salleName;
        this.description = description;
        this.floor = floor;
        this.lat = lat;
        this.lon = lon;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getSalleName() {
        return salleName;
    }

    public void setSalleName(String salleName) {
        this.salleName = salleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
}
