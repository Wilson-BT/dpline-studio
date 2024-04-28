package com.dpline.flink.core;

import com.dpline.common.request.FlinkRequest;
import com.dpline.common.request.Response;

import java.util.function.Function;


public abstract class TaskOperator implements Function<FlinkRequest, Response> {

    public abstract Response apply(FlinkRequest request);

}
