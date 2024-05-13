package com.dpline.console.config;

import com.dpline.common.store.FsStore;
import com.dpline.common.store.HdfsStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {
    @Bean(name="fsStore")
    public FsStore createFsStore() {
        return new HdfsStore();
    }
}
