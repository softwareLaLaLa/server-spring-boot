package com.example.paperservice.database;

import com.example.paperservice.Entity.PaperEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperDao extends JpaRepository<PaperEntity, Integer> {
    PaperEntity findById(int id);
    boolean existsById(int id);
    //List<PaperEntity> findByGroupId
    Page<PaperEntity> findByGroupId(int groupID, Pageable pageable);
    //List<PaperEntity> findByIdBetweenOrderByBrowseNumDesc(int low, int high, Pageable pageable);
    List<PaperEntity> findAll();
}