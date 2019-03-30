package com.cloudlyo.DataEntity;

import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    String openId;

    @Column
    String currentJob;

    public UserEntity() {
    }

    public UserEntity(@UniqueElements String openId, String currentJob) {
        this.openId = openId;
        this.currentJob = currentJob;
    }

    public String getOpenId() {
        return openId;
    }

    public String getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(String currentJob) {
        this.currentJob = currentJob;
    }
}
