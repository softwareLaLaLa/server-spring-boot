package com.example.paperservice.database;

import com.example.paperservice.Entity.*;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
public class RedisService {
    private static String PAPER_TAG_TABLE = "paperTag_";
    //static String PAPER_CLC_TABLE = "paperClick_";
    static String GROUP_TAG_TABLE = "groupTag_";
    static String USER_TAG_TABLE = "userTag_";
    //static String USER_GROUP_TABLE = "userGroup_";
    static String BRO_TABLE = "broHis_";
    static String TAG_TABLE = "tag_";

    public static RedisService redisService;

    @PostConstruct
    public void init(){
        redisService = this;
        redisService.redisTemplate = this.redisTemplate;
    }

    @Autowired
    RedisTemplate redisTemplate;

    //浏览记录操作
    public List<PaperSimpleData> getBrowseHistory(int usr_id){
        return redisTemplate.opsForList().range(BRO_TABLE+usr_id, 0, -1);
    }
    public void addBrowseHistory(int usr_id, List<PaperSimpleData> browseHistory){
        redisTemplate.opsForList().leftPushAll(BRO_TABLE+usr_id, browseHistory);
        redisTemplate.opsForList().trim(BRO_TABLE+usr_id, 0, 20);
        Gson gson = new Gson();
        System.out.println("更新浏览记录："+ gson.toJson(getBrowseHistory(usr_id)));
    }

    //该操作在mysql中实现
//    //tag表操作
//    //获取tag对应的论文群id
//    public List<Integer> getPaperGroupIDList(int tag_id){
//        return redisTemplate.opsForList().range(TAG_TABLE+tag_id, 0, -1);
//    }
//    //更新tag对应的论文群id
//    public void updatePaperGroup(int tag_id, Set<Integer> groupIDList){
//        String key = TAG_TABLE+tag_id;
//        redisTemplate.opsForList().trim(key, 0, 0);
//        System.out.println("删除tag对应papergroup数据："+redisTemplate.opsForList().size(key));
//        redisTemplate.opsForList().leftPushAll(key, groupIDList);
//    }

    //论文tag表操作
    //获取论文所有tag信息
    public Map<Integer, TagRela> getPaperTagData(int paper_id){
        Set<Integer> keys = redisTemplate.opsForHash().keys(PAPER_TAG_TABLE+paper_id);
        Map<Integer, TagRela> result = getPaperTagData(paper_id, keys);
        System.out.println("paper:"+paper_id+" TagData: "+result);
        return result;
    }
    //获取一串tag信息
    Map<Integer, TagRela> getPaperTagData(int paper_id, Set<Integer> tagIdList){
        List<TagRela> values = redisTemplate.opsForHash().multiGet(PAPER_TAG_TABLE+paper_id, tagIdList);
        System.out.println("获取论文对应tag信息");
        System.out.println("keys  : "+tagIdList);
        System.out.println("values: "+values);
        Map<Integer, TagRela> map = new HashMap<>();
        int count = 0;
        for(Integer i: tagIdList){
            map.put(i, values.get(count++));
        }
        return map;
    }
    public void addPaperTagData(int paper_id, Map<Integer, TagRela> map){
        redisTemplate.opsForHash().putAll(PAPER_TAG_TABLE+paper_id, map);
    }
    //为论文添加一个tag
    public void addPaperTagData(int paper_id, int tag_id, TagRela tr){
        redisTemplate.opsForHash().put(PAPER_TAG_TABLE+paper_id, tag_id, tr);
    }
    public void deletePaperTagData(String paper_id, Set<Integer> TagIdList){
        for(Integer i: TagIdList){
            deletePaperTagData(paper_id, i);
        }
    }
    //为论文删除一个tag
    public void deletePaperTagData(String paper_id, int tag_id){
        redisTemplate.opsForHash().delete(paper_id, tag_id);
    }
    public void updatePaperTagData(int paper_id, Set<Integer> tagID){
        for(Integer i: tagID){
            updatePaperTagData(paper_id, i);
        }
    }
    //为论文更新一个tag,为该tag的数量+1
    public void updatePaperTagData(int paper_id, int tag_id){
        TagRela data = null;
        if(!redisTemplate.opsForHash().hasKey(PAPER_TAG_TABLE+paper_id, tag_id)){
            data = new TagRela(0, 0);
        }else{
            data = (TagRela)redisTemplate.opsForHash().get(PAPER_TAG_TABLE+paper_id, tag_id);
        }
        data.addNum();
        redisTemplate.opsForHash().put(PAPER_TAG_TABLE+paper_id, tag_id, data);
    }

    //论文点击量表操作
    //获取点击量数据
//    public List<BrowseData> getPaperClickData(int paper_id){
//        return redisTemplate.opsForList().range(PAPER_CLC_TABLE+paper_id, 0, -1);
//    }
//    //更新点击量数据
//    public void updatePaperClickData(int paper_id, List<BrowseData> cdl){
//        redisTemplate.opsForList().leftPushAll(PAPER_CLC_TABLE+paper_id, cdl);
//        redisTemplate.opsForList().trim(PAPER_CLC_TABLE+paper_id, 0, 20);
//    }

    //论文群Tag表操作
    public Float getGroupTagData(int group_id, int tag_Id){
        Float value = null;
        if(!redisTemplate.opsForHash().hasKey(GROUP_TAG_TABLE+group_id, tag_Id)){
            return (float) 0;
        }else{
            value = (Float)redisTemplate.opsForHash().get(GROUP_TAG_TABLE+group_id, tag_Id);
        }
        return value;
    }

