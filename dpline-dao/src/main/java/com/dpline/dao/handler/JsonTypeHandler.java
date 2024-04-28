package com.dpline.dao.handler;

import cn.hutool.json.JSONUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JsonTypeHandler<T extends Object> extends BaseTypeHandler<T> {

    private Class clazz;

    public JsonTypeHandler() {
    }

    public JsonTypeHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, T t, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JSONUtil.toJsonStr(t));
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return (T) JSONUtil.toBean(resultSet.getString(columnName), clazz);
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int index) throws SQLException {
        return (T) JSONUtil.toBean(resultSet.getString(index), clazz);
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int index) throws SQLException {
        return (T) JSONUtil.toBean(callableStatement.getString(index), clazz);
    }
}
