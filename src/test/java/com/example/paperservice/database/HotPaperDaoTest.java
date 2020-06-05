package com.example.paperservice.database;

import com.example.paperservice.Entity.HotPaperEntity;
import com.example.paperservice.Entity.PaperEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public class HotPaperDaoTest extends BasicDaoTest{
    HotPaperEntity hotPaperEntity;

    int hot = 10;
    Date date = new Date();
    Date dateAfter = new Date();

    @Transactional
    @Rollback(false)
    @Before
    public void setup(){
        dateAfter.setDate(dateAfter.getDate()+1);
        paperDao.save(paperEntity);

        hotPaperEntity = new HotPaperEntity();
        hotPaperEntity.setId(paperEntity.getId());
        hotPaperEntity.setHot(hot);
        hotPaperEntity.setLastActiveTime(date);
        hotPaperDao.save(hotPaperEntity);
    }

    @Test
    public void findTop20ByOrderByHotDesc(){
        List<HotPaperEntity> paperList = hotPaperDao.findTop20ByOrderByHotDesc();

        HotPaperEntity hotPaperTest = paperList.get(0);
        Assert.assertEquals(hotPaperTest.getId(), paperEntity.getId());
        Assert.assertEquals(hotPaperTest.getHot(), hot);
        Assert.assertEquals(hotPaperTest.getLastActiveTime(), date);
    }

    @Test
    public void findById(){
        HotPaperEntity hotPaperTest = hotPaperDao.findById(paperEntity.getId());
        Assert.assertEquals(hotPaperTest.getHot(), hot);
        Assert.assertEquals(hotPaperTest.getLastActiveTime(), date);
    }

    @Test
    public void existById(){
        boolean result = hotPaperDao.existsById(paperEntity.getId());
        Assert.assertEquals(result, true);
    }

    @Test
    public void deleteHotPaperEntityByLastActiveTimeBefore(){
        hotPaperDao.deleteHotPaperEntityByLastActiveTimeBefore(dateAfter);
    }

    @Transactional
    @Rollback(false)
    @After
    public void rollback(){
        paperDao.delete(paperEntity);
        hotPaperDao.delete(hotPaperEntity);
    }
}
