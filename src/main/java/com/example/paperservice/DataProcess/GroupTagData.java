package com.example.paperservice.DataProcess;

import java.io.Serializable;

public class GroupTagData implements Serializable {
    Float relation;
    Integer paperNum;
    String tagName;

    public GroupTagData(Float relation, String tagName) {
        this.relation = relation;
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setPaperNum(Integer paperNum) {
        this.paperNum = paperNum;
    }

    public void setRelation(Float relation) {
        this.relation = relation;
    }

    public Float getRelation() {
        return relation;
    }

    public Integer getPaperNum() {
        return paperNum;
    }
}
