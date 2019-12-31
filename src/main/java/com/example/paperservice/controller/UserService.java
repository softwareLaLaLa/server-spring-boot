package com.example.paperservice.controller;

import com.example.paperservice.Entity.*;
import com.example.paperservice.database.EvaluationDao;
import com.example.paperservice.database.RedisService;
import com.example.paperservice.database.TagDao;
import com.example.paperservice.database.UserDao;
import com.example.paperservice.util.Calculator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;

import static com.example.paperservice.util.Calculator.getMatrixData;
import static com.example.paperservice.util.Calculator.getMatrixData2;

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
    @Autowired
    private RecommendService recommendService;

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
        Gson gson = new Gson();
        System.out.println("update browse history: " + gson.toJson(browseHistory));
        redisService.addBrowseHistory(browseHistory.getUsr_id(), browseHistory.getBrowsePaperData());
    }

    public UserEntity getAuthData(String name){
        if(userDao.existsByName(name)){
            return userDao.findByName(name);
        }
        return null;
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
        if(evaluationDao.countByUsrIdAndDateAfter(usr_id, date) > dataNum){
            evaluationDao.deleteByUsrIdAndDateBefore(usr_id, date);
        }
    }
    //更新用户tag数据
    private void refreshUserTagData(int usr_id){
        List<EvalEntity> evalList = evaluationDao.findByUsrIdOrderByDateDesc(usr_id);
        System.out.println("用户："+usr_id +" 评价记录："+evalList);
        if(evalList != null){
            if(evalList.size() == 0){
                return ;
            }
            Date lastDate = evalList.get(0).getDate();
            List<Float> userValueList = new ArrayList<>();
            Set<Integer> relativeTag = new HashSet<>();
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
                int paper_id = evalEntity.getPaperid();
                Map<Integer, TagRela> paperTagMap = redisService.getPaperTagData(paper_id);
                relativeTag.addAll(paperTagMap.keySet());
                paperList.add(paperTagMap);
            }
            //Map<Integer, Float> userTagMap = redisService.getUserTagData(usr_id);
            List<List<Float>> paperTagRel = getMatrixData(paperList, relativeTag);
            List<Float> newValueList = new ArrayList<>();
            //执行python代码
            newValueList = Calculator.multiple(userValueList, paperTagRel);
            int count = 0;
            Map<Integer, Float> userTagMap = new HashMap<>();
            for(Integer tagID :relativeTag){
                //System.out.println("更新前tag数据："+entry);
                userTagMap.put(tagID, newValueList.get(count));
                System.out.println("更新后tag数据： id="+tagID + "relation = "+newValueList.get(count));
                ++count;
            }
            redisService.refreshUserTagData(usr_id, userTagMap);
        }
    }

    //计算用户group关联信息
    private void setUserGroupData(int usr_id){
        System.out.println("设置用户group信息");
        Gson gson = new Gson();
        Map<Integer, Float> usrTagDataMap = redisService.getUserTagData(usr_id);
        System.out.println("用户tag信息："+usrTagDataMap);
        List<Float> usrTagRel = new ArrayList<>();
        List<Map<Integer, Float>> tagList = new ArrayList<>();
        Set<Integer> relativeGroup = new HashSet<>();
        for(Map.Entry<Integer, Float> entry: usrTagDataMap.entrySet()){
            usrTagRel.add(entry.getValue());
            Type listType = new TypeToken<ArrayList<Integer>>(){}.getType();
            TagEntity tagEntity = tagDao.findById((int)entry.getKey());
            List<Integer> groupIDList = gson.fromJson(tagEntity.getGroupIDList(), listType);
            System.out.println("tag："+ entry.getKey()+" 对应group"+groupIDList);
            tagList.add(redisService.getGroupTagData(groupIDList, tagEntity.getId()));
            relativeGroup.addAll(groupIDList);
        }


        List<List<Float>> newValueList = getMatrixData2(tagList, relativeGroup);
        System.out.println("tag relation 矩阵："+usrTagRel);
        System.out.println("整合矩阵："+newValueList);
        //对每个group的感兴趣程度
        List<Float> newUserValueGroupValue = Calculator.multiple(usrTagRel, newValueList);
        Collections.sort(newUserValueGroupValue);
        //分为两个group存储在mysql中
        float border = Calculator.divide(newUserValueGroupValue);
        System.out.println("用户对group的喜好程度为："+newUserValueGroupValue+" 分割界限："+border);
        List<Integer> favorGroup = new ArrayList<>();
        List<Integer> candidateGroup = new ArrayList<>();
        int count = 0;
        for(Integer groupID: relativeGroup){
            float value = newUserValueGroupValue.get(count++);
            if(value < border){
                candidateGroup.add(groupID);
            }else{
                favorGroup.add(groupID);
            }
        }
        UserEntity userEntity = userDao.findById(usr_id);
        userEntity.setCandidateGroup(gson.toJson(candidateGroup));
        userEntity.setGroup(gson.toJson(favorGroup));
        userDao.save(userEntity);
    }

    //初始化用户tag信息
    public Set<Integer> initUserTag(int usrId, Map<Integer, Float> tagData){
        redisService.addUserTagData(usrId, tagData);
        Set<Integer> groupId = recommendService.getTagGroup(tagData.keySet());
        UserEntity userEntity = userDao.findById(usrId);
        Gson gson = new Gson();
        userEntity.setGroup(gson.toJson(groupId));
        userDao.save(userEntity);
        return groupId;
    }
}
