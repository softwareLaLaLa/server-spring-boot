package com.example.paperservice.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PaperSimpleData implements Serializable {
    public void setTitle(String title) {
        this.title = title;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public int getPaper_id() {
        return paper_id;
    }

    public void setPaper_id(int paper_id) {
        this.paper_id = paper_id;
    }

    public int getTotalBrowseNum() {
        return totalBrowseNum;
    }

    public void setTotalBrowseNum(int totalBrowseNum) {
        this.totalBrowseNum = totalBrowseNum;
    }

    public void addTag(String tag){
        tagList.add(tag);
    }

    public PaperSimpleData(PaperEntity paperEntity, List<String> tagList) {
        this.paper_id = paperEntity.getId();
        //this.abst = paperEntity.getAbst();
        this.title = paperEntity.getTitle();
        this.totalBrowseNum = paperEntity.getBrowseNum();
        this.tagList = tagList;
    }

    int paper_id;
    String title;
    int totalBrowseNum;
    List<String> tagList = new ArrayList<>();
    //String abst;

//    public void setAbst(String abst) {
//        this.abst = abst;
//    }
//
//    public String getAbst() {
//        return abst;
//    }
}
