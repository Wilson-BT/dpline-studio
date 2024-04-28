package com.dpline.console.interceptor;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.dpline.common.util.CodeGenerateUtils;

public class CustomIdGenerator implements IdentifierGenerator {
    @Override
    public Long nextId(Object entity) {
        long id = 11111111111L;
        try {
            id = CodeGenerateUtils.getInstance().genCode();
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            e.printStackTrace();
        }
        return id;
    }
}
