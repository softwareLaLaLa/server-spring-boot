package com.example.paperservice.DataProcess;

import com.example.paperservice.Entity.PaperEntity;

import java.util.ArrayList;
import java.util.List;

public class PaperData {
    private String abst;
    private String resUrl;
    private int groupId;
    private List<TagSimpleData> existTag = new ArrayList<>();
    private List<TagSimpleData> recomTag = new ArrayList<>();

    public PaperData(PaperEntity paperEntity, List<List<TagSimpleData>> tagData){
        this.abst = paperEntity.getAbst();
        this.resUrl = paperEntity.getResUrl();
        this.groupId = paperEntity.getGroupID();
        existTag = tagData.get(0);
        recomTag = tagData.get(1);
    }
}
