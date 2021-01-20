package com.iti.project;

import org.json.simple.JSONObject;

import java.sql.Blob;
import java.sql.Date;

public class Player {

    private int id;
    private String name;
    private String userName;
    private String password;
    private String email;
    private String gender;
    private String status;
    private Blob avatar;
    private int score;
    private Date lastLogin;

    public Player(){
        this.id = -1;
        this.name = null;
        this.userName = null;
        this.password = null;
        this.email = null;
        this.gender = null;
        this.status = null;
        this.avatar = null;
        this.score = -1;
        this.lastLogin = null;
    }

    // This will be send to other clients
    public Player(String userName, int score){
        this.userName = userName;
        this.score = score;
        this.status = "Offline";
    }

    // This will be stored on the server
    public Player(int id, String name, String userName, String password,
                  String email, String gender, String status,  Blob avatar, int score, Date lastLogin){
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.status = status;
        this.avatar = avatar;
        this.score = score;
        this.lastLogin = lastLogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Blob getAvatar() {
        return avatar;
    }

    public void setAvatar(Blob avatar) {
        this.avatar = avatar;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Player getPlayerToSendToClient(){
        return new Player(this.userName, this.score);
    }

    public JSONObject toJson(){
        JSONObject playerJson = new JSONObject();
        playerJson.put("userName", this.userName);
        playerJson.put("score", this.score);
        playerJson.put("status", this.status);
        return playerJson;
    }

    public String toString(){
        return ("User name: " + this.userName + " , Score: " + this.score);
    }
}
