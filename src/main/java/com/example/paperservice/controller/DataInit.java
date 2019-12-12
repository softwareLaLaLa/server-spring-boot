package com.example.paperservice.controller;

import com.example.paperservice.Entity.PaperEntity;
import com.example.paperservice.Entity.TagEntity;
import com.example.paperservice.Entity.TagRela;
import com.example.paperservice.database.PaperDao;
import com.example.paperservice.database.RedisService;
import com.example.paperservice.database.TagDao;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.io.*;

@Component
public class DataInit {
    @Autowired
    RedisService redisService; // redisService.addPaperTagData(int paper_id, Map<Integer, TagRela> map)
    @Autowired
    PaperDao paperDao; // paperDap.save()
    @Autowired
    TagDao tagDao; // tagDao.save()

    private static final String[] txtNames = { "航空航天+超音速客机+音爆.txt", "航空航天+复合材料+化学.txt", "航空航天+空间对接+动力学+仿真技术.txt",
            "航空航天+微波光子+卫星通信.txt", "航空航天+医学+人体工程.txt", "化学+复合材料+表面处理.txt", "化学+核事故+应急机制.txt", "化学+化学污染+生态环境.txt",
            "化学+能源科学+专业教学.txt", "化学+生物学+细胞自噬.txt", "医学+仿真技术+实验教学.txt", "医学+禽流感+化学+遗传进化.txt", "医学+人体冷冻+伦理学+法律.txt",
            "医学+数值模拟+人工心脏+磁悬浮.txt", "医学+医患关系+公共治理.txt" };

    public void initData() {
        
        Set<String> existTags = new HashSet<>();
        for (String txtName : txtNames) {
            File file = new File("paperinfos\\" + txtName);
            String[] tags=txtName.split("\\+|\\.");
            
            Map<Integer, TagRela> paperTag = new HashMap<>();
            //Create and insert tags
            for(int i=0;i<tags.length-1;i++){
                //Prevent duplicate inserts of tags
                if(existTags.contains(tags[i])){
                    continue;
                }
                existTags.add(tags[i]);
                TagEntity aTag=new TagEntity(tags[i], "", 1, new Date());
                tagDao.save(aTag);
                Random random = new Random();
                float value = random.nextInt(4);
                value = (value+8)/10;
                paperTag.put(aTag.getId(),new TagRela(1,value));
            }


            try {
                FileReader reader = new FileReader(file);
                int fileLen = (int) file.length();
                char[] chars = new char[fileLen];
                try {
                    reader.read(chars);
                    // Get all the file's content
                    String allInfos = (String.valueOf(chars));
                    // Split each of the papers
                    String[] papers = allInfos.split("\\$");

                    for (String paper : papers) {
                        // Split the title, download link and abstract of each paper
                        String[] infos = paper.split("@");
                        // Create and insert each paper
                        PaperEntity aPaper = new PaperEntity(infos[0], infos[2], infos[1], 0, 1, 0);
                        paperDao.save(aPaper);
                        
                        //insert paper's tags
                        redisService.addPaperTagData(aPaper.getId(),paperTag);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

}
