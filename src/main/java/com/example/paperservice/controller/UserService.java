package com.example.paperservice.controller;

import com.example.paperservice.Entity.*;
import com.example.paperservice.database.EvaluationDao;
import com.example.paperservice.database.RedisService;
import com.example.paperservice.database.TagDao;
import com.example.paperservice.database.UserDao;
import com.example.paperservice.util.MatrixCalcu;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;

@Component
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisService redisService;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private EvaluationDao evaluationDao;

    public UserInfor getUserInforByUserName(String name){
        UserEntity userEntity = userDao.findByName(name);
        if(userEntity == null){
            return null;
        }
        int id = userEntity.getId();
        List<PaperSimpleData> browseData = redisService.getBrowseHistory(id);
        return new UserInfor(userEntity, browseData);
    }

    public void updateBrowseHistory(BrowseHistory browseHistory){
        redisService.addBrowseHistory(browseHistory.getUsr_id(), browseHistory.getBrowsePaperData());
    }

    public UserEntity getAuthData(String name){
        return userDao.findByName(name);
    }

    public void addUser(String name, String password, String role){
        userDao.save(new UserEntity(name, password, role));
    }

    //每次退出时更新用户的兴趣（tag与对应group）信息
    public void refreshUserData(int usr_id){
        deletePastData(usr_id);
        refreshUserTagData(usr_id);
        setUserGroupData(usr_id);
    }
    //删除过期数据
    private void deletePastData(int usr_id){
        //最少数据量
        final int dataNum = 30;
        //过期时间
        final int past = 30;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date date = calendar.getTime();
        if(evaluationDao.countEvalNumByUsr_idAndDateAfter(usr_id, date) > dataNum){
            evaluationDao.deleteByUsr_idAndDateBefore(usr_id, date);
        }
    }
    //更新用户tag数据
    private void refreshUserTagData(int usr_id){
        List<EvalEntity> evalList = evaluationDao.findByUsr_idOrderByDateDesc(usr_id);
        System.out.println("用户："+usr_id +" 评价记录："+evalList);
        if(evalList != null){
            if(evalList.size() == 0){
                return ;
            }
            Date lastDate = evalList.get(0).getDate();
            List<Float> userValueList = new ArrayList<>();
            //Multiset relativeTag = HashMultiset.create();;
            List<Map<Integer, TagRela>> paperList = new ArrayList<>();
            float w1 = 7; float w2 = 14*14/2;
            //SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            for(EvalEntity evalEntity: evalList){
                int dateGap = (int) ((lastDate.getTime() - evalEntity.getDate().getTime())/1000*60*60*24);
                if(dateGap < 8){
                    Float userValue = w1*evalEntity.getEval()/(dateGap+1);
                    userValueList.add(userValue);
                }else{
                    Float userValue = w2*evalEntity.getEval()/(dateGap*dateGap);
                    userValueList.add(userValue);
                }
                int paper_id = evalEntity.getPaper_id();
                Map<Integer, TagRela> paperTagMap = redisService.getPaperTagData(paper_id);
                //relativeTag.addAll(paperTagMap.keySet());
                paperList.add(paperTagMap);
            }
            Map<Integer, Float> userTagMap = redisService.getUserTagData(usr_id);
            List<List<Float>> paperTagRel = getMatrixData(paperList, userTagMap.keySet());
            List<Float> newValueList = new ArrayList<>();
            //执行python代码
            newValueList = MatrixCalcu.multiple(userValueList, paperTagRel);
            int count = 0;
            for(Map.Entry<Integer, Float> entry: userTagMap.entrySet()){
                System.out.println("更新前tag数据："+entry);
                int tag_id = entry.getKey();
                Float relation = entry.getValue();
                relation = newValueList.get(count++);
                System.out.println("更新后tag数据："+entry);
            }
            redisService.refreshUserTagData(usr_id, userTagMap);
        }
    }
    //生成矩阵数据
    private List<List<Float>> getMatrixData(List<Map<Integer, TagRela>> mapList, Set<Integer> relativeSet){
        float level = (float) 1;
        System.out.println("关联tag："+relativeSet);
        List<List<Float>> result = new ArrayList<>();
        for(Map<Integer, TagRela> tagData: mapList){
            System.out.println("论文Tag信息："+tagData);
            List<Float> temp = new ArrayList<>();
            for(Integer i: relativeSet){
                TagRela tagRela = tagData.get(i);
                if(tagRela != null){
                    if(tagRela.getCorrelation()>level) {
                        temp.add(tagRela.getCorrelation());
                        continue;
                    }
                    else{
                        System.out.println("tag:"+i+"相关值过低！");
                        temp.add((float) 0);
                    }
                }
                System.out.println("error! paper tag:"+i+"相关数据为空");
                temp.add((float) 0);
            }
            result.add(temp);
        }
        System.out.println("转化List数据结果："+result);
        return result;
    }
    private List<List<Float>> getMatrixData2(List<Map<Integer, Float>> list, Set<Integer> relativeSet){
        float level = (float) 1;
        System.out.println("关联group："+relativeSet);
        List<List<Float>> result = new ArrayList<>();
        for(Map<Integer, Float> tagGroupData:list){
            System.out.println("tagGroup信息："+tagGroupData);
            List<Float> temp = new ArrayList<>();
            for(Integer i: relativeSet){
                Float value = tagGroupData.get(i);
                if(value != null){
                    if(value > level) {
                        temp.add(value);
                    }else{
                        System.out.println("tag:"+i+"相关值过低！");
                        temp.add((float)0);
                    }
                    continue;
                }
                System.out.println("error! group tag:"+i+"相关数据为空");
                temp.add((float) 0);
            }
        }
        System.out.println("转化List数据结果："+result);
        return result;
    }

    //计算用户group关联信息
    private void setUserGroupData(int usr_id){
        Gson gson = new Gson();
        Map<Integer, Float> usrTagDataMap = redisService.getUserTagData(usr_id);
        List<Float> usrTagRel = new ArrayList<>();
        List<Map<Integer, Float>> tagList = new ArrayList<>();
        Set<Integer> relativeGroup = new HashSet<>();
        for(Map.Entry<Integer, Float> entry: usrTagDataMap.entrySet()){
            usrTagRel.add(entry.getValue());
            Type listType = new TypeToken<ArrayList<Integer>>(){}.getType();
            TagEntity tagEntity = tagDao.findById((int)entry.getKey());
            List<Integer> groupIDList = gson.fromJson(tagEntity.getGroupIDList(), listType);
            tagList.add(redisService.getGroupTagData(groupIDList, tagEntity.getId()));
            relativeGroup.addAll(groupIDList);
        }
        List<List<Float>> newValueList = getMatrixData2(tagList, relativeGroup);
        //对每个group的感兴趣程度
        List<Float> newUserValueGroupValue = MatrixCalcu.multiple(usrTagRel, newValueList);
        //分为两个group存储在mysql中

    }
}
