package com.dpline.console.config;

import com.dpline.console.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;

public class IdCodeToEnumConverterFactory implements ConverterFactory<String, BaseEnum> {

    /**
     * 目标类型缓存
     */
    @SuppressWarnings("rawtypes")
    private static final Map<Class, Converter> CONVERTERS = new HashMap<>();

    /**
     * @param targetType 枚举的类型
     * @param <T>
     * @return
     */
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        //noinspection unchecked
        Converter<String, T> converter = CONVERTERS.get(targetType);
        if (converter == null) {
            converter = new IdCodeToEnumConverter<>(targetType);
            CONVERTERS.put(targetType, converter);
        }
        return converter;
    }
}
