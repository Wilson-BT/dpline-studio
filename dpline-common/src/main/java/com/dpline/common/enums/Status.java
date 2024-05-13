package com.dpline.common.enums;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Optional;

/**
 * status enum
 */
public enum Status {

    SUCCESS(200, "success", "成功"),
    COMMUNICATION_ERROR(300, "communication error", "通信异常"),
    AUTHENTICATION_FAILED(401, "Authentication failed", "Authentication failed"),
    USER_STATE_ERROR(402, "user state error", "User state error"),
    INSERT_EXCEPTION(1000, "Insert value failed","数据新增失败！"),
    UPDATE_EXCEPTION(1001,"Update value failed","数据更新失败！"),
    DELETE_EXCEPTION(1002, "Delete value failed","数据删除失败！"),
    SELECT_EXCEPTION(1003, "Get value failed","数据获取失败！"),
    DIR_EXIST_EXCEPTION(1100, "dir exist exception","目录已经存在！"),
    FILE_NOT_EXIST_EXCEPTION(1101, "file not exist exception","文件不存在！"),
    FILE_BE_BOUNDED_WITH_JOB(1102, "file has been bounded by job [{0}]","文件已经被线上任务[{0}]绑定了"),
//    FILE_BE_PUBLISH_TO_PROD(1103, "file has been published to prod","文件已经被发布到线上"),
    FILE_NAME_NOT_VALID(1104, "file name not valid, only ([A-Za-z0-9_\\-])+","文件名不规范，只能使用 ([A-Za-z0-9_\\-])+"),
    FILE_NAME_EXIST_ERROR(1105,"same file name [{0}] exist","file name [{0}] 已经存在"),
    PROD_CAN_NOT_EDIT_FILE(1106,"prod can not edit file","生产环境不能修改文件"),
    USER_CAN_NOT_EDIT_FILE(1107,"user can not edit file","用户不能修改文件"),
    FILE_YML_CONFIG_NOT_EXIT(1108,"file yaml conf is not exit","文件的配置文件不存在"),
    MAIN_FILE_NOT_EXIST_ERROR(1109,"main file [dpline-flink-app.jar] not exit","main file [dpline-app.jar] 不存在"),
    FILE_DAG_CAN_NOT_PARSE_ERROR(1110,"file dag can not parse error","文件DAG无法解析"),
    NAME_NOT_EXIT_ERROR(1201,"tag name is not exit","TAG名称不存在"),
    JAR_FILE_EXIST(1301, "jar file {0} already exists,please delete it or change name!", "JAR文件相同的名称[{0}]已存在，请删除或修改资源名"),
    JAR_NAME_ERROR(1302, "jar file {0} error,please change name!", "JAR文件[{0}]名称错误，请更改名称"),
    JAR_FUNCTION_TYPE_ERROR(1303,"jar file type error,仅限于[MAIN|UDF|CONNECTOR|EXTENDED]","jar 文件类型错误,仅限于[MAIN|UDF|CONNECTOR|EXTENDED]"),
    JAR_UPLOAD_FAILED(1304,"jar file upload failed","jar 文件上传失败"),
    JAR_AUTH_TYPE_ERROR(1305,"jar auth type [{0}] error,only support [project|public]","jar 包项目or公共资源参数[{0}]存在问题，仅支持[project|public]"),
    JAR_FILE_SAME_VERSION_EXIST(1306, "jar file same version already exists,please delete it or change name!", "JAR文件相同的版本已存在，请删除或修改资源"),
    PRIMARY_JAR_NOT_FILE_ERROR(1307,"primary jar not exist error","不存在primary默认jar"),
    JAR_FILE_NOT_EXIST(1308, "jar file {0} not exists!", "JAR文件[{0}]不存在"),
    JAR_FILE_SAME_NAME_ERROR(1309, "jar file {0} exists!", "JAR文件[{0}]已经存在"),

    JAR_FILE_DELETE_ERROR(1310, "jar file {0} delete error!", "JAR文件[{0}]删除失败"),
    JAR_FILE_BOUNDED_ERROR(1311, "jar file bound by [{0}] error!", "JAR文件已经被任务[{0}]绑定"),
    JAR_FILE_VERSION_NOT_EXIST(1312, "jar file version not exit!", "JAR文件版本不存在"),

    DEPEND_RESOURCE_HAS_NO_JAR_FILE(1313, "depend jar file has no jar file!", "依赖资源组不存在相关资源jar包"),

