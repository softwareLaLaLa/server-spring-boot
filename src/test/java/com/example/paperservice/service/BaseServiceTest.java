package com.example.paperservice.service;

import com.example.paperservice.DataProcess.PaperSimpleData;
import com.example.paperservice.DataProcess.TagRela;
import com.example.paperservice.Entity.PaperEntity;
import com.example.paperservice.Entity.TagEntity;
import com.example.paperservice.database.RedisService;
import com.example.paperservice.database.TagDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class BaseServiceTest{
    String title = "title";
    String abst = "abst";
    String resUrl = "resUrl";
    int browseNum = 0;
    int evalNum = 0;
    int uncheckNum = 0;
    int groupId = 0;

    PaperEntity paperEntity = new PaperEntity(title, abst, resUrl, browseNum, evalNum, uncheckNum, groupId);

    @Mock
    private TagDao tagDao;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private BaseService baseService;
//
//    @Before
//    public void setUp(){
//        MockitoAnnotations.initMocks(this);
//    }

    @Test
    public void getPaperSimpleData() {
        Map<Integer, TagRela> tagRelaMap = new HashMap<>();
        tagRelaMap.put(0, new TagRela(1, 1));
        doReturn(tagRelaMap).when(redisService).getPaperTagData(any(int.class));

        TagEntity tagEntity = new TagEntity("tag", "", 1, new Date());
        doReturn(tagEntity).when(tagDao).findById(0);
        PaperSimpleData paperSimpleData = baseService.getPaperSimpleData(paperEntity);

        List<String> tagList = new ArrayList<>();
        tagList.add(tagEntity.getName());
        Assert.assertEquals(tagList, paperSimpleData.getTagList());
        Assert.assertEquals(paperEntity.getId(), paperSimpleData.getPaper_id());
        Assert.assertEquals(paperEntity.getTitle(), paperSimpleData.getTitle());
        Assert.assertEquals(paperEntity.getBrowseNum(),paperSimpleData.getTotalBrowseNum());
    }
}