package com.example.paperservice.Entity;

import java.util.Map;

public class InitialUserTagData {
    private int usr_id;
    private Map<Integer, Float> tagData;

    public InitialUserTagData(int usr_id, Map<Integer, Float> tagData) {
        this.usr_id = usr_id;
        this.tagData = tagData;
    }

    public int getUsr_id() {
        return usr_id;
    }

    public Map<Integer, Float> getTagData() {
        return tagData;
    }
}
