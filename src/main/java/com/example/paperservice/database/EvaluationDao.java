package com.example.paperservice.database;

import com.example.paperservice.Entity.EvalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EvaluationDao extends JpaRepository<EvalEntity, Integer> {
    EvalEntity findByUsr_idAndPaper_id(int usr_id, int paper_id);
    List<EvalEntity> findByUsr_idOrderByDateDesc(int usr_id);
    void deleteByUsr_idAndDateBefore(int usr_id, Date date);
    Integer countEvalNumByUsr_idAndDateAfter(int usr_id, Date date);
}
