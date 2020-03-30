package com.example.paperservice.DataProcess;

import com.example.paperservice.Entity.PaperEntity;

import java.io.Serializable;
import java.util.Map;

public class AddedPaperData implements Serializable {
    PaperEntity paperEntity;
    Map<Integer, Float> relation;

    public PaperEntity getPaperEntity() {
        return paperEntity;
    }

    public Map<Integer, Float> getRelation() {
        return relation;
    }

    public void setPaperEntity(PaperEntity paperEntity) {
        this.paperEntity = paperEntity;
    }

    public void setRelation(Map<Integer, Float> relation) {
        this.relation = relation;
    }
}
