package com.example.paperservice.database;

import com.example.paperservice.Entity.EvalEntity;
import com.example.paperservice.Entity.PaperEntity;
import com.example.paperservice.Entity.UserEntity;
import com.example.paperservice.database.EvaluationDao;
import com.example.paperservice.database.HotPaperDao;
import com.example.paperservice.database.PaperDao;
import com.example.paperservice.database.UserDao;
import com.netflix.discovery.shared.Application;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BasicDaoTest {
    @Autowired
    EvaluationDao evaluationDao;
    @Autowired
    PaperDao paperDao;
    @Autowired
    UserDao userDao;
    @Autowired
    HotPaperDao hotPaperDao;

    String title = "title";
    String abst = "abst";
    String resUrl = "resUrl";
    int browseNum = 0;
    int evalNum = 0;
    int uncheckNum = 0;
    int groupId = 0;

    String userName = "user";
    String password = "password";
    String role = "user";

    UserEntity userEntity = new UserEntity(userName, password, role);
    PaperEntity paperEntity = new PaperEntity(title, abst, resUrl, browseNum, evalNum, uncheckNum, groupId);

}
