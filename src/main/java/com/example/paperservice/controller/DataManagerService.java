package com.example.paperservice.controller;

import com.example.paperservice.DataProcess.*;
import com.example.paperservice.Entity.PaperEntity;
import com.example.paperservice.Entity.TagEntity;
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
    public void addNewPapers(List<AddedPaperData> newPaperData) throws IOException {
        System.out.println("加入新论文");
        List<Map<Integer, Float>> paperRelation = new ArrayList<>();
        for(AddedPaperData addedPaperData: newPaperData){
            paperRelation.add(addedPaperData.getRelation());
        }
        System.out.println("paperRelationData:"+paperRelation);
        //生成paper relation矩阵
        List<List<Float>> paperRelationData = getRelationData(paperRelation);
        System.out.println("paperData:");
        for(List<Float> onePaperData: paperRelationData){
            System.out.println(onePaperData);
        }

        //生成group relation矩阵
        System.out.println("groupData:");
        List<List<Float>> groupRelationData = getRelationData(new ArrayList<>(redisService.getAllGroupTagData().values()));
        for(List<Float> oneGroupData: groupRelationData){
            System.out.println(oneGroupData);
        }

        List<Integer> groupIDList = redisService.getAllGroupID();
        System.out.println("groupID:"+groupIDList);
        List<Integer> groupNum = Calculator.distance(paperRelationData, groupRelationData);
        System.out.println("groupNum:"+groupNum);
        int count = 0;
        for(AddedPaperData addedPaperData: newPaperData){
            addedPaperData.getPaperEntity().setGroupID(groupIDList.get(groupNum.get(count++)));
            paperDao.save(addedPaperData.getPaperEntity());
            Map<Integer, TagRela> paperTagData = new HashMap<>();
            for(Map.Entry<Integer, Float> entry:addedPaperData.getRelation().entrySet()){
                paperTagData.put(entry.getKey(), new TagRela(1, entry.getValue()));
            }
            redisService.addPaperTagData(addedPaperData.getPaperEntity().getId(), paperTagData);
        }
    }

    private List<List<Float>> getRelationData(List<Map<Integer, Float>> data){
        List<Integer> tagIDList = tagDao.findAllId();
        System.out.println("tagID:"+tagIDList);
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
        System.out.println("加入新tag"+tags);
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
        System.out.println("删除论文"+paperIDList);
        for(Integer paperID: paperIDList){
            paperDao.deleteById(paperID);
            redisService.deletePaperTagData(paperID);
        }
    }

    public void deleteTag(Set<Integer> tagIDList){
        System.out.println("删除标签："+tagIDList);
        redisService.deleteTagData(tagIDList);
        for(Integer tagID: tagIDList){
            tagDao.deleteById(tagID);
        }
    }

    public TagEntity mergeTag(List<Integer> tagID){
        System.out.println("合并标签："+tagID);
        TagEntity goalTag = tagDao.findById((int)tagID.get(0));
        TagEntity opeTag = tagDao.findById((int)tagID.get(1));
        goalTag.setNum(goalTag.getNum()+opeTag.getNum());
        if(!goalTag.getDate().after(opeTag.getDate())){
            goalTag.setDate(opeTag.getDate());
        }
        tagDao.delete(opeTag);
        tagDao.save(goalTag);
        redisService.mergeAllTagData(tagID);
        return goalTag;
    }

    public List<PaperSimpleData> getPaperData(Integer pageNum){
        System.out.println("获取论文数据："+pageNum);
        int size = 10;
        List<PaperSimpleData> paperData = new ArrayList<>();
        List<PaperEntity> paperList = paperDao.findAll(PageRequest.of(pageNum, size)).getContent();
        System.out.println();
        for(PaperEntity paperEntity: paperList) {
            paperData.add(baseService.getPaperSimpleData(paperEntity));
        }
        return paperData;
    }

    public List<TagEntity> getTagData(Integer pageNum){
        System.out.println("获取标签数据："+pageNum);
        int size = 10;
        List<TagEntity> tagEntityList = tagDao.findAll(PageRequest.of(pageNum, size)).getContent();
        return tagEntityList;
    }

    public Map<Integer, GroupTagData> getGroupData(ID pageNum){
        System.out.println("获取聚类数据："+pageNum);
        float level = (float)0.5;
        Map<Integer, Float> groupTagData = redisService.getGroupTagData(pageNum);
        Map<Integer, GroupTagData> result = new HashMap<>();
        //List<Integer> relationTagList = new ArrayList<>();
        for(Map.Entry<Integer, Float> entry: groupTagData.entrySet()){
            String tagName = tagDao.findById((int)entry.getKey()).getName();
            if(entry.getValue()>=level){
                result.put(entry.getKey(), new GroupTagData(entry.getValue(), tagName));
            }
        }
        getRelativePaperNum(paperDao.findIDByGroup(pageNum.getId()), result);
        return result;
    }

    private void getRelativePaperNum(List<Integer> paperIDList, Map<Integer, GroupTagData> tagData){
        float level = (float)0.5;
        System.out.println("paperIDList:"+paperIDList);

        for(Map.Entry<Integer, GroupTagData> entry: tagData.entrySet()){
            int count = 0;
            System.out.println("tagID:"+entry.getKey());
            for(Integer paperID: paperIDList){
                System.out.println("paperID:"+paperID);
                float relationValue = redisService.getPaperTagData(paperID, entry.getKey());
                System.out.println("relation:"+relationValue);
                if(relationValue>=level){
                    ++count;
                }
            }
            entry.getValue().setPaperNum(count);
        }
    }
}
