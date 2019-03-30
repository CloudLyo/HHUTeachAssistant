package com.cloudlyo.DataEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<TeacherEntity,String> {
    public List<TeacherEntity> findByName(String name);
    public List<TeacherEntity> findByOpenId(String openId);
}

