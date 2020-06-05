package com.example.paperservice.service;

import com.example.paperservice.DataProcess.AddedPaperData;
import com.example.paperservice.DataProcess.ID;
import com.example.paperservice.DataProcess.PaperSimpleData;
import com.example.paperservice.DataProcess.TagRela;
import com.example.paperservice.Entity.PaperEntity;
import com.example.paperservice.Entity.TagEntity;
import com.example.paperservice.database.PaperDao;
import com.example.paperservice.database.RedisService;
import com.example.paperservice.database.TagDao;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataManagerServiceTest extends BasicServiceTest{
    @Mock
    private PaperDao paperDao;
    @Mock
    private TagDao tagDao;
    @Mock
    private RedisService redisService;
    @Mock
    private BaseService baseService;
    @InjectMocks
    DataManagerService dataManagerService;

    String title = "title";
    String abst = "abst";
    String resUrl = "resUrl";
    int browseNum = 0;
    int evalNum = 0;
    int uncheckNum = 0;
    int groupId = 0;

    PaperEntity paperEntity = new PaperEntity(title, abst, resUrl, browseNum, evalNum, uncheckNum, groupId);
    @Test
    void addNewPapers() throws IOException {
        List<List<Float>> paperRelationData = new ArrayList<>();
        List<Float> temp = new ArrayList<>();
        temp.add(new Float(1.0));
        paperRelationData.add(temp);
        doReturn(paperRelationData).when(dataManagerService).getRelationData(any());

        Map<Integer, Map<Integer, Float>> data = new HashMap<>();
        Map<Integer, Float> relation = new HashMap<>();
        relation.put(0, new Float(1));
        data.put(0, relation);
        doReturn(data).when(redisService).getAllGroupTagData();

        List<Integer> groupIDList = new ArrayList<>();
        groupIDList.add(0);
        doReturn(groupIDList).when(redisService).getAllGroupID();

        //paperEntity.setGroupID(0);

        List<AddedPaperData> newPaperData = new ArrayList<>();
        AddedPaperData addedPaperData = new AddedPaperData();
        addedPaperData.setPaperEntity(paperEntity);
        addedPaperData.setRelation(relation);
        newPaperData.add(addedPaperData);
        dataManagerService.addNewPapers(newPaperData);

        ArgumentCaptor<PaperEntity> paperEntityArgumentCaptor = ArgumentCaptor.forClass(PaperEntity.class);
        verify(paperDao).save(paperEntityArgumentCaptor.capture());
        Assert.assertEquals(paperEntity.getTitle(), paperEntityArgumentCaptor.getValue().getTitle());
        Assert.assertEquals(paperEntity.getGroupID(), 0);

        //ArgumentCaptor<Integer> paperIdCaptor = ArgumentCaptor.forClass(int.class);
        //ArgumentCaptor paperTagDataCaptor = ArgumentCaptor.forClass(Map.class);
        //verify(redisService).addPaperTagData(paperIdCaptor.capture(), (Map<Integer, TagRela>) paperTagDataCaptor.capture());
        //Assert.assertEquals(paperEntityArgumentCaptor.getValue().getId(), (int)paperIdCaptor.getValue());
        //Assert.assertEquals()
    }

    @Test
    void addNewTag() {
        List<String> tags = new ArrayList<>();
        tags.add("法律"); tags.add("政治");
        dataManagerService.addNewTag(tags);

        ArgumentCaptor<String> tagNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(tagDao).findByName(tagNameArgumentCaptor.capture());
        Assert.assertEquals("法律", tagNameArgumentCaptor.getValue());
    }

    @Test
    void deletePaper() {
        List<Integer> paperIDList = new ArrayList<>();
        paperIDList.add(1);
        dataManagerService.deletePaper(paperIDList);
    }

    @Test
    void deleteTag() {
        Set<Integer> tagIDList = new HashSet<>();
        tagIDList.add(1);
        dataManagerService.deleteTag(tagIDList);
    }

    @Test
    void mergeTag() {
        List<Integer> tagID = new ArrayList<>();
        tagID.add(1); tagID.add(2);
        dataManagerService.mergeTag(tagID);

        ArgumentCaptor<TagEntity> tagEntityArgumentCaptor1 = ArgumentCaptor.forClass(TagEntity.class);
        verify(tagDao).delete(tagEntityArgumentCaptor1.capture());
        Assert.assertEquals(2, tagEntityArgumentCaptor1.getValue().getId());

        ArgumentCaptor<TagEntity> tagEntityArgumentCaptor2 = ArgumentCaptor.forClass(TagEntity.class);
        verify(tagDao).save(tagEntityArgumentCaptor2.capture());
        Assert.assertEquals(1, tagEntityArgumentCaptor2.getValue().getId());
    }

    @Test
    void getPaperData() {
        doReturn(paperEntity).when(paperDao).findAll(any(PageRequest.class));
        doReturn(new PaperSimpleData()).when(baseService).getPaperSimpleData(paperEntity);
        dataManagerService.getPaperData(1);
    }

    @Test
    void getTagData() {
        List<TagEntity> tagEntityList = new ArrayList<>();
        doReturn(tagEntityList).when(tagDao).findAll(any(PageRequest.class));
    }
//    @Test
//    void getGroupData() {
//        ID pageNum = new ID();
//        pageNum.setId(1);
//        dataManagerService.getGroupData(pageNum);
//        doNothing().when(dataManagerService).getRelativePaperNum(any(Map.class));
//    }
}