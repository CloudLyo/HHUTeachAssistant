package com.cloudlyo.DataEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<ClassEntity,Long>{
    public List<ClassEntity> findByName(String name);
    public List<ClassEntity> findByTeacherId(String teacherId);
    public List<ClassEntity> findById(long id);
}


