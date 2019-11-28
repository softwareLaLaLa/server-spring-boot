package com.example.paperservice.Entity;

import java.util.List;

public class BrowseHistory {
    List<PaperSimpleData> browsePaperData;
    int usr_id;

    public List<PaperSimpleData> getBrowsePaperData() {
        return browsePaperData;
    }

    public int getUsr_id() {
        return usr_id;
    }

    public void setBrowsePaperData(List<PaperSimpleData> browsePaperData) {
        this.browsePaperData = browsePaperData;
    }

    public void setUsr_id(int usr_id) {
        this.usr_id = usr_id;
    }

    public BrowseHistory(List<PaperSimpleData> browsePaperData, int usr_id) {
        this.browsePaperData = browsePaperData;
        this.usr_id = usr_id;
    }
}
