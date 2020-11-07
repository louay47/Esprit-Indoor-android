package com.example.espritindoor.Model;

public class user {

    private String  _id, username , email ,password , name , nom , prenom , image , Token;
    private Boolean etat ;
    private int phone ;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public Boolean getEtat() {
        return etat;
    }

    public void setEtat(Boolean etat) {
        this.etat = etat;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public user() { }

    public user(String username, String email, String password, String name, String nom, String prenom, String image, String token, Boolean etat, int phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.nom = nom;
        this.prenom = prenom;
        this.image = image;
        Token = token;
        this.etat = etat;
        this.phone = phone;
    }

    public user(String email, String password, String nom) {
        this.email = email;
        this.password = password;
        this.username = nom;
    }
}
