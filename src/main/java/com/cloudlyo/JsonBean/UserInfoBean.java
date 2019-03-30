package com.cloudlyo.JsonBean;

public class UserInfoBean {
    String jobId;
    String name;

    public String getJobId() {
        return jobId;
    }

    public String getName() {
        return name;
    }

    public UserInfoBean(String jobId, String name) {
        this.jobId = jobId;
        this.name = name;
    }

    public UserInfoBean() {
    }
}
