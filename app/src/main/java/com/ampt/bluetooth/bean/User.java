package com.ampt.bluetooth.bean;

/**
 * Created by malith on 7/23/15.
 */
public class User {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private long imageId;

    public User(int id, String name, String lastName, String email, String password, long imageId) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.imageId= imageId;
    }

    public User(String name, String lastName, String email, String password, long imageId) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.imageId=imageId;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}
