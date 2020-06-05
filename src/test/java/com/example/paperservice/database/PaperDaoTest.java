package com.example.paperservice.database;

import com.example.paperservice.Entity.PaperEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PaperDaoTest extends BasicDaoTest{

    @Transactional
    @Rollback(false)
    @Before
    public void setup(){
        paperEntity = paperDao.save(paperEntity);
        System.out.println(paperEntity.getId());
    }

    @Test
    public void findById() {
        PaperEntity paperTest = paperDao.findById(paperEntity.getId());
        Assert.assertEquals(paperEntity.getTitle(), paperTest.getTitle());
        Assert.assertEquals(paperEntity.getAbst(),paperTest.getAbst());
        Assert.assertEquals(paperEntity.getBrowseNum(), paperTest.getBrowseNum());
    }

    @Test
    public void existsById() {
        boolean result = paperDao.existsById(paperEntity.getId());
        Assert.assertEquals(true, result);
    }

    @Test
    public void findByGroupId() {
        int size = 5;
        List<PaperEntity> result = paperDao.findByGroupId(paperEntity.getGroupID(), PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "browseNum"))).getContent();
        Assert.assertEquals(size, result.size());
        for(PaperEntity paperTest: result) {
            Assert.assertEquals(groupId, paperTest.getGroupID());
        }
    }

    @Test
    public void findAll() {
        List<PaperEntity> result = paperDao.findAll();
    }

    @Test
    public void testFindAll() {
        int size = 5;
        List<PaperEntity> result = paperDao.findAll(PageRequest.of(1, 5)).getContent();
        Assert.assertEquals(size, result.size());
    }

    @Test
    public void findIDByGroup() {
        List<Integer> result = paperDao.findIDByGroup(paperEntity.getGroupID());
    }

    @Transactional
    @Rollback(false)
    @After
    public void rollback(){
        paperDao.delete(paperEntity);
    }
}