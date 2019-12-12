package com.example.paperservice.database;

import com.example.paperservice.Entity.EvalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EvaluationDao extends JpaRepository<EvalEntity, Integer> {
    EvalEntity findByUsrIdAndPaperId(int usr_id, int paper_id);
    List<EvalEntity> findByUsrIdOrderByDateDesc(int usr_id);
    void deleteByUsrIdAndDateBefore(int usr_id, Date date);
    Integer countByUsrIdAndDateAfter(int usr_id, Date date);
}
