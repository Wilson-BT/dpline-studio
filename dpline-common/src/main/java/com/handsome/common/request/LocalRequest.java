package com.handsome.common.request;

import com.handsome.common.enums.OperatorType;
import com.handsome.common.enums.StreamType;
import com.handsome.common.options.FlinkHomeOptions;

import java.util.List;

/**
 *
 * 执行本地调试的时候的 Request
 *
 */
public class LocalRequest implements Request {

    private StreamType streamType;

    private OperatorType operatorType;

    private FlinkHomeOptions flinkHomeOptions;

    private List<String> sqls;

    public List<String> getSqls() {
        return sqls;
    }

    public void setSqls(List<String> sqls) {
        this.sqls = sqls;
    }


    public OperatorType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(OperatorType operatorType) {
        this.operatorType = operatorType;
    }
    public StreamType getStreamType() {
        return streamType;
    }

    public void setStreamType(StreamType streamType) {
        this.streamType = streamType;
    }


    @Override
    public FlinkHomeOptions getFlinkHomeOptions() {
        return null;
    }

    @Override
    public void setFlinkHomeOptions(FlinkHomeOptions flinkHomeOptions) {

    }
}
