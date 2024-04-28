//package com.dpline.console.util;
//
//
//public class EnvHolder {
//
//    private static final ThreadLocal<String> LOCAL = new ThreadLocal<>();
//
//    public static void addEnv(String env) {
//        LOCAL.set(env);
//    }
//
//    public static String getEnv() {
//        return LOCAL.get();
//    }
//
//    public static void clearEnv() {
//        LOCAL.remove();
//    }
//
//    public static void clearEnvAndAddEnv(String env) {
//        clearEnv();
//        addEnv(env);
//    }
//
//}
