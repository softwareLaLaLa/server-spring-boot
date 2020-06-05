package com.example.paperservice.database;

import com.example.paperservice.Entity.TagEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TagDaoTest extends BasicDaoTest{
    @Autowired
    TagDao tagDao;

    String tagName = "tag";
    int num = 0;
    Date tagDate = new Date();
    Date dateAfter = new Date();
    TagEntity tagEntity = new TagEntity(tagName, null, num, tagDate);

    @Before
    @Transactional
    @Rollback(false)
    public void setup(){
        tagDao.save(tagEntity);
    }

    @Test
    public void findById() {
        TagEntity tagTest = tagDao.findById(tagEntity.getId());
        Assert.assertEquals(tagName, tagTest.getName());
        Assert.assertEquals(tagDate, tagTest.getDate());
    }

    @Test
    public void findByName() {
        TagEntity tagTest = tagDao.findByName(tagName);
        Assert.assertEquals(tagEntity.getId(), tagTest.getId());
        Assert.assertEquals(tagDate, tagTest.getDate());
    }

    @Test
    public void existsByName() {
        boolean result = tagDao.existsByName(tagName);
        Assert.assertEquals(true, result);
    }

    @Test
    public void deleteByDateBeforeAndNumLessThan() {
        tagDao.deleteByDateBeforeAndNumLessThan(dateAfter, num-1);
    }

    @Test
    public void findByDateBeforeAndNumLessThan() {
        tagDao.findByDateBeforeAndNumLessThan(dateAfter, num-1);
    }

    @Test
    public void findAll() {
        int size = 5;
        List<TagEntity> result = tagDao.findAll(PageRequest.of(1, size)).getContent();
        Assert.assertEquals(size, result.size());
    }

    @Test
    public void findAllId() {
        List<Integer> result = tagDao.findAllId();
    }

    @After
    @Transactional
    @Rollback(false)
    public void rollBack(){
        tagDao.delete(tagEntity);
    }
}