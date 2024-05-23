package com.dpline.common;

import java.util.regex.Pattern;

/**
 * Constants
 */
public final class Constants {

    public final static Long ZERO_FOR_LONG = 0L;

    /**
     * https prefix
     */
    public static final String HTTPS_SCHEMA = "https://";

    /**
     * http prefix
     */
    public static final String HTTP_SCHEMA = "http://";

    private Constants() {
        throw new UnsupportedOperationException("Construct Constants");
    }


    /**
     *  app.home
     */
    public static final String APP_HOME = "APP_HOME";

    /**
     * common配置文件的地址
     */
    public static final String COMMON_PROPERTIES_PATH = "/common.properties";

    // ==========================================================FileSystem start============================================

    /**
     * resource storage type
     */
    public static final String RESOURCE_STORAGE_TYPE = "resource.storage.type";

    // ====================== S3 FileSystem start =======================
    /**
     * AWS S3
     */
    public static final String AWS_S3_V4 = "com.amazonaws.services.s3.enableV4";

    /**
     * fs s3a endpoint
     */
    public static final String FS_S3A_ENDPOINT = "fs.s3a.endpoint";

    /**
     * fs s3a access key
     */
    public static final String FS_S3A_ACCESS_KEY = "fs.s3a.access.key";

    /**
     * fs s3a secret key
     */
    public static final String FS_S3A_SECRET_KEY = "fs.s3a.secret.key";


    // ====================== S3 FileSystem end =======================
    // =================== hadoop start ====================


    public static final String HADOOP_CONF_DIR = "fs.hdfs.conf.dir";
    /**
     * hdfs defaultFS property name. Should be consistent with the property name in hdfs-site.xml
     */
    public static final String FS_DEFAULTFS = "fs.defaultFS";

    /**
     * hadoop user
     */
    public static final String HDFS_ROOT_USER = "fs.hdfs.root.user";

    /**
     * kerberos
     */
    public static final String KERBEROS = "kerberos";

    /**
     * kerberos expire time
     */
    public static final String KERBEROS_EXPIRE_TIME = "kerberos.expire.time";

    /**
     * kerberos conf path
     */
    public static final String JAVA_SECURITY_KRB5_CONF_PATH = "java.security.krb5.conf.path";

    /**
     * login.user.keytab.username
     */
    public static final String LOGIN_USER_KEY_TAB_USERNAME = "login.user.keytab.username";

    /**
     * login.user.keytab.path
     */
    public static final String LOGIN_USER_KEY_TAB_PATH = "login.user.keytab.path";

    /**
     * java.security.krb5.conf
     */
    public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";

    /**
     * hadoop.security.authentication
     */
    public static final String HADOOP_SECURITY_AUTHENTICATION = "hadoop.security.authentication";

    /**
     * hadoop.security.authentication.startup.state
     */
    public static final String HADOOP_SECURITY_AUTHENTICATION_STARTUP_STATE = "hadoop.security.authentication.startup.state";
    // ================================= hadoop end ===================================
    // ==========================================================FileSystem end============================================

    /**
     * resource.view.suffixs
     */
    public static final String RESOURCE_VIEW_SUFFIXS = "resource.view.suffixs";

    /**
     * jar
     */
    public static final String JAR = "jar";

    public static final String RESOURCE_VIEW_SUFFIXS_DEFAULT_VALUE = "txt,log,sh,bat,conf,cfg,py,java,sql,xml,hql,properties,json,yml,yaml,ini,js";



    /**
     * comma ,
     */
    public static final String COMMA = ",";


    /**
     * SPACE " "
     */
    public static final String SPACE = " ";

    /**
     * BLACK ""
     */
    public static final String BLACK = "";


    public static final String LEFT_BRACKETS = "[";

    public static final String RIGHT_BRACKETS = "]";


    public static final String TIMESTAMP = "timestamp";
    public static final char SUBTRACT_CHAR = '-';
    public static final char DOT_CHAR = '.';
    public static final char ADD_CHAR = '+';
    public static final char MULTIPLY_CHAR = '*';
    public static final char DIVISION_CHAR = '/';
    public static final char LEFT_BRACE_CHAR = '(';
    public static final char RIGHT_BRACE_CHAR = ')';
    public static final String ADD_STRING = "+";
    public static final String STAR = "*";
    public static final String DIVISION_STRING = "/";
    public static final String LEFT_BRACE_STRING = "(";
    public static final char P = 'P';
    public static final char N = 'N';
    public static final String SUBTRACT_STRING = "-";

