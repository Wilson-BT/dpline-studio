package com.handsome.common.enums;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Optional;

/**
 * status enum
 */
public enum Status {

    SUCCESS(200, "success", "成功"),
    INTERNAL_SERVER_ERROR_ARGS(10000, "Internal Server Error: {0}", "服务端异常: {0}"),
    REQUEST_PARAMS_NOT_VALID_ERROR(10001, "request parameter {0} is not valid", "请求参数[{0}]无效"),
    USER_NAME_EXIST(10003, "user name already exists", "用户名已存在"),
    USER_NAME_NULL(10004, "user name is null", "用户名不能为空"),
    S3_OPERATION_ERROR(10006, "S3 operation error", "S3操作错误"),
    USER_NOT_EXIST(10010, "user {0} not exists", "用户[{0}]不存在"),
    ALERT_GROUP_NOT_EXIST(10011, "alarm group not found", "告警组不存在"),
    ALERT_GROUP_EXIST(10012, "alarm group already exists", "告警组名称已存在"),
    USER_NAME_PASSWD_ERROR(10013, "user name or password error", "用户名或密码错误"),
    LOGIN_SESSION_FAILED(10014, "create session failed!", "创建session失败"),
    PROJECT_NOT_FOUNT(10018, "project {0} not found ", "项目[{0}]不存在"),
    PROJECT_ALREADY_EXISTS(10019, "project {0} already exists", "项目名称[{0}]已存在"),
    CREATE_ALERT_GROUP_ERROR(10027, "create alert group error", "创建告警组错误"),
    QUERY_ALL_ALERTGROUP_ERROR(10028, "query all alertgroup error", "查询告警组错误"),
    LIST_PAGING_ALERT_GROUP_ERROR(10029, "list paging alert group error", "分页查询告警组错误"),
    UPDATE_ALERT_GROUP_ERROR(10030, "update alert group error", "更新告警组错误"),
    DELETE_ALERT_GROUP_ERROR(10031, "delete alert group error", "删除告警组错误"),
    ALERT_GROUP_GRANT_USER_ERROR(10032, "alert group grant user error", "告警组授权用户错误"),
    LOGIN_SUCCESS(10042, "login success", "登录成功"),
    USER_LOGIN_FAILURE(10043, "user login failure", "用户登录失败"),
    UPDATE_PROJECT_ERROR(10046, "update project error", "更新项目信息错误"),
    QUERY_PROJECT_DETAILS_BY_CODE_ERROR(10047, "query project details by code error", "查询项目详细信息错误"),
    CREATE_PROJECT_ERROR(10048, "create project error", "创建项目错误"),
    LOGIN_USER_QUERY_PROJECT_LIST_PAGING_ERROR(10049, "login user query project list paging error", "分页查询项目列表错误"),
    DELETE_PROJECT_ERROR(10050, "delete project error", "删除项目错误"),
    QUERY_UNAUTHORIZED_PROJECT_ERROR(10051, "query unauthorized project error", "查询未授权项目错误"),
    QUERY_AUTHORIZED_PROJECT(10052, "query authorized project", "查询授权项目错误"),
    QUERY_QUEUE_LIST_ERROR(10053, "query queue list error", "查询队列列表错误"),
    CREATE_RESOURCE_ERROR(10054, "create resource error", "创建资源错误"),
    UPDATE_RESOURCE_ERROR(10055, "update resource error", "更新资源错误"),
    QUERY_RESOURCES_LIST_ERROR(10056, "query resources list error", "查询资源列表错误"),
    QUERY_RESOURCES_LIST_PAGING(10057, "query resources list paging", "分页查询资源列表错误"),
    DELETE_RESOURCE_ERROR(10058, "delete resource error", "删除资源错误"),
    VERIFY_RESOURCE_BY_NAME_AND_TYPE_ERROR(10059, "verify resource by name and type error", "资源名称或类型验证错误"),
    VIEW_RESOURCE_FILE_ON_LINE_ERROR(10060, "view resource file online error", "查看资源文件错误"),
    CREATE_RESOURCE_FILE_ON_LINE_ERROR(10061, "create resource file online error", "创建资源文件错误"),
    RESOURCE_FILE_IS_EMPTY(10062, "resource file is empty", "资源文件内容不能为空"),
    EDIT_RESOURCE_FILE_ON_LINE_ERROR(10063, "edit resource file online error", "更新资源文件错误"),
    DOWNLOAD_RESOURCE_FILE_ERROR(10064, "download resource file error", "下载资源文件错误"),
    CREATE_UDF_FUNCTION_ERROR(10065, "create udf function error", "创建UDF函数错误"),
    VIEW_UDF_FUNCTION_ERROR(10066, "view udf function error", "查询UDF函数错误"),
    UPDATE_UDF_FUNCTION_ERROR(10067, "update udf function error", "更新UDF函数错误"),
    QUERY_UDF_FUNCTION_LIST_PAGING_ERROR(10068, "query udf function list paging error", "分页查询UDF函数列表错误"),
    QUERY_DATASOURCE_BY_TYPE_ERROR(10069, "query datasource by type error", "查询数据源信息错误"),
    VERIFY_UDF_FUNCTION_NAME_ERROR(10070, "verify udf function name error", "UDF函数名称验证错误"),
    DELETE_UDF_FUNCTION_ERROR(10071, "delete udf function error", "删除UDF函数错误"),
    AUTHORIZED_FILE_RESOURCE_ERROR(10072, "authorized file resource error", "授权资源文件错误"),
    AUTHORIZE_RESOURCE_TREE(10073, "authorize resource tree display error", "授权资源目录树错误"),
    UNAUTHORIZED_UDF_FUNCTION_ERROR(10074, "unauthorized udf function error", "查询未授权UDF函数错误"),
    AUTHORIZED_UDF_FUNCTION_ERROR(10075, "authorized udf function error", "授权UDF函数错误"),
    REVOKE_K8S_NAMESPACE_ERROR(10081, "revoke k8s namespace error", "撤权k8s客户端错误"),
    QUERY_TASK_LIST_PAGING_ERROR(10082, "query task list paging error", "分页查询任务列表错误"),
    QUERY_TASK_RECORD_LIST_PAGING_ERROR(10083, "query task record list paging error", "分页查询任务记录错误"),
    CREATE_USER_ERROR(10090, "create user error", "创建用户错误"),
    QUERY_USER_LIST_PAGING_ERROR(10091, "query user list paging error", "分页查询用户列表错误"),
    UPDATE_USER_ERROR(10092, "update user error", "更新用户错误"),
    DELETE_USER_BY_ID_ERROR(10093, "delete user by id error", "删除用户错误"),
    GRANT_PROJECT_ERROR(10094, "grant project error", "授权项目错误"),
    GRANT_RESOURCE_ERROR(10095, "grant resource error", "授权资源错误"),
    GRANT_K8S_NAMESPACE_ERROR(10096, "grant k8s namespace error", "授权k8s客户端错误"),
    GET_USER_INFO_ERROR(10098, "get user info error", "获取用户信息错误"),
    USER_LIST_ERROR(10099, "user list error", "查询用户列表错误"),
    VERIFY_USERNAME_ERROR(10100, "verify username error", "用户名验证错误"),
    UNAUTHORIZED_USER_ERROR(10101, "unauthorized user error", "查询未授权用户错误"),
    AUTHORIZED_USER_ERROR(10102, "authorized user error", "查询授权用户错误"),
    SIGN_OUT_ERROR(10123, "sign out error", "退出错误"),
    IP_IS_EMPTY(10125, "ip is empty", "IP地址不能为空"),
    NAME_NULL(10134, "name must be not null", "名称不能为空"),
    NAME_EXIST(10135, "name {0} already exists", "名称[{0}]已存在"),
    SAVE_ERROR(10136, "save error", "保存错误"),
    USER_DISABLED(10148, "The current user is disabled", "当前用户已停用"),
    QUERY_AUTHORIZED_AND_USER_CREATED_PROJECT_ERROR(10162, "query authorized and user created project error error", "查询授权的和用户创建的项目错误"),
    TRANSFORM_PROJECT_OWNERSHIP(10179, "Please transform project ownership [{0}]", "请先转移项目所有权[{0}]"),
    QUERY_ALERT_GROUP_ERROR(10180, "query alert group error", "查询告警组错误"),
    CURRENT_LOGIN_USER_TENANT_NOT_EXIST(10181, "the tenant of the currently login user is not specified", "未指定当前登录用户的租户"),
    REVOKE_PROJECT_ERROR(10182, "revoke project error", "撤销项目授权错误"),
    QUERY_AUTHORIZED_USER(10183, "query authorized user error", "查询拥有项目权限的用户错误"),

