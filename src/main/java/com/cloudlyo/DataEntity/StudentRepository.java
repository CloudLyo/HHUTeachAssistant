package com.cloudlyo.DataEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<StudentEntity,String> {
    public List<StudentEntity> findByOpenId(String openId);
}

