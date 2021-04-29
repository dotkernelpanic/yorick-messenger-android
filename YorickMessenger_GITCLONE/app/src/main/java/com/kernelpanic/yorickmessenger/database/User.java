package com.kernelpanic.yorickmessenger.database;

public class User {
    public int id;
    public String name;
    public String fullname;

    public User(String fullname) {
        this.fullname = fullname;
    }

    //Empty constructor
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

}
