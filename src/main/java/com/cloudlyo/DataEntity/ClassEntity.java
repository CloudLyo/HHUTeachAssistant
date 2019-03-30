package com.cloudlyo.DataEntity;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "class")
public class ClassEntity {
    @Id
    Long id;

    @Column
    String name;

    @Column
    String students="";                  //studentid1_studentid2_...

    @Column
    String teacherId;

    public void setName(String name) {
        this.name = name;
    }

    public void setStudents(String students) {
        this.students = students;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStudents() {
        return students;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClassEntity() {
    }
}
