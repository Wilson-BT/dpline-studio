package com.dpline.dao.rto;

import com.dpline.dao.entity.JobSavepoint;
import lombok.Data;

import java.util.Arrays;

@Data
public class JobSavepointRto extends JobSavepoint {

    String checkpointType;

    Long projectId;

    public enum CheckpointType {

        CHECKPOINT,SAVEPOINT,NONE;

        public static CheckpointType of(String key){
            return Arrays.stream(CheckpointType.values()).filter(value->{
                return value.name().equals(key);
            }).findFirst().orElse(null);
        }
    }


}
