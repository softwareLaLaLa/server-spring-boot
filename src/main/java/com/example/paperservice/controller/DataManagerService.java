package com.example.paperservice.controller;

import com.example.paperservice.DataProcess.GroupTagData;
import com.example.paperservice.Entity.PaperEntity;
import com.example.paperservice.DataProcess.PaperSimpleData;
import com.example.paperservice.Entity.TagEntity;
import com.example.paperservice.DataProcess.TagRela;
import com.example.paperservice.database.PaperDao;
import com.example.paperservice.database.RedisService;
import com.example.paperservice.database.TagDao;
import com.example.paperservice.util.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class DataManagerService {
    @Autowired
    private PaperDao paperDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BaseService baseService;

    //添加一个新的论文，计算论文所在的group，在数据库中添加paper信息与papertag信息
    public void addNewPapers(Map<PaperEntity, Map<Integer, Float>> newPaperData) throws IOException {
        //生成paper relation矩阵
        List<List<Float>> paperRelationData = getRelationData((List<Map<Integer, Float>>) newPaperData.values());

        //生成group relation矩阵
        List<List<Float>> groupRelationData = getRelationData((List<Map<Integer, Float>>) redisService.getAllGroupTagData().values());

        List<Integer> groupIDList = redisService.getAllGroupID();

        List<Integer> groupNum = Calculator.distance(paperRelationData, groupRelationData);
        int count = 0;
        for(Map.Entry<PaperEntity, Map<Integer, Float>> paperData: newPaperData.entrySet()){
            paperData.getKey().setGroupID(groupIDList.get(groupNum.get(count++)));
            paperDao.save(paperData.getKey());
            Map<Integer, TagRela> paperTagData = new HashMap<>();
            for(Map.Entry<Integer, Float> entry: paperData.getValue().entrySet()){
                paperTagData.put(entry.getKey(), new TagRela(1, entry.getValue()));
            }
            redisService.addPaperTagData(paperData.getKey().getId(), paperTagData);
        }
    }

    private List<List<Float>> getRelationData(List<Map<Integer, Float>> data){
        List<Integer> tagIDList = tagDao.findAllId();
        List<List<Float>> relationData = new ArrayList<>();
        for(Map<Integer,Float> entry: data){
            List<Float> currentGroupRelationData = new ArrayList<>();
            for(Integer tagID: tagIDList){
                if(!entry.containsKey(tagID)) {
                    currentGroupRelationData.add(new Float(0));
                }else{
                    currentGroupRelationData.add(entry.get(tagID));
                }
            }
            relationData.add(currentGroupRelationData);
        }
        return relationData;
    }

    public void addNewTag(List<String> tags){
        for(String tag: tags){
            TagEntity tagEntity = null;
            if(!tagDao.existsByName(tag)){
                tagEntity = new TagEntity(tag, "", 0, new Date());
                tagDao.save(tagEntity);
            }else{
                tagEntity = tagDao.findByName(tag);
                tagEntity.setNum(tagEntity.getNum()+1);
                tagEntity.setDate(new Date());
                tagDao.save(tagEntity);
            }
        }
    }

    public void deletePaper(List<Integer> paperIDList){
        for(Integer paperID: paperIDList){
            paperDao.deleteById(paperID);
            redisService.deletePaperTagData(paperID);
        }
    }

    public void deleteTag(Set<Integer> TagIDList){
        redisService.deleteTagData(TagIDList);
        for(Integer tagID: TagIDList){
            tagDao.deleteById(tagID);
        }
    }

    public void mergeTag(List<Integer> tagID){
        TagEntity goalTag = tagDao.findById((int)tagID.get(0));
        TagEntity opeTag = tagDao.findById((int)tagID.get(1));
        goalTag.setNum(goalTag.getNum()+opeTag.getNum());
        if(!goalTag.getDate().after(opeTag.getDate())){
            goalTag.setDate(opeTag.getDate());
        }

        redisService.mergeAllTagData(tagID);
    }

    public List<PaperSimpleData> getPaperData(Integer pageNum){
        int size = 10;
        List<PaperSimpleData> paperData = new ArrayList<>();
        List<PaperEntity> paperList = paperDao.findPaperEntities(PageRequest.of(pageNum, size));
        for(PaperEntity paperEntity: paperList) {
            paperData.add(baseService.getPaperSimpleData(paperEntity));
        }
        return paperData;
    }

    public List<TagEntity> getTagData(Integer pageNum){
        int size = 10;
        List<TagEntity> tagEntityList = tagDao.findTagEntities(PageRequest.of(pageNum, size));
        return tagEntityList;
    }

    public Map<Integer, GroupTagData> getGroupData(Integer pageNum){
        float level = (float)0.5;
        Map<Integer, Float> groupTagData = redisService.getGroupTagData(pageNum);
        Map<Integer, GroupTagData> result = new HashMap<>();
        //List<Integer> relationTagList = new ArrayList<>();
        for(Map.Entry<Integer, Float> entry: groupTagData.entrySet()){
            if(entry.getValue()>=0.5){
                result.put(entry.getKey(), new GroupTagData(entry.getValue()));
            }
        }
        getRelativePaperNum(paperDao.findIDByGroup(pageNum), result);
        return result;
    }

    private void getRelativePaperNum(List<Integer> paperIDList, Map<Integer, GroupTagData> tagData){
        float level = (float)0.5;
        for(Map.Entry<Integer, GroupTagData> entry: tagData.entrySet()){
            int count = 0;
            for(Integer paperID: paperIDList){
                float relationValue = redisService.getPaperTagData(paperID, entry.getKey());
                if(relationValue>=level){
                    ++count;
                }
            }
            entry.getValue().setPaperNum(count);
        }
    }
}
