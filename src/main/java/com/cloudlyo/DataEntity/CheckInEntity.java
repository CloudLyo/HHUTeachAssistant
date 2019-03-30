package com.cloudlyo.DataEntity;

import javax.persistence.*;

@Entity
@Table(name = "checkIn")
public class CheckInEntity {
    @Id
    @GeneratedValue
    long id;

    @Column
    long classId;

    @Column
    String startTime;                                //yyyy-MM-01 hh:mm:ss EE

    @Column
    long lastTime;                                  //minute

    @Column
    String checkedId;                                   //openId1_openId2_...

    public long getId() {
        return id;
    }

    public long getClassId() {
        return classId;
    }

    public String getStartTime() {
        return startTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public String getCheckedId() {
        return checkedId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setClassId(long classId) {
        this.classId = classId;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public void setCheckedId(String checkedId) {
        this.checkedId = checkedId;
    }

    public CheckInEntity(long classId, String startTime, long lastTime, String checkedId) {
        this.classId = classId;
        this.startTime = startTime;
        this.lastTime = lastTime;
        this.checkedId = checkedId;
    }

    public CheckInEntity() {
    }
}
