package com.example.paperservice.database;

import com.example.paperservice.Entity.EvalEntity;
import com.example.paperservice.Entity.PaperEntity;
import com.example.paperservice.Entity.UserEntity;
import org.junit.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public class EvaluationDaoTest extends BasicDaoTest{
    EvalEntity evalEntity = null;

    Date dateBefore = new Date();
    Date date = new Date();
    Date dateAfter = new Date();

    float eval = (float)0.1;

    @Before
    @Transactional
    @Rollback(false)
    public void setup(){
        userDao.save(userEntity);
        paperDao.save(paperEntity);

        dateBefore.setDate(dateBefore.getDate()-1);
        dateAfter.setTime(dateAfter.getDate()+1);
        evalEntity = new EvalEntity();
        evalEntity.setDate(date);
        evalEntity.setEval(eval);
        evalEntity.setPaperId(paperEntity.getId());
        evalEntity.setUsrId(userEntity.getId());
        evaluationDao.save(evalEntity);
    }
    @Test
    public void findByUsrIdOrderByDateDesc() {
        List<EvalEntity> evalList =  evaluationDao.findByUsrIdOrderByDateDesc(userEntity.getId());
        EvalEntity evalTest = evalList.get(0);
        Assert.assertEquals(evalTest.getDate(), date);
        Assert.assertEquals(evalTest.getEval(), eval, 0.001);
    }

    @Test
    public void countByUsrIdAndDateAfter(){
        List<EvalEntity> e = evaluationDao.findByUsrIdOrderByDateDesc(userEntity.getId());
        int result = evaluationDao.countByUsrIdAndDateAfter(userEntity.getId(), dateBefore);
        Assert.assertEquals(1,result);
    }

    @Test
    public void deleteByUsrIdAndDateBefore(){
        evaluationDao.deleteByUsrIdAndDateBefore(userEntity.getId(), dateAfter);
        //int result = evaluationDao.countByUsrIdAndDateAfter(userEntity.getId(), dateBefore);
        //Assert.assertEquals(result,0);
    }

    @Transactional
    @Rollback(false)
    @After
    public void rollback(){
        userDao.delete(userEntity);
        paperDao.delete(paperEntity);
        evaluationDao.delete(evalEntity);
    }
}
