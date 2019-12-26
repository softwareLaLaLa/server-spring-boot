package com.example.paperservice.controller;

import com.example.paperservice.Entity.*;
import com.example.paperservice.PaperServiceApplication;
import com.example.paperservice.util.MyPasswordEncoder;
import com.google.gson.Gson;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class ApplicationService {
    @Autowired
    UserService userService;
    @Autowired
    RecommendService recommendService;

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
    @ResponseBody
    public UserInfor userInfor(String name){
        System.out.println("usr name = " + name);
        return userService.getUserInforByUserName(name);
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

    @GetMapping("/recommend-paper")
    public List<PaperSimpleData> getRecommendpaper(@RequestBody Map<Integer, Integer> groupMap){
        return recommendService.getRecommendPaper(groupMap);
    }

    @GetMapping("/hot-paper")
    public List<PaperSimpleData> getHotPaper(){
        return recommendService.getHotPaper();
    }

    @GetMapping("/paperData")
    public String getPaper(@RequestBody ID paper_id){
        Gson gson = new Gson();
        PaperData paperData = recommendService.getPaperData(paper_id.getId());
        return gson.toJson(paperData);
    }

    @PostMapping("/user-tag")
    public void initUserTag(@RequestBody ID usrId, @RequestBody Map<Integer, Float> tagData){
        userService.initUserTag(usrId.getId(), tagData);
    }

    @PostMapping("/user-data")
    public void updateUserTag(@RequestBody ID user_id){
        userService.refreshUserData(user_id.getId());
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
