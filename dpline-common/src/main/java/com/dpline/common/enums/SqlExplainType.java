package com.dpline.common.enums;

public enum SqlExplainType {

    /**
     * sql is null
     */
    VERIFY_FAILED(0,"sql is null"),

    /**
     * sql syntax error
     */
    SYNTAX_ERROR(1,"sql syntax error"),

    /**
     * unSupport sql
     */
    UNSUPPORTED_SQL(2,"unSupport sql"),

    /**
     * sql right
     */
    SQL_RIGHT(3,"success");

    public final int explainType;

    public final String message;

    SqlExplainType(int explainType,String message) {
        this.explainType = explainType;
        this.message = message;
    }

    public static SqlExplainType of(Integer explainType) {
        for (SqlExplainType type : values()) {
            if (type.explainType == explainType) {
                return type;
            }
        }
        return null;
    }

}
