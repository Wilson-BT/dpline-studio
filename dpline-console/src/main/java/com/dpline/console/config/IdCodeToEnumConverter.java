package com.dpline.console.config;

import com.dpline.console.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IdCodeToEnumConverter<T extends BaseEnum> implements Converter<String, T> {
    private final Map<String, T> idEnumMap = new HashMap<>();
    private final Map<String, T> codeEnumMap = new HashMap<>();

    public IdCodeToEnumConverter(Class<T> enumType) {
        Arrays.stream(enumType.getEnumConstants())
                .forEach(x -> {
                    idEnumMap.put(Integer.toString(x.getKey()), x);
                    codeEnumMap.put(x.getValue(), x);
                });
    }

    @Override
    public T convert(String source) {
        return Optional.of(source)
                .map(codeEnumMap::get)
                .orElseGet(() -> Optional.of(source)
                        .map(idEnumMap::get)
                        .orElseThrow(() -> new IllegalArgumentException("参数没有找到相应的枚举类型。。。请检查参数")));
    }
}
