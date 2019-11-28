package com.example.paperservice.database;

import com.example.paperservice.Entity.HotPaperEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HotPaperDao extends JpaRepository<HotPaperEntity, Integer> {
    List<HotPaperEntity> findTop20OrderByHotDesc();
    HotPaperEntity findById(int paper_id);
    void deleteHotPaperEntityByLastActiveTimeBefore(Date date);
    boolean existsById(int paper_id);
}
