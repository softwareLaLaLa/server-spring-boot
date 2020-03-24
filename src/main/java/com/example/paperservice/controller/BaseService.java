package com.example.paperservice.controller;

import com.example.paperservice.DataProcess.PaperSimpleData;
import com.example.paperservice.DataProcess.TagRela;
import com.example.paperservice.Entity.PaperEntity;
import com.example.paperservice.database.PaperDao;
import com.example.paperservice.database.RedisService;
import com.example.paperservice.database.TagDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.print.Paper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BaseService {
    @Autowired
    private PaperDao paperDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private RedisService redisService;

    public PaperSimpleData getPaperSimpleData(PaperEntity paperEntity){
        Map<Integer, TagRela> tagRelaMap = redisService.getPaperTagData(paperEntity.getId());
        List<String> tagList = new ArrayList<>();
        for (Integer i : tagRelaMap.keySet()) {
            tagList.add(tagDao.findById((int) i).getName());
        }
        return new PaperSimpleData(paperEntity,tagList);
    }
}
