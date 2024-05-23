package com.dpline.common.request;

import com.dpline.common.params.JobDefinitionOptions;
import com.dpline.common.params.RuntimeOptions;
import lombok.Data;

@Data
public class FlinkSubmitRequest extends FlinkRequest {

    RuntimeOptions runtimeOptions;

    JobDefinitionOptions jobDefinitionOptions;
}
