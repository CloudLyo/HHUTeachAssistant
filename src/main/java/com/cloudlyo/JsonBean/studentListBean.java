package com.cloudlyo.JsonBean;

public class studentListBean {
    String name;
    String jobId;
    String isCheckedIn;

    public studentListBean(String name, String jobId, String isCheckIn) {
        this.name = name;
        this.jobId = jobId;
        this.isCheckedIn = isCheckIn;
    }
}
