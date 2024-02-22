package com.dpline.dao.dto;

import com.dpline.common.params.DataStreamConfig;
import com.dpline.common.params.JobConfig;
import com.dpline.dao.domain.SourceConfig;
import com.dpline.dao.entity.File;
import lombok.Data;

import java.util.List;

@Data
public class FileDto extends File {

    String userCode;

    String tagName;

    String tagDesc;

    String projectName;

    private String filePath;

    private JobConfig jobConfig;

    private SourceConfig sourceConfig;

    private DataStreamConfig dataStreamConfig;

    /**
     * 是否上线标识，1：上线，0，未上线
     */
    private Integer onLine;

    private String dag;

    private Long jobId;

    private String jobStatus;

    private List<String> envList;

}
