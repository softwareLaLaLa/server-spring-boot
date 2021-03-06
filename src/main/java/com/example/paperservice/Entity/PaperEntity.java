package com.example.paperservice.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "paper")
public class PaperEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "abst")
    private String abst;

    @Column(name = "resUrl")
    private String resUrl;

    //用户浏览次数
    @Column(name = "browseNum")
    private int browseNum;

    //用户添加tag次数
    @Column(name = "evalNum")
    private int evalNum;

    //未check数据次数
    @Column(name = "uncheckNum")
    private int uncheckNum;

    @Column(name = "groupId")
    private int groupId;

    public int getUncheckNum() {
        return uncheckNum;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAbst() {
        return abst;
    }

    public String getResUrl() {
        return resUrl;
    }

    public int getBrowseNum() {
        return browseNum;
    }

    public int getEvalNum() {
        return evalNum;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBrowseNum(int browseNum) {
        this.browseNum = browseNum;
    }

    public void setEvalNum(int evalNum) {
        this.evalNum = evalNum;
    }

    public void setUncheckNum(int uncheckNum) {
        this.uncheckNum = uncheckNum;
    }

    public int getGroupID() {
        return groupId;
    }

    public void setGroupID(int groupId) {
        this.groupId = groupId;
    }

    public PaperEntity(int id, String title, String abst, String resUrl, int browseNum, int evalNum, int uncheckNum, int groupID) {
        this.id = id;
        this.title = title;
        this.abst = abst;
        this.resUrl = resUrl;
        this.browseNum = browseNum;
        this.evalNum = evalNum;
        this.uncheckNum = uncheckNum;
        this.groupId = groupID;
    }

    public PaperEntity(String title, String abst, String resUrl, int browseNum, int evalNum, int uncheckNum, int groupID) {
        this.title = title;
        this.abst = abst;
        this.resUrl = resUrl;
        this.browseNum = browseNum;
        this.evalNum = evalNum;
        this.uncheckNum = uncheckNum;
        this.groupId = groupID;
    }

    public PaperEntity(String title, String abst, String resUrl, int browseNum, int evalNum, int uncheckNum) {
        this.title = title;
        this.abst = abst;
        this.resUrl = resUrl;
        this.browseNum = browseNum;
        this.evalNum = evalNum;
        this.uncheckNum = uncheckNum;
    }

    public PaperEntity(){}
}
