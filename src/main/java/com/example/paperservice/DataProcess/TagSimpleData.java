package com.example.paperservice.DataProcess;

import com.example.paperservice.Entity.TagEntity;

public class TagSimpleData {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TagSimpleData(TagEntity tagEntity){
        this.id = tagEntity.getId();
        this.name = tagEntity.getName();
    }
}