    UDF_FUNCTION_NOT_EXIST(20001, "UDF function not found", "UDF函数不存在"),
    UDF_FUNCTION_EXISTS(20002, "UDF function already exists", "UDF函数已存在"),
    RESOURCE_NOT_EXIST(20004, "resource not exist", "资源不存在"),
    RESOURCE_EXIST(20005, "resource already exists", "资源已存在"),
    RESOURCE_SUFFIX_NOT_SUPPORT_VIEW(20006, "resource suffix do not support online viewing", "资源文件后缀不支持查看"),
    RESOURCE_SIZE_EXCEED_LIMIT(20007, "upload resource file size exceeds limit", "上传资源文件大小超过限制"),
    RESOURCE_SUFFIX_FORBID_CHANGE(20008, "resource suffix not allowed to be modified", "资源文件后缀不支持修改"),
    UDF_RESOURCE_SUFFIX_NOT_JAR(20009, "UDF resource suffix name must be jar", "UDF资源文件后缀名只支持[jar]"),
    HDFS_COPY_FAIL(20010, "hdfs copy {0} -> {1} fail", "hdfs复制失败：[{0}] -> [{1}]"),
    RESOURCE_FILE_EXIST(20011, "resource file {0} already exists in hdfs,please delete it or change name!", "资源文件[{0}]在S3中已存在，请删除或修改资源名"),
    RESOURCE_FILE_NOT_EXIST(20012, "resource file {0} not exists in hdfs!", "资源文件[{0}]在hdfs中不存在"),
    UDF_RESOURCE_IS_BOUND(20013, "udf resource file is bound by UDF functions:{0}", "udf函数绑定了资源文件[{0}]"),
    RESOURCE_IS_USED(20014, "resource file is used by process definition", "资源文件被上线的流程定义使用了"),
    PARENT_RESOURCE_NOT_EXIST(20015, "parent resource not exist", "父资源文件不存在"),
    RESOURCE_NOT_EXIST_OR_NO_PERMISSION(20016, "resource not exist or no permission,please view the task node and remove error resource", "请检查任务节点并移除无权限或者已删除的资源"),
    RESOURCE_IS_AUTHORIZED(20017, "resource is authorized to user {0},suffix not allowed to be modified", "资源文件已授权其他用户[{0}],后缀不允许修改"),
    TASK_RESOURCE_IS_BOUND(20018,"resource file is bounded by task [{0}]","资源被任务[{0}]绑定"),