    PROJECT_ID_NOT_EXIST(1401,"project id is not exits","项目id不存在"),

    SAME_JOB_NAME_EXIST(1501,"same job name has exists","相同的任务名已经存在了"),
    JOB_CLUSTER_NOT_EXIST(1502,"job cluster not exists","Job的集群不存在"),
    JOB_CONFIG_NOT_EXIST(1503,"job config not exists","JOB 配置文件不存在"),
    JOB_MODE_NOT_SUPPORT(1504,"job mode not support","JOB 任务模式不支持"),
    JOB_FLINK_NOT_EXIST(1505,"job motor not support","JOB 引擎不存在"),
    JOB_DEPLOY_ERROR(1506,"job deploy error","JOB 部署失败"),
    JOB_RUNNING_ERROR(1507,"job running error","JOB正在运行"),

    DATA_STREAM_CONFIG_NULL_ERROR(1508,"data stream config is null","data stream config 配置为空"),

    CREATE_START_REQUEST_ERROR(1509,"create start request error","创建启动请求失败"),
    JOB_ONLINE_FAILED(1510,"job online failed, main jar is not exits","Main jar不存在，上线失败"),
    SAVEPOINT_NAME_NULL_ERROR(1600,"savepoint name null error","savepoint 名称为空"),
    SOURCE_CONFIG_NULL_ERROR(1601,"source config null error","依赖资源为空"),

    JOB_NOT_EXIST(1602,"job not exist","JOB不存在"),

    JOB_IMAGE_NOT_EXISTS(1603,"job image not exists","JOB镜像不存在"),

    DELETE_CHECKPOINT_ERROR(1604,"delete checkpoint error","删除checkpoint失败"),

    DELETE_SAVEPOINT_ERROR(1605,"delete savepoint error","删除savepoint失败"),

    // ===================== cluster ==================
    CLUSTER_ADD_LIST_NULL(2001,"cluster add list is null","添加列表为空"),
    USER_RELATION_ALL_SAVED(2002,"user relation all saved","用户集群关系已经存在"),
    CLUSTER_SAME_NAME_EXIST(2003,"cluster same name exist","集群相同的名称已经存在"),
    CLUSTER_CREATE_ERROR(2004,"cluster create error","集群创建失败"),

    SAME_CLUSTER_EXIST(2005,"same cluster exist","相同的集群已经存在"),
    CLUSTER_BOUNDED_JOB_EXIST(2006,"cluster bounded job [{0}] exist","集群绑定的Job [{0}] 存在,不能删除"),
    CLUSTER_BOUNDED_USER_EXIST(2007,"cluster bounded user [{0}] exist","集群绑定的用户 [{0}] 存在,不能删除"),

    CLUSTER_DELETE_ERROR(2008,"cluster delete error","集群删除失败"),

    CLUSTER_UPDATE_ERROR(2009,"cluster update error","集群更新失败"),

    CLUSTER_PARAMS_IS_EMPTY(2010,"cluster params empty error","集群参数为空异常"),

    CLUSTER_OPERATOR_ERROR(2011,"cluster operate error","集群操作失败"),

    FLINK_REAL_VERSION_NOT_EXISTS(3001,"flink real version not exists","Flink真正版本不存在"),
    FLINK_TASK_SUBMITTING_WAIT_ERROR(3002,"flink task is submitting now","任务正在提交中，请等待10S后重试"),
    FLINK_TASK_STOPPING_ERROR(3003,"flink task is stopping now","任务正在停止中，稍后重试"),
    FLINK_VERSION_NOT_EXISTS(3004,"flink version not exists","Flink版本不存在"),
    FLINK_PATH_NOT_EXISTS(3005,"flink path not exists","Flink路径不存在"),

    ALERT_CONFIG_EDIT_ERROR(4000,"alter config edit error","告警配置修改失败"),

