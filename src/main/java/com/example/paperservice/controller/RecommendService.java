package com.example.paperservice.controller;

import com.example.paperservice.Entity.*;
import com.example.paperservice.database.*;
import com.google.gson.Gson;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RecommendService {
    @Autowired
    private TagDao tagDao;
    @Autowired
    private PaperDao paperDao;
    @Autowired
    private EvaluationDao evaluationDao;
    @Autowired
    private HotPaperDao hotPaperDao;
    @Autowired
    private RedisService redisService;

    //添加评价，可以用队列实现
    public void addEvalData(List<EvalEntity> evalList){
        evaluationDao.saveAll(evalList);
    }

    //添加新tag数据
    public void addNewTagsData(Map<Integer, Set<String>> paperNewTagMap){
        Map<Integer, Set<Integer>> paperTagMap = new HashMap<>();
        //Set<String> newTag = new HashSet<>();
        for(Map.Entry<Integer, Set<String>> entry: paperNewTagMap.entrySet()){
            Set<Integer> newTagIDList = new HashSet<>();
            for(String name: entry.getValue()){
                TagEntity tagEntity = null;
                if(!tagDao.existsByName(name)){
                    tagEntity = new TagEntity(name, "", 1, new Date());
                    tagDao.save(tagEntity);
                }else{
                    tagEntity = tagDao.findByName(name);
                }
                newTagIDList.add(tagEntity.getId());
            }
            paperTagMap.put(entry.getKey(), newTagIDList);
            //newTag.addAll(entry.getValue());
        }
        addTagsData(paperTagMap);
    }

    //添加用户为该论文添加的tag信息,可以用队列实现
    public void addTagsData(Map<Integer, Set<Integer>> paperTagMap){
        int unCheckMaxNum = 7;

        for(Map.Entry<Integer, Set<Integer>> paperTagEntry: paperTagMap.entrySet()){
            int paper_id = paperTagEntry.getKey();
            redisService.updatePaperTagData(paper_id, paperTagEntry.getValue());
            updateTagData(paperTagEntry.getValue());
            PaperEntity paperEntity = paperDao.findById(paper_id);
            int unCheckNum = paperEntity.getUncheckNum() + 1;
            if(unCheckNum>unCheckMaxNum){
                unCheckNum = 0;
                updatePaperTagData(paperEntity);
            }
            paperEntity.setUncheckNum(unCheckNum);
            paperEntity.setEvalNum(paperEntity.getEvalNum()+1);
            paperDao.save(paperEntity);
        }
    }

    //更新全部论文tag信息
    private void updatePaperTagData(PaperEntity paperEntity){
        int paper_id = paperEntity.getId();
        System.out.println("更新论文："+paper_id+" tag相关值");
        //总评价次数
        int evalNum = paperEntity.getEvalNum();
        Map<Integer, TagRela> tagRelaMap = redisService.getPaperTagData(paper_id);
        for(Map.Entry<Integer, TagRela> tagRelaEntry: tagRelaMap.entrySet()){
            TagRela tagRela = tagRelaEntry.getValue();
            //根据特定函数计算rela值并存储
            float value = (float) (1/(1+Math.exp(5-10*tagRela.getTag_num()/evalNum)));
            System.out.println("对tag:"+ tagRelaEntry.getKey() + " 更新值为"+ value);
            tagRela.setCorrelation(value);
            redisService.addPaperTagData(paper_id, tagRelaEntry.getKey(), tagRela);
        }
    }

    //添加用户浏览信息，可以用队列实现
    public void addBrowseNumForPaper(int paper_id){
        HotPaperEntity hotPaperEntity = null;
        if(!hotPaperDao.existsById(paper_id)){
            System.out.println("该论文不在热榜中！");
            hotPaperEntity = new HotPaperEntity(0, null);
            return ;
        }else{
            hotPaperEntity = hotPaperDao.findById(paper_id);
        }
        hotPaperEntity.setHot(hotPaperEntity.getHot()+1);
        hotPaperEntity.setLastActiveTime(new Date());
        hotPaperDao.save(hotPaperEntity);
    }

    public void refreshHotPaperData(){
        //过期时间
        final int past = 7;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date date = calendar.getTime();
        hotPaperDao.deleteHotPaperEntityByLastActiveTimeBefore(date);
    }

    public List<PaperSimpleData> getHotPaper(){
        List<HotPaperEntity> hotPaperEntityList = hotPaperDao.findTop20OrderByHotDesc();
        List<PaperSimpleData> result = new ArrayList<>();
        for(HotPaperEntity entity: hotPaperEntityList){
            int paper_id = entity.getId();
            result.add(getPaper(paper_id));
        }
        return result;
    }

    private PaperSimpleData getPaper(int paper_id){
        PaperEntity paperEntity = paperDao.findById(paper_id);
        Map<Integer, TagRela> tagRelaMap = redisService.getPaperTagData(paper_id);
        List<String> tagList = new ArrayList<>();
        for(Integer i: tagRelaMap.keySet()){
            tagList.add(tagDao.findById((int)i).getName());
        }
        return new PaperSimpleData(paperEntity, tagList);
    }

    //获取推荐论文
    //参数key对应groupID, value对应要获取的数量
    public List<PaperSimpleData> getRecommendPaper(Map<Integer, Integer> groupMap){
        int groupPaperNum = 1000;
        int pageSize = 5;
        List<PaperSimpleData> result = new ArrayList<>();
        for(Map.Entry<Integer,Integer> entry: groupMap.entrySet()){
            int group_id = entry.getKey();
            List<PaperEntity> paperList = paperDao.findByIdBetweenOrderByBrowseNumDesc(group_id*groupPaperNum, (group_id+1)*groupPaperNum, PageRequest.of(entry.getValue(), pageSize));
            for(PaperEntity paperEntity: paperList){
                Map<Integer, TagRela> tagRelaMap = redisService.getPaperTagData(paperEntity.getId());
                List<String> tagList = new ArrayList<>();
                for(Integer i: tagRelaMap.keySet()){
                    tagList.add(tagDao.findById((int)i).getName());
                }
                result.add(new PaperSimpleData(paperEntity, tagList));
            }
        }
        return result;
    }

    //获取论文推荐tag
    public List<List<TagSimpleData>> getRecommendTag(int paper_id){
        float validity1 = (float)0.3; float validity2 = (float) 0.85;
        Gson gson = new Gson();
        Map<Integer, TagRela> tagRelaMap = redisService.getPaperTagData(paper_id);
        System.out.println("论文:"+paper_id+" 对应tag信息："+gson.toJson(tagRelaMap));
        List<List<TagSimpleData>> result = new ArrayList<>();
        List<TagSimpleData> recommendTag = new ArrayList<>();
        List<TagSimpleData> ownedTag = new ArrayList<>();
        for(Map.Entry<Integer, TagRela> tagRelaEntry: tagRelaMap.entrySet()){
            float value = tagRelaEntry.getValue().getCorrelation();
            int tag_id = tagRelaEntry.getKey();
            if(value > validity2){
                ownedTag.add(new TagSimpleData(tagDao.findById(tag_id)));
            }else if(value > validity1){
                recommendTag.add(new TagSimpleData(tagDao.findById(tag_id)));
            }
        }
        result.add(ownedTag); result.add(recommendTag);
        System.out.println("推荐tag信息:"+gson.toJson(result));
        return result;
    }

    //用户使用该tag后调用
    private void updateTagData(Set<Integer> tagIDList){
        for(Integer i: tagIDList){
            TagEntity tagEntity = tagDao.findById((int)i);
            tagEntity.setDate(new Date());
            tagEntity.setNum(tagEntity.getNum()+1);
            tagDao.save(tagEntity);
        }
    }

    public void freshTagData(){
        final int past = 30;
        final int usedNum = 10;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date date = calendar.getTime();
        tagDao.deleteByDateBeforeAndNumLessThan(date, usedNum);
    }
}