    USER_NO_OPERATION_PERM(30001, "user has no operation privilege", "当前用户没有操作权限"),
    USER_NO_OPERATION_PROJECT_PERM(30002, "user {0} is not has project {1} permission", "当前用户[{0}]没有[{1}]项目的操作权限"),


    TASK_DEFINE_NOT_EXIST(50030, "task definition [{0}] does not exist", "任务定义[{0}]不存在"),
    TASK_DEFINE_EXIST_ERROR(50031, "task definition [{0}] exist", "任务草稿[{0}]存在"),
    TASK_DEFINE_RELATION_EXIST_ERROR(50032, "task definition relation [{0}] exist", "任务草稿[{0}]存在关联"),
    CREATE_TASK_DEFINITION_ERROR(50037, "create flink task definition failed", "创建任务错误"),
    UPDATE_TASK_DEFINITION_ERROR(50038, "update task definition error", "更新任务定义错误"),
    TASK_TAG_NAME_EXIST_ERROR(50031,"task tag [{0}] has already exists","任务标记[{0}]已经存在"),
    TASK_TAG_RELATION_EXIST_ERROR(50031,"task tag relation [{0}] exists","任务标记[{0}]存在关联"),
    CREATE_TASK_TAG_ERROR(50032, "create flink task tag failed", "创建任务tag错误"),
    DELETE_TASK_TAG_ERROR(50033, "delete flink task tag failed", "删除任务tag错误"),
    S3_NOT_STARTUP(60001, "S3 not startup", "S3未启用"),

