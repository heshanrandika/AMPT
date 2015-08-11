package com.ampt.bluetooth.database.model;

/**
 * Created by Heshanr on 4/15/2015.
 */
public class DogsData {
    private long id;
    private String name;
    private String imageID;
    private boolean status;
    private String device_name;
    private String device_address;
    private int age;
    private String created_at;
    private String goal;
    private String gender;
    private String dob;
    private String breed;

    public DogsData() {
    }

    public DogsData(String name, String device_name, String device_address) {
        this.name = name;
        this.device_name = device_name;
        this.device_address = device_address;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviceAddress() {
        return device_address;
    }

    public void setDeviceAddress(String device_address) {
        this.device_address = device_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDeviceName() {
        return device_name;
    }

    public void setDeviceName(String device_name) {
        this.device_name = device_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}