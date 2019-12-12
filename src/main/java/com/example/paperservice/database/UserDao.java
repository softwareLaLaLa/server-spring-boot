package com.example.paperservice.database;

import com.example.paperservice.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;

@Repository
public interface UserDao extends JpaRepository<UserEntity, Integer> {
    UserEntity findById(int id);
    UserEntity findByName(String name);
    boolean existsByName(String name);
}
