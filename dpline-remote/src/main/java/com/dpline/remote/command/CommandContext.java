
package com.dpline.remote.command;

import com.dpline.common.util.JSONUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  command context
 */
public class CommandContext implements Serializable {

    private Map<String, String> items = new LinkedHashMap<>();

    public Map<String, String> getItems() {
        return items;
    }

    public void setItems(Map<String, String> items) {
        this.items = items;
    }

    public void put(String key, String value) {
        items.put(key, value);
    }

    public String get(String key) {
        return items.get(key);
    }

    public byte[] toBytes() {
        return JSONUtils.toJsonByteArray(this);
    }

    public static CommandContext valueOf(byte[] src) {
        return JSONUtils.parseObject(src, CommandContext.class);
    }
}
