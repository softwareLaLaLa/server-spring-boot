package com.example.paperservice.Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "candidateGroup")
    private String candidateGroup;

    @Column(name = "favorGroup")
    private String group;

    public String getCandidateGroup() {
        return candidateGroup;
    }

    public String getGroup() {
        return group;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRole() {
        return role;
    }

    public UserEntity(String name, String password, String role){
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public UserEntity(){}

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCandidateGroup(String candidateGroup) {
        this.candidateGroup = candidateGroup;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}