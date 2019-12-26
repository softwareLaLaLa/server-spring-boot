package com.example.paperservice.database;

import com.example.paperservice.Entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TagDao extends JpaRepository<TagEntity, Integer> {
    TagEntity findById(int id);
    TagEntity findByName(String name);
    boolean existsByName(String name);
    void deleteByDateBeforeAndNumLessThan(Date date, int num);
    List<TagEntity> findByDateBeforeAndNumLessThan(Date date, int num);
    //void deleteAllById(List<Integer> idList);
    //public boolean existsById(int id);
    List<TagEntity> findAll();
}
