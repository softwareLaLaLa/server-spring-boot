package com.example.paperservice.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "paperHotData")
public class HotPaperEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "hot")
    private int hot;

    @Column(name = "activeTime")
    private Date lastActiveTime;

    public void setHot(int hot) {
        this.hot = hot;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public int getHot() {
        return hot;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    public int getId() {
        return id;
    }

    public HotPaperEntity(int hot, Date lastActiveTime) {
        this.hot = hot;
        this.lastActiveTime = lastActiveTime;
    }

    public HotPaperEntity(){}

    public void setId(int id) {
        this.id = id;
    }
}