    INTERNAL_SERVER_ERROR_ARGS(10000, "Internal Server Error: {0}", "服务端异常: {0}"),
    REQUEST_PARAMS_NOT_VALID_ERROR(10001, "request parameter {0} is not valid", "请求参数[{0}]无效"),
    USER_CODE_EXIST(10002, "user code already exists", "用户编码已存在"),
    USER_NAME_EXIST(10003, "user name already exists", "用户名已存在"),
    USER_NAME_NULL(10004, "user name is null", "用户名不能为空"),
    USER_NOT_EXIST(10010, "user {0} not exists", "用户[{0}]不存在"),
    USER_NAME_PASSWD_ERROR(10013, "user name or password error", "用户名或密码错误"),
    LOGIN_SESSION_FAILED(10014, "create session failed!", "创建session失败"),
    LOGIN_SUCCESS(10042, "login success", "登录成功"),
    USER_LOGIN_FAILURE(10043, "user login failure", "用户登录失败"),
    CREATE_USER_ERROR(10090, "create user error", "创建用户错误"),
    GET_USER_INFO_ERROR(10098, "get user info error", "获取用户信息错误"),
    USER_LIST_ERROR(10099, "user list error", "查询用户列表错误"),
    VERIFY_USERNAME_ERROR(10100, "verify username error", "用户名验证错误"),
    UNAUTHORIZED_USER_ERROR(10101, "unauthorized user error", "查询未授权用户错误"),
    AUTHORIZED_USER_ERROR(10102, "authorized user error", "查询授权用户错误"),
    SIGN_OUT_ERROR(10123, "sign out error", "退出错误"),
    IP_IS_EMPTY(10125, "ip is empty", "IP地址不能为空"),
    NAME_EXIST(10135, "name {0} already exists", "名称[{0}]已存在"),
    USER_DISABLED(10148, "The current user is disabled", "当前用户已停用"),
    DATASOURCE_EXIST(10184, "datasource already exists", "数据源已存在"),


    USER_NO_OPERATION_PERM(30001, "user has no operation privilege", "当前用户没有操作权限"),
    USER_NO_OPERATION_PROJECT_PERM(30002, "user is not has project {1} permission", "当前用户[{0}]没有[{1}]项目的操作权限"),
    ALERT_NAME_EXIST_ERROR(30003, "alert name [{}] exist error", "告警实例名称[{}]已经存在"),
    ALERT_ID_NULL_ERROR(30004, "alert id is null", "告警实例ID为空"),
    ALERT_BOUNDED_JOB_ERROR(30005, "alert has been bounded by job [{}]", "告警实例已经被JOB [{}]绑定"),
    TASK_DEFINE_EXIST_ERROR(50031, "task definition [{0}] exist", "任务草稿[{0}]存在"),

    CREATE_START_TASK_REQUEST_ERROR(60001, "create task start request error", "创建任务开始请求失败"),
    MAIN_JAR_RESOURCE_NOT_EXISTS(60002, "main jar [{0}] is not exist.", "此前的Main jar资源[{0}]已经不存在，需要重新部署"),
    RUNTIME_OPTIONS_PARSE_ERROR(60003, "runtime_options parse error.", "RUNTIME_OPTIONS 解析失败"),

    FLINK_YAML_PARSE_ERROR(60004, "flink yaml parse error.", "FLINK YAML 解析失败"),

    // flink 版本
    FLINK_CLIENT_NOT_EXISTS(120008,"this flink path [{0}] not exists","Flink 版本路径不存在，需要检查"),
    RUN_FLINK_TASK_INSTANCE_ERROR(150007,"run flink task instance failed","Flink任务实例运行失败"),
    STOP_FLINK_TASK_INSTANCE_ERROR(150008,"stop flink task instance failed","Flink任务实例停止失败"),
    TRIGGER_FLINK_TASK_INSTANCE_ERROR(150009,"trigger flink task instance failed","Flink任务实例创建Savepoint检查点失败"),
    /**
     * docker image
     */
    DOCKER_IMAGE_NAME_EXIST_ERROR(160002, "docker image name has exists", "docker镜像已经存在"),
    DOCKER_IMAGE_NOT_EXIST_ERROR(160003, "docker image not exists", "docker镜像不存在"),
    DOCKER_IMAGE_TASK_BOUND_ERROR(160005, "docker image bounded by task [{}]", "docker镜像已经被任务[{}]绑定");






    private final int code;
    private final String enMsg;
    private final String zhMsg;

    Status(int code, String enMsg, String zhMsg) {
        this.code = code;
        this.enMsg = enMsg;
        this.zhMsg = zhMsg;
    }

    public int getCode() {
        return this.code;
    }

    /**
     * 根据语言设置，返回相应的语言信息
     * @return
     */
    public String getMsg() {
        if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(LocaleContextHolder.getLocale().getLanguage())) {
            return this.zhMsg;
        } else {
            return this.enMsg;
        }
    }

    /**
     * Retrieve Status enum entity by status code.
     * @param code
     * @return
     */
    public static Optional<Status> findStatusBy(int code) {
        for (Status status : Status.values()) {
            if (code == status.getCode()) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