    /**
     * for monitor
     */
    QUERY_DATABASE_STATE_ERROR(70001, "query database state error", "查询数据库状态错误"),

    CREATE_ACCESS_TOKEN_ERROR(70010, "create access token error", "创建访问token错误"),
    GENERATE_TOKEN_ERROR(70011, "generate token error", "生成token错误"),
    QUERY_ACCESSTOKEN_LIST_PAGING_ERROR(70012, "query access token list paging error", "分页查询访问token列表错误"),
    UPDATE_ACCESS_TOKEN_ERROR(70013, "update access token error", "更新访问token错误"),
    DELETE_ACCESS_TOKEN_ERROR(70014, "delete access token error", "删除访问token错误"),
    ACCESS_TOKEN_NOT_EXIST(70015, "access token not exist", "访问token不存在"),
    QUERY_ACCESSTOKEN_BY_USER_ERROR(70016, "query access token by user error", "查询访问指定用户的token错误"),
    TOKEN_EXPIRED(70017, "token expired", "token已过期"),

    //plugin
    PLUGIN_NOT_A_UI_COMPONENT(110001, "query plugin error, this plugin has no UI component", "查询插件错误，此插件无UI组件"),
    QUERY_PLUGINS_RESULT_IS_NULL(110002, "query plugins result is null", "查询插件为空"),
    QUERY_PLUGINS_ERROR(110003, "query plugins error", "查询插件错误"),
    QUERY_PLUGIN_DETAIL_RESULT_IS_NULL(110004, "query plugin detail result is null", "查询插件详情结果为空"),

    UPDATE_ALERT_PLUGIN_INSTANCE_ERROR(110005, "update alert plugin instance error", "更新告警组和告警组插件实例错误"),
    DELETE_ALERT_PLUGIN_INSTANCE_ERROR(110006, "delete alert plugin instance error", "删除告警组和告警组插件实例错误"),
    GET_ALERT_PLUGIN_INSTANCE_ERROR(110007, "get alert plugin instance error", "获取告警组和告警组插件实例错误"),
    CREATE_ALERT_PLUGIN_INSTANCE_ERROR(110008, "create alert plugin instance error", "创建告警组和告警组插件实例错误"),
    QUERY_ALL_ALERT_PLUGIN_INSTANCE_ERROR(110009, "query all alert plugin instance error", "查询所有告警实例失败"),
    PLUGIN_INSTANCE_ALREADY_EXIT(110010, "plugin instance already exit", "该告警插件实例已存在"),
    LIST_PAGING_ALERT_PLUGIN_INSTANCE_ERROR(110011, "query plugin instance page error", "分页查询告警实例失败"),
    DELETE_ALERT_PLUGIN_INSTANCE_ERROR_HAS_ALERT_GROUP_ASSOCIATED(110012, "failed to delete the alert instance, there is an alarm group associated with this alert instance",
            "删除告警实例失败，存在与此告警实例关联的警报组"),
    PROCESS_DEFINITION_VERSION_IS_USED(110013,"this process definition version is used","此工作流定义版本被使用"),

