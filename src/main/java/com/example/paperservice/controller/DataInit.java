package com.example.paperservice.controller;

import com.example.paperservice.Entity.TagRela;
import com.example.paperservice.database.PaperDao;
import com.example.paperservice.database.RedisService;
import com.example.paperservice.database.TagDao;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataInit {
    @Autowired
    RedisService redisService; //redisServiceaddPaperTagData(int paper_id, Map<Integer, TagRela> map)
    @Autowired
    PaperDao paperDao;  //paperDap.save()
    @Autowired
    TagDao tagDao; //tagDao.save()
}
