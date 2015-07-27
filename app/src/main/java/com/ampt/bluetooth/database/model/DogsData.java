package com.ampt.bluetooth.database.model;

/**
 * Created by Heshanr on 4/15/2015.
 */
public class DogsData {
    int id;
    String name;
    byte[] image;
    boolean status;
    String device_name;
    String device_address;
    int age;
    String created_at;
    String goal;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
}