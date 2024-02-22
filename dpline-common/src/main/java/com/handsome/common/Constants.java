package com.handsome.common;

import org.apache.commons.lang.SystemUtils;
import java.util.regex.Pattern;

/**
 * Constants
 */
public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Construct Constants");
    }

    /**
     * data basedir path
     */
    public static final String DATA_BASEDIR_PATH = "data.basedir.path";

    /**
     *  app.home
     */
    public static final String APP_HOME = "APP_HOME";


    /**
     * common配置文件的地址
     */
    public static final String COMMON_PROPERTIES_PATH = "/common.properties";


    /**
     * com.amazonaws.services.s3.enableV4
     * S3 配置
     */
    public static final String AWS_S3_V4 = "com.amazonaws.services.s3.enableV4";

    public static final String FS_DEFAULTFS = "fs.defaultFS";

    public static final String FS_S3A_ENDPOINT = "fs.s3a.endpoint";

    public static final String FS_S3A_ACCESS_KEY = "fs.s3a.access.key";

    public static final String FS_S3A_SECRET_KEY = "fs.s3a.secret.key";

    /**
     * resource.view.suffixs
     */
    public static final String RESOURCE_VIEW_SUFFIXS = "resource.view.suffixs";

    public static final String K8S_LOCAL_PATH_PREFIX = "k8s.local.path.prefix";
    /**
     * jar
     */
    public static final String JAR = "jar";

    public static final String RESOURCE_VIEW_SUFFIXS_DEFAULT_VALUE = "txt,log,sh,bat,conf,cfg,py,java,sql,xml,hql,properties,json,yml,yaml,ini,js";


    /**
     * string true
     */
    public static final String STRING_TRUE = "true";

    /**
     * resource storage type
     */
    public static final String RESOURCE_STORAGE_TYPE = "resource.storage.type";

    /**
     * comma ,
     */
    public static final String COMMA = ",";

    /**
     * COLON :
     */
    public static final String COLON = ":";

    /**
     * QUESTION ?
     */
    public static final String QUESTION = "?";

    /**
     * SPACE " "
     */
    public static final String SPACE = " ";

    /**
     * BLACK ""
     */
    public static final String BLACK = "";

    /**
     * SINGLE_SLASH /
     */
    public static final String SINGLE_SLASH = "/";

    /**
     * DOUBLE_SLASH //
     */
    public static final String DOUBLE_SLASH = "//";


    /**
     * com.handsome.operator host，default localhost
     */
    public static final String OPERATOR_LISTEN_HOST = "operator.listen.host";

    /**
     * com.handsome.operator port，default 50055
     */
    public static final String OPERATOR_LISTEN_PORT = "operator.listen.port";



    public static final String LEFT_BRACKETS = "[";

    public static final String RIGHT_BRACKETS = "]";


    public static final String TIMESTAMP = "timestamp";
    public static final char SUBTRACT_CHAR = '-';
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
     * httpclient soceket time out
     */
    public static final int SOCKET_TIMEOUT = 60 * 1000;

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
     * master/worker server use for zk
     */
    public static final String ALIAS = "alias";
    public static final String CONTENT = "content";




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
    public static final String PAGE_NUMBER = "pageNo";


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

    public static final String SESSION_USER_TYPE = "sessionUserType";

    public static final String SESSION_USER_ID = "sessionUserId";

    public static final String SESSION_USER_NAME = "sessionUserName";

    /**
     * locale
     */
    public static final String LOCALE_LANGUAGE = "language";

    /**
     * session timeout
     */
    public static final int SESSION_TIME_OUT = 7200;
    public static final int MAX_FILE_SIZE = 1024 * 1024 * 1024;
    public static final String UDF = "UDF";
    public static final String CLASS = "class";

    /**
     * dataSource sensitive param
     */
    public static final String DATASOURCE_PASSWORD_REGEX = "(?<=((?i)password((\\\\\":\\\\\")|(=')))).*?(?=((\\\\\")|(')))";

    /**
     * authorize all perm 111
     */
    public static final int AUTHORIZE_ALL_PERM = 7;
    /**
     * no permission 000
     */
    public static final int NO_PERMISSON = 0;
    /**
     * read permission 010
     */
    public static final int READ_PERMISSION = 2;


    /**
     * authorize readable perm
     */
    public static final int AUTHORIZE_READABLE_PERM = 4;


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

    /**
     * Ingress 配置
     */
    public static final String INGRESS_HOST = "flink.topsports.com.cn";

    public static final String K8S_INGRESS_NAME = "flink-prd-ingress";

    public static final String K8S_INGRESS_VERSION = "networking.k8s.io/v1beta1";

    public static final String NGINX_REWRITE = "nginx.org/rewrites";


}