    // flink 版本
    CREATE_FLINK_VERSION_ERROR(120001, "create flink version error", "创建flink version失败"),
    FLINK_VERSION_NAME_EXISTS(120002,"this flink name [{0}] already exists","Flink 版本名称已经存在"),
    FLINK_VERSION_PATH_EXISTS(120003,"this flink path [{0}] already exists","Flink 版本路径已经存在"),
    FLINK_VERSION_NAME_IS_NULL(120004,"this flink version name shouldn't be empty.","flink 版本名称不能为空"),
    UPDATE_FLINK_VERSION_ERROR(120005, "update environment [{0}] info error", "更新环境[{0}]信息失败"),
    DELETE_FLINK_VERSION_ERROR(120006, "delete environment error", "删除环境信息失败"),
    DELETE_FLINK_VERSION_RELATED_TASK_EXISTS(120007, "this environment has been used in tasks,so you can't delete it.", "该环境已经被任务使用，所以不能删除该环境信息"),
    FLINK_CLIENT_NOT_EXISTS(120008,"this flink path [{}] not exists","Flink 版本路径不存在，需要检查"),
    VERIFY_FLINK_VERSION_ERROR(1200009, "verify environment error", "验证环境信息错误,flink home name should like flink-1.13.2..."),
    LIST_FLINK_VERSION_INSTANCE_ERROR(120010, "list flink version instance1 error", "删除环境信息失败"),
    // k8s 环境
    CREATE_K8S_ENVIRIONMENT_ERROR(120011, "create k8s envirionment error", "创建K8s客户端失败."),
    K8S_ENVIRIONMENT_NAME_EXISTS(120012,"this k8s envirionment name [{0}] already exists","k8s 环境名称[{0}]已经存在"),
    K8S_ENVIRIONMENT_PATH_EXISTS(120013,"this k8s envirionment config path [{0}] already exists","kubeConfig 配置路径已经存在"),
    K8S_ENVIRIONMENT_PATH_NOT_EXISTS(120014,"this k8s envirionment config path [{0}] not exists","kubeConfig 配置路径不存在"),
    K8S_ENVIRIONMENT_NAME_IS_NULL(120015,"this k8s envirionment name shouldn't be empty.","flink 版本名称不能为空"),
    UPDATE_K8S_ENVIRIONMENT_ERROR(120016, "update k8s envirionment [{0}] info error", "更新环境[{0}]信息失败"),
    DELETE_K8S_ENVIRIONMENT_ERROR(120017, "delete k8s envirionment error", "删除环境信息失败"),
    DELETE_K8S_ENVIRIONMENT_RELATED_TASK_EXISTS(120018, "this k8s envirionment has been used in tasks,so you can't delete it.", "该环境已经被任务使用，所以不能删除该环境信息"),
    VERIFY_K8S_ENVIRIONMENT_ERROR(1200019, "verify k8s envirionment error", "验证环境信息错误"),
    LIST_K8S_ENVIRIONMENT_INSTANCE_ERROR(120020, "list k8s envirionment instance error", "列举k8s环境信息失败"),
    K8S_ENVIRIONMENT_ONLINE_ERROR(120021, "delete k8s envirionment error,k8s namespace is online", "删除环境信息失败,k8s namespace处于上线状态"),

