package com.example.paperservice.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tag")
public class TagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "groupIDList")
    private String groupIDList;

    @Column(name = "usedNum")
    private int num;

    @Column(name = "date")
    private Date date;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public Date getDate() {
        return date;
    }

    public String getGroupIDList() {
        return groupIDList;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TagEntity(String name, String groupIDList, int num, Date date) {
        this.name = name;
        this.groupIDList = groupIDList;
        this.num = num;
        this.date = date;
    }
}
