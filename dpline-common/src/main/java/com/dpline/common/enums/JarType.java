package com.dpline.common.enums;

import java.util.Optional;

public class JarType {

    /**
     * jar 的功能 类别
     */
    public enum FunctionType {
        MAIN(0,"MAIN"),
        UDF(1,"UDF"),
        CONNECTOR(2,"CONNECTOR"),
        EXTENDED(3,"EXTENDED");

        private int key;
        private String value;

        FunctionType() {
        }

        FunctionType(int key, String value) {
            this.key = key;
            this.value = value;
        }
        public int getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public static Optional<FunctionType> of(String val){
            for (FunctionType value : FunctionType.values()){
                if(value.getValue().equals(val)){
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }
    }

    /**
     * jar 的所属环境类别
     */
    public enum AuthType {

        PUBLIC(0,"public"),

        PROJECT(1,"project");

        private int key;
        private String value;

        AuthType() {
        }

        AuthType(int key, String value) {
            this.key = key;
            this.value = value;
        }
        public int getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public static Optional<AuthType> of(String value){
            for (AuthType entity : AuthType.values()){
                if(entity.getValue().equals(value)){
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        }
    }

}
