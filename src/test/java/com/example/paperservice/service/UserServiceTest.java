package com.example.paperservice.service;

import com.example.paperservice.DataProcess.BrowseHistory;
import com.example.paperservice.DataProcess.TagRela;
import com.example.paperservice.Entity.EvalEntity;
import com.example.paperservice.database.EvaluationDao;
import com.example.paperservice.database.RedisService;
import com.example.paperservice.database.TagDao;
import com.example.paperservice.database.UserDao;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends BasicServiceTest{

    @Mock
    private UserDao userDao;
    @Mock
    private RedisService redisService;
    @Mock
    private TagDao tagDao;
    @Mock
    private EvaluationDao evaluationDao;
    @Mock
    private RecommendService recommendService;
    @InjectMocks
    UserService userService;

    @Test
    void getUserInforByUserName() {
        userService.getUserInforByUserName("user1");
    }

    @Test
    void updateBrowseHistory() {
        BrowseHistory browseHistory = new BrowseHistory(1, new ArrayList<>());
        userService.updateBrowseHistory(browseHistory);
    }

    @Test
    void getAuthData() {
        userService.getAuthData("user1");
    }

    @Test
    void addUser() {
        userService.addUser("name", "password", "role");
    }

    @Test
    void refreshUserData() {
        List<EvalEntity> evalList = new ArrayList<>();
        EvalEntity evalEntity = new EvalEntity(1, 1, 1, new Date());
        evalList.add(evalEntity);

        doReturn(evalList).when(evaluationDao).findByUsrIdOrderByDateDesc(1);
        Map<Integer, TagRela> paperTagMap = new HashMap<>();
        doReturn(paperTagMap).when(redisService).getPaperTagData(any());
    }

    @Test
    void initUserTag() {
        Map<Integer, Float> tagData = new HashMap<>();
        tagData.put(1, new Float(1));
        userService.initUserTag(1, tagData);

        ArgumentCaptor<Integer> usrIdCaptor = ArgumentCaptor.forClass(int.class);
        ArgumentCaptor<Map<Integer, Float>> tagDataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(redisService).addUserTagData(usrIdCaptor.capture(), tagDataCaptor.capture());

        Assert.assertEquals(1, (int)usrIdCaptor.getValue());
        Assert.assertEquals(1, tagDataCaptor.getValue().size());
    }
}