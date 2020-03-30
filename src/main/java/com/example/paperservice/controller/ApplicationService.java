package com.example.paperservice.controller;

import com.example.paperservice.DataProcess.*;
import com.example.paperservice.Entity.*;
import com.example.paperservice.util.MyPasswordEncoder;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class ApplicationService {
    @Autowired
    UserService userService;
    @Autowired
    RecommendService recommendService;
    @Autowired
    DataManagerService dataManagerService;

    @PostMapping("/signin")
    public boolean signIn(@RequestBody Map params) throws Exception {
        String name = (String) params.get("name");
        String password = (String) params.get("password");
        UserEntity systemUser = userService.getAuthData(name);
        if(systemUser != null){
            return false;
        }
        password = MyPasswordEncoder.getEncoder().encode(password);
        userService.addUser(name, password, "user");
        return true;
    }

//    @PostMapping("/login")
//    public boolean logIn(@RequestBody Map params) throws Exception {
//        String name = (String) params.get("name");
//        String password = (String) params.get("password");
//        UserEntity systemUser = userService.getAuthData(name);
//        if(systemUser == null){
//            return false;
//        }
//        password = MyPasswordEncoder.getEncoder().encode(password);
//        if(systemUser.getPassword().equals(password)){
//            return true;
//        }
//        return false;
//    }

    @GetMapping("/user-infor")
    public String userInfor(@RequestParam String name){
        System.out.println("usr name = " + name);
        Gson gson = new Gson();
        UserInfor userInfor = userService.getUserInforByUserName(name);
        System.out.println("user infor: "+ gson.toJson(userInfor));
        return gson.toJson(userInfor);
    }

    @PostMapping("/browse-history")
    public void addBrowseHistory(@RequestBody BrowseHistory browseHistory){
        userService.updateBrowseHistory(browseHistory);
    }

    @PostMapping("/evaluation-data")
    public void addEvalData(@RequestBody List<EvalEntity> evalEntityList){
        Gson gson = new Gson();
        System.out.println("加入评估数据" + gson.toJson(evalEntityList));
        recommendService.addEvalData(evalEntityList);
    }

    @PostMapping("/paper-tag")
    public void addTagForPaper(@RequestBody Map<Integer, Set<Integer>> paperTagMap){
        recommendService.addTagsData(paperTagMap);
    }

    @PostMapping("/paper-new-tag")
    public void addNewTagForPaper(@RequestBody Map<Integer, Set<String>> paperNewTagMap){
        recommendService.addNewTagsData(paperNewTagMap);
    }

    @PostMapping("/recommend-paper")
    public Map<Integer,List<PaperSimpleData>> getRecommendpaper(@RequestBody Map<Integer, Integer> groupMap){
        System.out.println("获取group："+groupMap + "对应论文");
        return recommendService.getRecommendPaper(groupMap);
    }

    @GetMapping("/hot-paper")
    public List<PaperSimpleData> getHotPaper(){
        return recommendService.getHotPaper();
    }

    @GetMapping("/paperData")
    public String getPaper(@RequestParam int paper_id){
        Gson gson = new Gson();
        PaperData paperData = recommendService.getPaperData(paper_id);
        return gson.toJson(paperData);
    }

    @PostMapping("/user-tag")
    public Set<Integer> initUserTag(@RequestBody InitialUserTagData userTagData){
        return userService.initUserTag(userTagData.getUsr_id(), userTagData.getTagData());
    }

    @PostMapping("/user-data")
    public void updateUserTag(@RequestBody ID user_id){
        userService.refreshUserData(user_id.getId());
    }

    @GetMapping("/tag-data")
    public List<TagSimpleData> getTagData(){
        return recommendService.getTagData();
    }


    @PostMapping("/back/new-paper")
    public void addNewPaper(@RequestBody List<AddedPaperData> paperData) throws IOException {
        dataManagerService.addNewPapers(paperData);
    }

    @PostMapping("/back/new-tag")
    public void addNewTag(@RequestBody List<String> tags){
        dataManagerService.addNewTag(tags);
    }

    @DeleteMapping("/back/paper")
    public void deletePaper(@RequestBody List<Integer> paperIDList){
        dataManagerService.deletePaper(paperIDList);
    }

    @DeleteMapping("/back/tag")
    public void deleteTag(@RequestBody Set<Integer> tagIDList){
        dataManagerService.deleteTag(tagIDList);
    }

    @PostMapping("/back/tag")
    public TagEntity mergeTag(@RequestBody List<Integer> tagID){
        return dataManagerService.mergeTag(tagID);
    }

    @GetMapping("/back/paper")
    public List<PaperSimpleData> getPaperData(@RequestParam int pageNum){
        return dataManagerService.getPaperData(pageNum);
    }

    @GetMapping("/back/tag")
    public List<TagEntity> getTagData(@RequestParam int pageNum){
        return dataManagerService.getTagData(pageNum);
    }

    @GetMapping("/back/group")
    public Map<Integer, GroupTagData> getGroupData(@RequestParam int pageNum){
        ID groupNum = new ID();
        groupNum.setId(pageNum);
        return dataManagerService.getGroupData(groupNum);
    }

    @GetMapping("/back/cluster")
    public boolean clusterPaper(@RequestParam int clusterNum){
        return recommendService.clusterPaper(clusterNum);
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
