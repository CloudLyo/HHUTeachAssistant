package com.cloudlyo.DataEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckInEntity,Long> {
    public List<CheckInEntity> findByClassIdOrderByStartTimeDesc(long classId);
    public void deleteCheckInEntitiesByClassId(long classId);

}
