package com.handsome.common.request;


import com.handsome.common.options.FlinkHomeOptions;

public abstract class RemoteRequest implements Request {

    private FlinkHomeOptions flinkHomeOptions;

    @Override
    public FlinkHomeOptions getFlinkHomeOptions() {
        return flinkHomeOptions;
    }

    @Override
    public void setFlinkHomeOptions(FlinkHomeOptions flinkHomeOptions) {
        this.flinkHomeOptions = flinkHomeOptions;
    }
}
