package com.example.paperservice.Entity;

import java.util.Date;

public class UserTagData {
    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public String getTag() {
        return tag;
    }

    public float getValue() {
        return value;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    String tag;
    float value;
    Date lastActiveTime;
}
