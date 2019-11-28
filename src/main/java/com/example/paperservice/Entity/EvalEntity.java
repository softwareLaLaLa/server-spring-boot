package com.example.paperservice.Entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.persistence.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "evaluation")
public class EvalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "usr_id")
    private int usr_id;

    @Column(name = "paper_id")
    private int paper_id;

    @Column(name = "eval")
    private int eval;

    //@Column(name = "tags")
    //private String tagIDList;

    @Column(name = "date")
    private Date date;

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public int getUsr_id() {
        return usr_id;
    }

    public int getPaper_id() {
        return paper_id;
    }

    public int getEval() {
        return eval;
    }

//    public List<Integer> getTagIDList() {
//        Gson gson = new Gson();
//        Type dataListType = new TypeToken<ArrayList<Integer>>(){}.getType();
//        return  gson.fromJson(tagIDList, dataListType);
//    }

    public EvalEntity(int usr_id, int paper_id, int eval, Date date) {
        this.usr_id = usr_id;
        this.paper_id = paper_id;
        this.eval = eval;
        //this.tagIDList = tagIDList;
        this.date = date;
    }
}
