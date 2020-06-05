package com.example.paperservice.service;

import com.example.paperservice.DataProcess.PaperSimpleData;
import com.example.paperservice.Entity.EvalEntity;
import com.example.paperservice.Entity.HotPaperEntity;
import com.example.paperservice.Entity.PaperEntity;
import com.example.paperservice.Entity.TagEntity;
import com.example.paperservice.database.*;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.awt.print.Paper;
import java.util.*;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class RecommendServiceTest extends BasicServiceTest{

    @Mock
    private TagDao tagDao;
    @Mock
    private PaperDao paperDao;
    @Mock
    private EvaluationDao evaluationDao;
    @Mock
    private HotPaperDao hotPaperDao;
    @Mock
    private RedisService redisService;
    @Mock
    private BaseService baseService;
    @Mock
    private RecommendService recommendService;

    @Test
    void addEvalData() {
        List<EvalEntity> evalEntities = new ArrayList<>();
        EvalEntity evalEntity = new EvalEntity(1, 1, 1, new Date());
        evalEntities.add(evalEntity);

        doReturn(true).when(recommendService).addBrowseNumForPaper(1);
        ArgumentCaptor<List<EvalEntity>> evalListCaptor = ArgumentCaptor.forClass(List.class);

        recommendService.addEvalData(evalEntities);
        verify(evaluationDao).saveAll(evalListCaptor.capture());

        Assert.assertEquals(1, evalListCaptor.getValue().size());
        Assert.assertEquals(evalEntity.getUsrid(), evalListCaptor.getValue().get(0).getUsrid());
    }

    @Test
    void addNewTagsData() {
        Map<Integer, Set<String>> tagData = new HashMap<>();
        Set<String> tagName = new HashSet<>();
        tagName.add("法律"); tagName.add("政治");
        tagData.put(1, tagName);

        doNothing().when(recommendService).addNewTagsData(any());

        recommendService.addNewTagsData(tagData);

        ArgumentCaptor<String> tagNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(tagDao).findByName(tagNameCaptor.capture());

        Assert.assertEquals("政治", tagNameCaptor.getValue());
    }

    @Test
    void addTagsData() {
        Map<Integer, Set<Integer>> tagData = new HashMap<>();
        Set<Integer> tagId = new HashSet<>();
        tagId.add(1); tagId.add(2);
        tagData.put(1, tagId);

        recommendService.addTagsData(tagData);
    }

    @Test
    void refreshHotPaperData() {
        doNothing().when(hotPaperDao).deleteHotPaperEntityByLastActiveTimeBefore(any(Date.class));
        recommendService.refreshHotPaperData();
    }

    @Test
    void getHotPaper() {
        List<HotPaperEntity> hotList = new ArrayList<>();
        hotList.add(new HotPaperEntity(1, 1, new Date()));
        doReturn(hotList).when(hotPaperDao).findTop20ByOrderByHotDesc();

        List<PaperSimpleData> result = recommendService.getHotPaper();
        Assert.assertEquals(1, result.size());
        //Assert.assertEquals("title", result.get(0).getTitle());
    }

    @Test
    void getRecommendPaper() {
        Map<Integer, Integer> groupMap = new HashMap<>();
        groupMap.put(1, 5);

        List<PaperEntity> paperList = new ArrayList<>();
        PaperEntity paperEntity = new PaperEntity("title", "abst", "resUrl", 0, 0, 0, 0);
        paperList.add(paperEntity);
        doReturn(paperList).when(paperDao).findByGroupId(1, any(PageRequest.class));
        doCallRealMethod().when(baseService).getPaperSimpleData(any(PaperEntity.class));

        Map<Integer, List<PaperSimpleData>> result = recommendService.getRecommendPaper(groupMap);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(1, result.get(1).size());
        Assert.assertEquals("title", result.get(1).get(0).getTitle());
    }

    @Test
    void getRecommendTag() {
        recommendService.getRecommendTag(1);
    }

    @Test
    void freshTagData() {
        List<TagEntity> tagIdList = new ArrayList<>();
        tagIdList.add(new TagEntity("tag", "", 1, new Date()));
        doReturn(tagIdList).when(tagDao).findByDateBeforeAndNumLessThan(any(Date.class), any(int.class));
    }

    @Test
    void getTagGroup() {
        Set<Integer> tagIdSet = new HashSet<>();
        tagIdSet.add(1); tagIdSet.add(2);

        List<Integer> groupIdList = new ArrayList<>();
        groupIdList.add(1); groupIdList.add(2);
        Gson gson = new Gson();
        TagEntity tagEntity = new TagEntity("tag", gson.toJson(groupIdList), 1, new Date());
        doReturn(tagEntity).when(tagDao).findById(1);  doReturn(tagEntity).when(tagDao).findById(2);
        Set<Integer> result = recommendService.getTagGroup(tagIdSet);

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.containsAll(groupIdList));
    }

    @Test
    void clusterPaper() {

    }
}