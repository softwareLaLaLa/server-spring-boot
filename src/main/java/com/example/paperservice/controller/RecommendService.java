package com.example.paperservice.controller;

import com.example.paperservice.Entity.*;
import com.example.paperservice.database.*;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.paperservice.util.Calculator.getMatrixData;

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
        List<EvalEntity> list = new ArrayList<>();
        for(EvalEntity evalEntity: evalList){
            if(addBrowseNumForPaper(evalEntity.getPaperid())){
               list.add(evalEntity);
            }
        }
        evaluationDao.saveAll(list);
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
                    tagEntity = new TagEntity(name, "", 0, new Date());
                    tagDao.save(tagEntity);
                }else{
                    tagEntity = tagDao.findByName(name);
//                    tagEntity.setNum(tagEntity.getNum()+1);
//                    tagEntity.setDate(new Date());
//                    tagDao.save(tagEntity);
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
                System.out.println("更新论文tag数据");
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
    public boolean addBrowseNumForPaper(int paper_id){
        //更新论文数据
        if(!paperDao.existsById(paper_id)){
            System.out.println("不存在该论文！");
            return false;
        }
        PaperEntity paperEntity = paperDao.findById(paper_id);
        paperEntity.setBrowseNum(paperEntity.getBrowseNum()+1);
        paperDao.save(paperEntity);

        //更新热榜数据
        HotPaperEntity hotPaperEntity = null;
        if(!hotPaperDao.existsById(paper_id)){
            System.out.println("该论文不在热榜中！");
            hotPaperEntity = new HotPaperEntity(0, null);
        }else{
            hotPaperEntity = hotPaperDao.findById(paper_id);
        }
        hotPaperEntity.setHot(hotPaperEntity.getHot()+1);
        hotPaperEntity.setLastActiveTime(new Date());
        hotPaperDao.save(hotPaperEntity);
        return true;
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
        List<HotPaperEntity> hotPaperEntityList = hotPaperDao.findTop20ByOrderByHotDesc();
        List<PaperSimpleData> result = new ArrayList<>();
        for(HotPaperEntity entity: hotPaperEntityList){
            int paper_id = entity.getId();
            result.add(getPaperSimpleData(paper_id));
        }
        return result;
    }

    private PaperSimpleData getPaperSimpleData(int paper_id){
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

    //更新tag数据
    public void freshTagData(){
        System.out.println("更新Tag信息");
        final int past = 30;
        final int usedNum = 10;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date date = calendar.getTime();
        List<TagEntity> tagList = tagDao.findByDateBeforeAndNumLessThan(date, usedNum);
        Set<Integer> tagIDList = new HashSet<>();
        for(TagEntity tagEntity :tagList){
            tagIDList.add(tagEntity.getId());
        }
        redisService.deleteTagData(tagIDList);
        tagDao.deleteByDateBeforeAndNumLessThan(date, usedNum);

    }

    public PaperData getPaperData(int paper_id){
        PaperEntity paperEntity = paperDao.findById(paper_id);
        Gson gson = new Gson();
        System.out.println("paperEntity:"+gson.toJson(paperEntity));
        List<List<TagSimpleData>> tagData = getRecommendTag(paper_id);
        return new PaperData(paperEntity, tagData);
    }

    public void clusterPaper(){
        List<PaperEntity> paperList = paperDao.findAll();
        List<Integer> paperIDList = new ArrayList<>();
        Set<Integer> tagList = new HashSet<>();
        List<Map<Integer, TagRela>> papersTagData = new ArrayList<>();
        for(PaperEntity paperEntity: paperList){
            int id = paperEntity.getId();
            paperIDList.add(id);
            Map<Integer, TagRela> tagRelaMap = redisService.getPaperTagData(id);
            papersTagData.add(tagRelaMap);
            tagList.addAll(tagRelaMap.keySet());
        }
        List<List<Float>> matrix = getMatrixData(papersTagData, tagList);
        //聚类

        List<List<Integer>> groupPaperIDList = new ArrayList<>();
        List<List<Float>> groupTagRelationList = new ArrayList<>();
        Map<Integer, Map<Integer, Float>> groupsData = new HashMap<>();
        Map<Integer, List<Integer>> tagGroupData = new HashMap<>();

        int groupID = 0;
        final int border = 1000;
        //更新paperID
        for(List<Integer> paperNumberList :groupPaperIDList){
            for(Integer paperNumber: paperNumberList) {
                int paperID = paperIDList.get(paperNumber);
                paperID = paperID%border + groupID*border;
            }
            ++groupID;
        }

        //更新groupTag信息, 更新Tag信息,
        //决定是否保留groupTag信息
        final float lowest = (float)0.1;
        //决定是否保留TagGroup信息
        final float lowest2 = (float)1;
        groupID = 0;
        for(List<Float> tagRelation :groupTagRelationList){
            int count = 0;
            Map<Integer, Float> groupTagData = new HashMap<>();

            for(Integer tagID :tagList){
                Float value = tagRelation.get(count++);
                if(value < lowest){
                    continue;
                }else if (value < lowest2){
                    if(tagGroupData.containsKey(tagID)){
                        tagGroupData.get(tagID).add(groupID);
                    }else{
                        List<Integer> groupIDList = new ArrayList<>();
                        groupIDList.add(groupID);
                        tagGroupData.put(tagID, groupIDList);
                    }
                }
                groupTagData.put(tagID, value);
            }
            groupsData.put(groupID++, groupTagData);
        }
        for(Map.Entry<Integer, List<Integer>> tagGroupDataEntry:tagGroupData.entrySet()){
            TagEntity tagEntity = tagDao.findById((int)tagGroupDataEntry.getKey());
            Gson gson = new Gson();
            tagEntity.setGroupIDList(gson.toJson(tagGroupDataEntry.getValue()));
            tagDao.save(tagEntity);
        }
        redisService.refreshGroupTagData(groupsData);
    }
}
