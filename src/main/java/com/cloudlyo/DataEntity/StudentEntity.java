package com.cloudlyo.DataEntity;

import javax.persistence.*;

@Entity
@Table(name = "student")
public class StudentEntity {
    @Id
    String openId;
    @Column
    String name;
    @Column
    String jobId;
    @Column
    String classes;          //class1_class2_...

    public StudentEntity(String openId, String name, String jobId, String classes) {
        this.openId = openId;
        this.name = name;
        this.jobId = jobId;
        this.classes = classes;
    }

    public StudentEntity() {
    }

    public String getOpenId() {
        return openId;
    }

    public String getName() {
        return name;
    }

    public String getJobId() {
        return jobId;
    }

    public String getClasses() {
        return classes;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }
}
