package com.dpline.common.request;

import com.dpline.common.enums.LocalTaskOperateType;
import com.dpline.common.enums.StreamType;
import com.dpline.common.params.FlinkHomeOptions;

import java.util.List;

/**
 *
 * 执行本地调试的时候的 Request
 *
 */
public class FlinkLocalRequest implements Request {

    private StreamType streamType;

    private LocalTaskOperateType operatorType;

    private FlinkHomeOptions flinkHomeOptions;

    private List<String> sqls;

    public List<String> getSqls() {
        return sqls;
    }

    public void setSqls(List<String> sqls) {
        this.sqls = sqls;
    }


    public LocalTaskOperateType getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(LocalTaskOperateType operatorType) {
        this.operatorType = operatorType;
    }
    public StreamType getStreamType() {
        return streamType;
    }

    public void setStreamType(StreamType streamType) {
        this.streamType = streamType;
    }


//    @Override
//    public FlinkHomeOptions getFlinkHomeOptions() {
//        return null;
//    }
//
//    @Override
//    public void setFlinkHomeOptions(FlinkHomeOptions flinkHomeOptions) {
//
//    }
}