    public static final String COLON = ":";



    /**
     * date format of yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * date format of yyyyMMdd
     */
    public static final String YYYYMMDD = "yyyyMMdd";

    /**
     * date format of yyyyMMddHHmmss
     */
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * date format of yyyyMMddHHmmssSSS
     */
    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    /**
     * http connect time out
     */
    public static final int HTTP_CONNECT_TIMEOUT = 60 * 1000;


    /**
     * http connect request time out
     */
    public static final int HTTP_CONNECTION_REQUEST_TIMEOUT = 60 * 1000;

    /**
     * http header
     */
    public static final String HTTP_HEADER_UNKNOWN = "unKnown";

    /**
     * http X-Forwarded-For
     */
    public static final String HTTP_X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * http X-Real-IP
     */
    public static final String HTTP_X_REAL_IP = "X-Real-IP";

    /**
     * UTF-8
     */
    public static final String UTF_8 = "UTF-8";


    public static final String CRLF = "\r\n";

    public static final String CRLF_N = "\n";

    /**
     * user name regex
     */
    public static final Pattern REGEX_USER_NAME = Pattern.compile("^[a-zA-Z0-9._-]{3,39}$");
    

    /**
     * date format of yyyyMMddHHmmss
     */
    public static final String PARAMETER_FORMAT_TIME = "yyyyMMddHHmmss";

    /**
     * month_begin
     */
    public static final String MONTH_BEGIN = "month_begin";
    /**
     * add_months
     */
    public static final String ADD_MONTHS = "add_months";
    /**
     * month_end
     */
    public static final String MONTH_END = "month_end";
    /**
     * week_begin
     */
    public static final String WEEK_BEGIN = "week_begin";
    /**
     * week_end
     */
    public static final String WEEK_END = "week_end";

    /**
     * status
     */
    public static final String STATUS = "status";

    /**
     * message
     */
    public static final String MSG = "msg";

    /**
     * page size
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * current page no
     */
    public static final String PAGE_NUMBER = "page";


    /**
     *
     */
    public static final String DATA_LIST = "data";

    public static final String TOTAL_LIST = "totalList";

    public static final String CURRENT_PAGE = "currentPage";

    public static final String TOTAL_PAGE = "totalPage";

    public static final String TOTAL = "total";

    /**
     * session user
     */
    public static final String SESSION_USER = "session.user";

    public static final String SESSION_ID = "sessionId";

    /**
     * locale
     */
    public static final String LOCALE_LANGUAGE = "language";

    /**
     * session timeout
     */
    public static final int SESSION_TIME_OUT = 7200;
    /**
     * dataSource sensitive param
     */
    public static final String START_TIME = "start time";
    public static final String END_TIME = "end time";
    public static final String START_END_DATE = "startDate,endDate";

    /**
     * system date(yyyyMMddHHmmss)
     */
    public static final String PARAMETER_DATETIME = "system.datetime";
    /**
     * email regex
     */
    public static final Pattern REGEX_MAIL_NAME = Pattern.compile("^([a-z0-9A-Z]+[_|\\-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");

    public static final String MD5_TOKEN = "5744b6ca9ad8a108cf454b7fe4b12c2d";

    /**
     * 本地操作系统类型
     */
    public static final String OS_NAME = System.getProperty("os.name");

    /**
     * netty epoll 是否允许开启
     */
    public static final String NETTY_EPOLL_ENABLE = System.getProperty("netty.epoll.enable", "true");

    /**
     * netty 服务端心跳超时时间
     */
    public static final int NETTY_SERVER_HEART_BEAT_TIME = 1000 * 60 * 3 + 1000;

    /**
     * netty 客户端心跳超时时间
     */
    public static final int NETTY_CLIENT_HEART_BEAT_TIME = 1000 * 6;


    public static final int CPUS = Runtime.getRuntime().availableProcessors();


    public static final String SQL_MAIN_JAR = "dpline-flink-app";

    public static final String SQL_MAIN_CLASS = "com.dpline.flink.SqlMainClass";

    /**
     *
     */
    public final static String TRACE_ID = "traceId";

    public final static String JOB_ID = "jobId";

    public final static String RUN_TYPE = "runType";


    /**
     * string true
     */
    public static final String STRING_TRUE = "true";

}
