package com.moh.ihrisupdatetool.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "jobs")
public class JobEntity {

    @PrimaryKey
    @NonNull
    @SerializedName("job_id")
    private String jobId;
    @SerializedName("job")
    private String jobName;

    public JobEntity(@NonNull String jobId, String jobName) {
        this.jobId = jobId;
        this.jobName = jobName;
    }

    @NonNull
    public String getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    @Override
    public String toString() {
        return "JobEntity{" +
                "jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                '}';
    }
}
