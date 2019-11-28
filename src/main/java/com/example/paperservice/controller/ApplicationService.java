package com.example.paperservice.controller;

import com.example.paperservice.Entity.*;
import com.example.paperservice.PaperServiceApplication;
import com.example.paperservice.util.MyPasswordEncoder;
import com.netflix.discovery.converters.Auto;
import org.graalvm.compiler.lir.LIRInstruction;
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

    @GetMapping("/user-infor")
    @ResponseBody
    public UserInfor userInfor(Authentication authentication){
        String name = authentication.getName();
        System.out.println("usr name = " + name);
        return userService.getUserInforByUserName(name);
    }

    @PostMapping("/browse-history")
    public void addBrowseHistory(@RequestBody BrowseHistory browseHistory){
        userService.updateBrowseHistory(browseHistory);
    }

    @PostMapping("/evaluation-data")
    public void addEvalData(@RequestBody List<EvalEntity> evalEntityList){
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
}
