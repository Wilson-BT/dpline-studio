package com.handsome.common.enums;

public enum SqlErrorType {

    /**
     * sql is null
     */
    VERIFY_FAILED(0),

    /**
     * sql syntax error
     */
    SYNTAX_ERROR(1),

    /**
     * unSupport sql
     */
    UNSUPPORTED_SQL(2),

    /**
     * sql right
     */
    SQL_RIGHT(3);

    public final int errorType;

    SqlErrorType(int errorType) {
        this.errorType = errorType;
    }

    public static SqlErrorType of(Integer errorType) {
        for (SqlErrorType type : values()) {
            if (type.errorType == errorType) {
                return type;
            }
        }
        return null;
    }

}
