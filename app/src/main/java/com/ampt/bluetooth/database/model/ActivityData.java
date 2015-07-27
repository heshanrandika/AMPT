package com.ampt.bluetooth.database.model;

/**
 * Created by Heshanr on 4/15/2015.
 */
public class ActivityData {

    int id;
    int dog_id;
    int play;
    int walk;
    int swimming;
    int sleep;
    String created_at;

    public ActivityData() {
    }

    public ActivityData(int id) {
        this.id = id;
    }

    public ActivityData(int play, int walk, int swimming, int sleep, String created_at) {
        this.play = play;
        this.walk = walk;
        this.swimming = swimming;
        this.sleep = sleep;
        this.created_at = created_at;
    }

    public ActivityData(int id, int dog_id, int play, int walk, int swimming, int sleep, String created_at) {
        this.id = id;
        this.dog_id = dog_id;
        this.play = play;
        this.walk = walk;
        this.swimming = swimming;
        this.sleep = sleep;
        this.created_at = created_at;
    }

    public int getDogId() {
        return dog_id;
    }

    public void setDogId(int dog_id) {
        this.dog_id = dog_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlay() {
        return play;
    }

    public void setPlay(int play) {
        this.play = play;
    }

    public int getWalk() {
        return walk;
    }

    public void setWalk(int walk) {
        this.walk = walk;
    }

    public int getSwimming() {
        return swimming;
    }

    public void setSwimming(int swimming) {
        this.swimming = swimming;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }
}
