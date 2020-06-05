package com.example.paperservice.database;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({EvaluationDaoTest.class,HotPaperDaoTest.class, PaperDaoTest.class, TagDaoTest.class, UserDaoTest.class})
public class DataBaseTestSuit {
}