    public Map<Integer, Float> getGroupTagData(List<Integer> groupIDList, int tag_id){
        Map<Integer, Float> result = new HashMap<>();
        for(Integer i: groupIDList){
            result.put(i, getGroupTagData(i, tag_id));
        }
        return result;
    }

    public List<Float> getGroupTagData(int group_id, List<Integer> tagIDList){
        return redisTemplate.opsForHash().multiGet(group_id, tagIDList);
    }
    public void refreshGroupTagData(Map<Integer, Map<Integer, Float>> map){
        System.out.println("更新group的tag数据"+map);
        for(Object id: redisTemplate.keys(GROUP_TAG_TABLE+"*")) {
            String group_id = (String) id;
            Set<Integer> idList = redisTemplate.opsForHash().keys(group_id);
            deleteGroupTagData(group_id, idList);
        }
        for(Integer i: map.keySet()){
            redisTemplate.opsForHash().putAll(GROUP_TAG_TABLE+i, map.get(i));
        }
    }
    public void deleteGroupTagData(String group_id, Set<Integer> TagIdList){
        for(Integer i: TagIdList){
            deleteGroupTagData(group_id, i);
        }
    }
    //为论文group删除一个tag
    public void deleteGroupTagData(String group_id, int tag_id){
        redisTemplate.opsForHash().delete(group_id, tag_id);
    }

    //用户tag表操作
    //周期性更新用户Tag信息
    public void refreshUserTagData(int usr_id, Map<Integer, Float> map){
        Set<Integer> idList = redisTemplate.opsForHash().keys(USER_TAG_TABLE + usr_id);
        deleteUserTagData(USER_TAG_TABLE+usr_id, idList);
        addUserTagData(usr_id, map);
    }
    public void deleteUserTagData(String usr_id, Set<Integer> TagIdList){
        for(Integer i: TagIdList){
            deleteUserTagData(usr_id, i);
        }
    }
    //为用户删除一个tag
    public void deleteUserTagData(String usr_id, int tag_id){
        redisTemplate.opsForHash().delete(usr_id, tag_id);
    }
    public void addUserTagData(int usr_id, Map<Integer, Float> map){
        redisTemplate.opsForHash().putAll(USER_TAG_TABLE+usr_id, map);
    }
//    //为论文添加一个tag
//    public void addUserTagData(int usr_id, int tag_id, Float value){
//        redisTemplate.opsForHash().put(USER_TAG_TABLE+usr_id, tag_id, value);
//        System.out.println("为用户添加Tag: "+ ((TagRela)redisTemplate.opsForHash().get(USER_TAG_TABLE+usr_id, tag_id)));
//    }
    //获取全部tag信息
    public Map<Integer, Float> getUserTagData(int usr_id){
        Set<Integer> keys = redisTemplate.opsForHash().keys(USER_TAG_TABLE+usr_id);
        return getUserTagData(usr_id, keys);
    }
    //获取一串tag信息
    public Map<Integer, Float> getUserTagData(int usr_id, Set<Integer> tagIdList){
        List<Float> values = redisTemplate.opsForHash().multiGet(USER_TAG_TABLE+usr_id, tagIdList);
        System.out.println("获取论文对应tag信息");
        System.out.println("keys  : "+tagIdList);
        System.out.println("values: "+values);
        Map<Integer, Float> map = new HashMap<>();
        int count = 0;
        for(Integer i: tagIdList){
            map.put(i, values.get(count++));
        }
        return map;
    }

//    //用户评价论文后即时更新
//    //输入tag_id为key,w(Date, eval)为value的map
//    public void updateUserTagData(int usr_id, Map<Integer, Float> map){
//        for(Map.Entry<Integer, Float> entry:map.entrySet()){
//            updateUserTagData(usr_id, entry.getKey(), entry.getValue());
//        }
//    }
//    public void updateUserTagData(int usr_id, int tag_id, float value){
//        TagRela tagRela = null;
//        if(!redisTemplate.opsForHash().hasKey(USER_TAG_TABLE+usr_id, tag_id)){
//            System.out.println("用户没有该Tag："+tag_id);
//            addUserTagData(usr_id, tag_id, value);
//            return;
//        }else{
//            tagRela = (TagRela)redisTemplate.opsForHash().get(USER_TAG_TABLE+usr_id, tag_id);
//        }
//        value = (tagRela.getTag_num()*tagRela.getCorrelation()+value)/(tagRela.getTag_num()+1);
//        deleteUserTagData(usr_id, tag_id);
//        addUserTagData(usr_id, tag_id, value);
//    }

    public void deleteTagData(Set<Integer> tagIDList){
        Set<String> userSet = redisTemplate.keys(USER_TAG_TABLE);
        Set<String> paperSet = redisTemplate.keys(PAPER_TAG_TABLE);
        Set<String> groupSet = redisTemplate.keys(GROUP_TAG_TABLE);
        System.out.println("userSet:"+ userSet);
        System.out.println("paperSet:"+ paperSet);
        System.out.println("groupSet:"+ groupSet);

        for(String userKey: userSet){
            deleteUserTagData(userKey, tagIDList);
        }

        for(String paperKey :paperSet){
            deletePaperTagData(paperKey, tagIDList);
        }

        for(String groupKey :groupSet){
            deleteGroupTagData(groupKey, tagIDList);
        }
    }
}
