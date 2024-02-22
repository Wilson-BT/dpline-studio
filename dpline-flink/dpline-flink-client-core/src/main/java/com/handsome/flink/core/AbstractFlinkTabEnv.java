package com.handsome.flink.core;

import com.handsome.common.options.LocalFlinkEnvConfig;
import com.handsome.common.util.Asserts;
import com.handsome.common.enums.StreamType;
import lombok.Data;

/**
 * flink sql env
 */
@Data
public abstract class AbstractFlinkTabEnv {
    /**
     * batch table env
     */
    private Object btEnv;
    /**
     * stream table env
     */
    private Object stEnv;

    /**
     * create table env
     */
    public abstract AbstractFlinkTabEnv createStreamTableEnv(StreamType flag, boolean reWrite);

    /**
     * set local config,such as param/cpu/mem/checkpoint
     */
    public abstract void updateConfig(LocalFlinkEnvConfig localConfig);

    public Object getObjectEnv(StreamType streamType){
        switch (streamType){
            case BATCH:
                return  Asserts.isNull(btEnv) ?
                    createStreamTableEnv(streamType,false).getObjectEnv(streamType)
                    : getBtEnv();
            case STREAM:
            default:
                return  Asserts.isNull(stEnv) ?
                    createStreamTableEnv(streamType,false).getObjectEnv(streamType)
                    : getStEnv();
        }
    }

}