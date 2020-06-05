package com.example.paperservice.database;

import com.example.paperservice.Entity.UserEntity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest extends BasicDaoTest{
    @Before
    @Transactional
    @Rollback(false)
    public void setup(){
        userDao.save(userEntity);
    }

    @Test
    public void findById() {
        UserEntity userTest = userDao.findById(userEntity.getId());
        Assert.assertEquals(userName, userTest.getName());
        Assert.assertEquals(password, userTest.getPassword());
        Assert.assertEquals(role, userTest.getRole());
    }

    @Test
    public void findByName() {
        UserEntity userTest = userDao.findByName(userName);
        Assert.assertEquals(password, userTest.getPassword());
        Assert.assertEquals(role, userTest.getRole());
    }

    @Test
    public void existsByName() {
        boolean result = userDao.existsByName(userName);
        Assert.assertEquals(true, result);
    }

    @After
    @Transactional
    @Rollback(false)
    public void rollBack(){
        userDao.delete(userEntity);
    }
}