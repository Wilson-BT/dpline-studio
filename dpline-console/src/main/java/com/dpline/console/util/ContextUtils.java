package com.dpline.console.util;

public class ContextUtils {

    private static ThreadLocal<Context> currentLocalContext = new InheritableThreadLocal<>();


    public static Context get() {
        return currentLocalContext.get();
    }

    public static void set(Context context) {
        currentLocalContext.set(context);
    }

    public static void unset() {
        currentLocalContext.remove();
    }

}
