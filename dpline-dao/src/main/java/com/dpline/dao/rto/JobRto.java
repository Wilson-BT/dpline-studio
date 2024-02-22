package com.dpline.dao.rto;

import com.dpline.dao.entity.Job;
import lombok.Data;

import java.util.List;

@Data
public class JobRto extends GenericRto<Job> {

    private Long id;

    public Long folderId;

    private Long projectId;

    private List<Long> folderIds;

    private String savepointPath;

    private String savepointName;

}
