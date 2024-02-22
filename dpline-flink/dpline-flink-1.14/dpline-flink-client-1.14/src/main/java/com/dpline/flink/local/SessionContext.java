package com.dpline.flink.local;

import com.dpline.common.util.Asserts;
import com.dpline.flink.core.context.AbstractSessionContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import java.util.Map;

/**
 * Session 上下文环境
 */
public class SessionContext extends AbstractSessionContext {

    public SessionContext(Map<String, String> configMap) {
        this.configMap = configMap;
    }

    /**
     * create Environment by configuration
     * @return
     */
    public synchronized TableEnvironmentImpl createTableEnvironment() {
        return (StreamTableEnvironmentImpl) StreamTableEnvironmentImpl.create(
            createStreamExecutionEnvironment(),
            EnvironmentSettings.newInstance().build(),
            TableConfig.getDefault());
    }

    private StreamExecutionEnvironment createStreamExecutionEnvironment() {
        return new StreamExecutionEnvironment(convertToConfiguration());
    }

    public Configuration convertToConfiguration() {
        if(Asserts.isNull(configMap)){
            throw new NullPointerException("Session Config is null.");
        }
        return Configuration.fromMap(this.configMap);
    }


}