    CREATE_FLINK_SESSION_CLUSTER_ERROR(130001, "create flink session error", "创建flink version失败"),
    FLINK_SESSION_CLUSTER_ID_EXISTS(130002,"this flink session name [{0}] already exists","Flink 版本名称已经存在"),
    FLINK_SESSION_CLUSTER_NAME_IS_NULL(130003,"this flink session name shouldn't be empty.","Flink 版本名称不能为空"),
    UPDATE_FLINK_SESSION_CLUSTER_ERROR(130004, "update flink session [{0}] info error", "更新环境[{0}]信息失败"),
    DELETE_FLINK_SESSION_CLUSTER_ERROR(130005, "delete flink session error", "删除环境信息失败"),
    DELETE_FLINK_SESSION_CLUSTER_RELATED_TASK_EXISTS(130006, "this flink session has been used in task [{}]", "该环境已经被任务[{}]使用"),
    VERIFY_FLINK_SESSION_CLUSTER_ERROR(130007, "verify flink session error", "验证环境信息错误"),
    FLINK_SESSION_CLUSTER_RELATED_EXISTS(130008,"flink session [{0}] relation exist","Flink session [{0}] 存在相关依赖"),
    NOT_ALLOW_TO_DISABLE_OWN_ACCOUNT(130009, "Not allow to disable your own account", "不能停用自己的账号"),
    FLINK_SESSION_CLUSTER_RUNNING(130010, "flink session cluster is running error", "Flink session 正在运行"),
    START_FLINK_SESSION_CLUSTER_ERROR(130011, "start flink session error", "启动 flink session 失败"),
    STOP_FLINK_SESSION_CLUSTER_ERROR(130012, "stop flink session error", "停止 flink session 失败"),
    FLINK_SESSION_CLUSTER_NOT_RUNNING(130013, "flink session cluster not running", "Flink session 没有正在运行"),
    FLINK_SESSION_CLUSTER_NAME_EXIST_ERROR(140001,"this flink session name exist error.","Flink 版本名称已经存在"),

    UPDATE_FLINK_TASK_FAILED(140002,"update flink task failed","更新task数据失败"),
    FLINK_NAME_EXIST_ERROR(140003,"same flink name [{0}]exist","task name 已经存在"),

    FLINK_TASK_INSTANCE_EXIST_ERROR(150001,"flink task tag had create instance,need delete and recreate","Task tag 已经创建了实例，需要删除后才能重新创建"),
    CREATE_FLINK_TASK_INSTANCE_ERROR(150002,"flink task instance create failed","Flink任务实例创建失败"),
    UPDATE_FLINK_TASK_INSTANCE_ERROR(150003,"flink task instance update failed","Flink任务实例更新失败"),
    FLINK_TASK_INSTANCE_NOT_EXIST_ERROR(150004,"flink task instance not exist","Task instance不存在"),
    FLINK_TASK_INSTANCE_IS_RUNNING(150005,"flink task is running","flink task 正在运行，不能修改"),
    DEPLOY_FLINK_TASK_INSTANCE_ERROR(150006,"deploy flink task instance failed","Flink任务实例部署失败"),
    FLINK_TASK_INSTANCE_RELATION_EXIST(150007,"flink task instance [{0}] relation exists","Flink task 实例[{0}]存在关联"),
    RUN_FLINK_TASK_INSTANCE_ERROR(150007,"run flink task instance failed","Flink任务实例运行失败"),
    STOP_FLINK_TASK_INSTANCE_ERROR(150008,"stop flink task instance failed","Flink任务实例停止失败"),
    FLINK_TASK_RUN_MODE_NOT_SUPPORT_ERROR(150009,"flink task instance run mode not support","Flink 任务实例运行模式目前不支持"),
    FLINK_TASK_NOT_DEPLOY_ERROR(150010,"flink task instance not deployed","Flink 任务实例目前尚未部署"),
    FLINK_TASK_INSTANCE_IS_STOPPED(150011,"flink task is stopped","flink task 已经停止"),
    /**
     * docker image
     */
    DOCKER_IMAGE_CREATE_ERROR(160001, "docker register failed", "docker镜像注册失败"),
    DOCKER_IMAGE_NAME_EXIST_ERROR(160002, "docker image name has exists", "docker镜像已经存在"),
    DOCKER_IMAGE_NOT_EXIST_ERROR(160003, "docker image not exists", "docker镜像不存在"),
    DELETE_DOCKER_IMAGE_ERROR(160004, "docker image delete error", "docker镜像删除失败"),
    DOCKER_IMAGE_TASK_BOUND_ERROR(160005, "docker image bounded by task [{}]", "docker镜像已经被任务[{}]绑定"),
    DOCKER_IMAGE_FLINKSESSION_BOUND_ERROR(160006, "docker image bounded by flink session [{}]", "docker镜像已经被flink session [{}]绑定"),
    NULL_ERROR(20000,"[{0}] must not be null","[{0}] 不能为空");




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
