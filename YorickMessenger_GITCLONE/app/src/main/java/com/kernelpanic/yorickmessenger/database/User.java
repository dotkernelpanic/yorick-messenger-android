package com.kernelpanic.yorickmessenger.database;

public class User {
    public int id;
    public String name;
    public String surname;
    public String fullname;

    public User(String fullname) {
        this.fullname = fullname;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
