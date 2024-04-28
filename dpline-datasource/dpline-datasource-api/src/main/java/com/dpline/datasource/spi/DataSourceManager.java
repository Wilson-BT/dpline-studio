package com.dpline.datasource.spi;

import com.dpline.common.enums.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

@Component
@Slf4j
public class DataSourceManager {

    private final Map<String, DataSourceChannel> datasourceClientMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void installPlugin() {
        ServiceLoader<DataSourceChannelFactory> loader = ServiceLoader.load(DataSourceChannelFactory.class);
        loader.forEach(t -> {
            final String name = t.getName();

            log.info("注册数据源插件: {}", name);

            if (datasourceClientMap.containsKey(name)) {
                throw new IllegalStateException(format("存在重复数据源插件名称 '%s'", name));
            }
            loadDatasourceClient(t);
            log.info("已注册数据源插件: {}", name);
        });

    }

    private void loadDatasourceClient(DataSourceChannelFactory datasourceChannelFactory) {
        DataSourceChannel datasourceChannel = datasourceChannelFactory.create();
        datasourceClientMap.put(datasourceChannelFactory.getName(), datasourceChannel);
    }

    public DataSourceChannel getDataSourceChannel(DataSourceType dataSourceType) {
        if (dataSourceType == null) {
            return null;
        }
        DataSourceChannel dataSourceChannel = datasourceClientMap.get(dataSourceType.getName());
        return dataSourceChannel;
    }
}
