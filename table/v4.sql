/*
 Navicat Premium Data Transfer

 Source Server         : dev_mdm_1277
 Source Server Type    : MySQL
 Source Server Version : 50725
 Source Host           : 10.10.219.127:3306
 Source Schema         : dpline

 Target Server Type    : MySQL
 Target Server Version : 50725
 File Encoding         : 65001

 Date: 03/04/2024 18:04:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dpline_access_token
-- ----------------------------
DROP TABLE IF EXISTS `dpline_access_token`;
CREATE TABLE `dpline_access_token` (
                                       `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
                                       `user_id` int(11) DEFAULT NULL COMMENT 'user id',
                                       `token` varchar(64) DEFAULT NULL COMMENT 'token',
                                       `expire_time` datetime DEFAULT NULL COMMENT 'end time of token ',
                                       `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                       `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_alert_instance
-- ----------------------------
DROP TABLE IF EXISTS `dpline_alert_instance`;
CREATE TABLE `dpline_alert_instance` (
                                         `id` bigint(20) NOT NULL COMMENT 'ID',
                                         `instance_name` varchar(200) DEFAULT NULL COMMENT 'alert instance name',
                                         `alert_type` varchar(30) DEFAULT NULL,
                                         `instance_params` text COMMENT 'plugin instance params. Also contain the params value which user input in web ui.',
                                         `enabled_flag` tinyint(2) DEFAULT '1',
                                         `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                         `create_user` varchar(128) DEFAULT NULL,
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         `update_user` varchar(128) DEFAULT NULL,
                                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_cluster
-- ----------------------------
DROP TABLE IF EXISTS `dpline_cluster`;
CREATE TABLE `dpline_cluster` (
                                  `id` bigint(20) NOT NULL COMMENT 'id',
                                  `cluster_name` varchar(100) DEFAULT NULL COMMENT '引擎名称',
                                  `env_type` varchar(16) DEFAULT NULL COMMENT '环境类型，开发环境:dev,测试环境:test,生产环境:prod',
                                  `cluster_type` varchar(16) DEFAULT NULL COMMENT '集群类型,分为kubernetes/yarn',
                                  `cluster_params` text COMMENT '引擎参数',
                                  `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否启用,0:未启用,1:已启用',
                                  `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                  `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'update time',
                                  `update_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='引擎';

-- ----------------------------
-- Table structure for dpline_cluster_user
-- ----------------------------
DROP TABLE IF EXISTS `dpline_cluster_user`;
CREATE TABLE `dpline_cluster_user` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `cluster_id` bigint(20) DEFAULT NULL COMMENT '引擎ID',
                                       `user_code` varchar(100) DEFAULT NULL COMMENT '用户code',
                                       `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否有效',
                                       `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
                                       `update_user` varchar(128) DEFAULT NULL COMMENT '更新人',
                                       `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                       `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                       PRIMARY KEY (`id`),
                                       KEY `idx_user_code` (`user_code`)
) ENGINE=InnoDB AUTO_INCREMENT=250 DEFAULT CHARSET=utf8mb4 COMMENT='引擎用户关系表';

-- ----------------------------
-- Table structure for dpline_data_source
-- ----------------------------
DROP TABLE IF EXISTS `dpline_data_source`;
CREATE TABLE `dpline_data_source` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                      `data_source_name` varchar(255) DEFAULT NULL COMMENT '实例名称',
                                      `data_source_type` varchar(255) DEFAULT NULL COMMENT '数据源类型',
                                      `data_source_flag` varchar(16) DEFAULT NULL COMMENT '类别:public:通用数据源,project:项目数据源',
                                      `project_id` bigint(20) NOT NULL COMMENT '关联项目编号',
                                      `description` text COMMENT '描述',
                                      `connection_params` text COMMENT '数据源配置json格式',
                                      `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否有效',
                                      `env` varchar(10) DEFAULT 'dev',
                                      `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
                                      `update_user` varchar(128) DEFAULT NULL COMMENT '更新人',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                      `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='数据源管理表';

-- ----------------------------
-- Table structure for dpline_docker_image
-- ----------------------------
DROP TABLE IF EXISTS `dpline_docker_image`;
CREATE TABLE `dpline_docker_image` (
                                       `id` bigint(19) NOT NULL COMMENT 'key',
                                       `image_name` varchar(250) DEFAULT NULL COMMENT 'docker image name',
                                       `short_name` varchar(50) DEFAULT NULL COMMENT '简称',
                                       `register_address` varchar(100) NOT NULL DEFAULT '' COMMENT 'flink task tag name',
                                       `register_password` varchar(250) DEFAULT NULL COMMENT 'docker image password',
                                       `register_user` varchar(250) DEFAULT NULL COMMENT 'docker image name',
                                       `motor_type` varchar(20) DEFAULT 'FLINK' COMMENT '镜像类型,flink/spark',
                                       `motor_version_id` bigint(20) DEFAULT NULL COMMENT 'flink/spark 版本',
                                       `enabled_flag` tinyint(4) DEFAULT '1' COMMENT '是否可用',
                                       `description` varchar(255) DEFAULT NULL COMMENT '描述信息',
                                       `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                       `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                       `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                                       `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_file
-- ----------------------------
DROP TABLE IF EXISTS `dpline_file`;
CREATE TABLE `dpline_file` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `file_name` varchar(255) NOT NULL COMMENT '文件名称',
                               `file_type` varchar(255) NOT NULL COMMENT '文件类型',
                               `description` varchar(255) DEFAULT NULL COMMENT '文件描述',
                               `content` longtext,
                               `etl_content` longtext CHARACTER SET utf8 COMMENT '转换语句sql',
                               `meta_table_content` longtext CHARACTER SET utf8 COMMENT '元表sql',
                               `config_content` longtext,
                               `source_content` longtext,
                               `data_stream_content` longtext COMMENT 'ds配置信息',
                               `dag` longtext,
                               `file_status` varchar(255) DEFAULT NULL COMMENT '文件状态',
                               `project_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
                               `folder_id` bigint(20) DEFAULT NULL COMMENT '目录ID',
                               `run_motor_type` varchar(20) DEFAULT 'FLINK' COMMENT 'FLINK or SPARK',
                               `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否可用',
                               `env_type` varchar(10) DEFAULT 'dev',
                               `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
                               `update_user` varchar(128) DEFAULT NULL COMMENT '更新人',
                               `create_time` datetime DEFAULT NULL COMMENT 'create time',
                               `update_time` datetime DEFAULT NULL COMMENT 'update time',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13110517315233 DEFAULT CHARSET=utf8mb4 COMMENT='文件';

-- ----------------------------
-- Table structure for dpline_file_tag
-- ----------------------------
DROP TABLE IF EXISTS `dpline_file_tag`;
CREATE TABLE `dpline_file_tag` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `file_name` varchar(255) DEFAULT NULL,
                                   `file_tag_name` varchar(255) DEFAULT NULL COMMENT 'tag 名称',
                                   `file_content` longtext,
                                   `file_type` varchar(20) DEFAULT NULL COMMENT 'file type ds or sql',
                                   `meta_table_json` longtext CHARACTER SET utf8 COMMENT '元表json',
                                   `etl_content` longtext CHARACTER SET utf8 COMMENT '转换语句sql',
                                   `meta_table_content` longtext CHARACTER SET utf8 COMMENT '元表sql',
                                   `config_content` longtext,
                                   `source_content` longtext,
                                   `data_stream_content` longtext COMMENT 'ds配置信息',
                                   `remark` varchar(1500) DEFAULT NULL COMMENT '备注',
                                   `project_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
                                   `run_motor_type` varchar(20) DEFAULT 'FLINK' COMMENT 'FLINK or SPARK',
                                   `file_id` bigint(20) DEFAULT NULL COMMENT '文件ID',
                                   `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否可用',
                                   `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
                                   `update_user` varchar(128) DEFAULT NULL COMMENT '更新人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`),
                                   KEY `idx_file_id` (`file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12346930979361 DEFAULT CHARSET=utf8mb4 COMMENT='tag 标记';

-- ----------------------------
-- Table structure for dpline_flink_session
-- ----------------------------
DROP TABLE IF EXISTS `dpline_flink_session`;
CREATE TABLE `dpline_flink_session` (
                                        `id` bigint NOT NULL COMMENT 'id',
                                        `flink_session_name` varchar(64) DEFAULT NULL COMMENT 'flink session name',
                                        `description` varchar(255) DEFAULT NULL,
                                        `user_id` int(11) DEFAULT NULL COMMENT 'user id',
                                        `k8s_namespace_id` bigint(22) NOT NULL DEFAULT '0' COMMENT 'k8s namespace id',
                                        `taskmanager_num` int(6) DEFAULT NULL COMMENT 'taskmanager_num',
                                        `taskmanager_mem_size` int(11) DEFAULT NULL COMMENT 'taskmanager_mem_size',
                                        `taskmanager_cpu_num` int(6) DEFAULT NULL COMMENT 'taskmanager_cpu_num',
                                        `taskmanager_slot_num` int(6) DEFAULT NULL COMMENT 'taskmanager_slot_num',
                                        `jobmanager_process_size` int(6) DEFAULT NULL COMMENT 'jobmanager_process_size',
                                        `status` tinyint(2) NOT NULL DEFAULT '2' COMMENT 'status:2.unuseful,1.useful,0:failed',
                                        `restart_options` varchar(12) DEFAULT NULL COMMENT 'restart_options',
                                        `checkpoint_options` varchar(12) DEFAULT NULL COMMENT 'checkpoint_options',
                                        `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                        `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                        `application_id` varchar(200) DEFAULT NULL COMMENT 'k8s:cluster_id,yarn:application_id',
                                        `cluster_id` bigint NOT NULL COMMENT 'session dependence clusterId',
                                        `resource_ids` varchar(100) DEFAULT NULL COMMENT 'flink session dependence resources',
                                        `udf_ids` varchar(100) DEFAULT NULL COMMENT 'session dependence udfIds',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_flink_tag_task_res_relation
-- ----------------------------
DROP TABLE IF EXISTS `dpline_flink_tag_task_res_relation`;
CREATE TABLE `dpline_flink_tag_task_res_relation` (
                                                      `id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'id',
                                                      `resource_id` int(11) DEFAULT NULL COMMENT 'resource id',
                                                      `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                                      `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                                      `draft_tag_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:task tag;1:task draft',
                                                      UNIQUE KEY `unique_index` (`id`,`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_flink_tag_task_udf_relation
-- ----------------------------
DROP TABLE IF EXISTS `dpline_flink_tag_task_udf_relation`;
CREATE TABLE `dpline_flink_tag_task_udf_relation` (
                                                      `id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'id',
                                                      `udf_id` int(11) DEFAULT NULL COMMENT 'resource id',
                                                      `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                                      `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                                      `draft_tag_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:task tag;1:task draft',
                                                      UNIQUE KEY `unique_index` (`id`,`udf_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_flink_version
-- ----------------------------
DROP TABLE IF EXISTS `dpline_flink_version`;
CREATE TABLE `dpline_flink_version` (
                                        `id` bigint(19) NOT NULL COMMENT 'key',
                                        `flink_name` varchar(64) DEFAULT NULL COMMENT 'flink name',
                                        `description` varchar(255) DEFAULT NULL,
                                        `flink_path` varchar(255) DEFAULT NULL COMMENT 'flink-client home path',
                                        `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否可用',
                                        `real_version` varchar(20) DEFAULT NULL COMMENT 'real version',
                                        `motor_type` varchar(20) DEFAULT 'FLINK' COMMENT 'FLINK or SPARK',
                                        `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                        `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                                        `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                        `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `dpline_flink_version` (`flink_name`,`flink_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_folder
-- ----------------------------
DROP TABLE IF EXISTS `dpline_folder`;
CREATE TABLE `dpline_folder` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                 `folder_name` varchar(255) NOT NULL COMMENT '目录名称',
                                 `parent_id` bigint(20) DEFAULT NULL COMMENT '父目录ID',
                                 `project_id` bigint(20) NOT NULL COMMENT '项目ID',
                                 `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否可用',
                                 `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
                                 `update_user` varchar(128) DEFAULT NULL COMMENT '更新人',
                                 `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                 `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12058673049249 DEFAULT CHARSET=utf8mb4 COMMENT='目录';

-- ----------------------------
-- Table structure for dpline_jar_file_dtl
-- ----------------------------
DROP TABLE IF EXISTS `dpline_jar_file_dtl`;
CREATE TABLE `dpline_jar_file_dtl` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `main_resource_id` bigint(20) DEFAULT NULL COMMENT '主资源id',
                                       `jar_name` varchar(100) DEFAULT NULL COMMENT 'jar包名称',
                                       `jar_path` varchar(255) DEFAULT NULL COMMENT 'jar包路径',
                                       `file_md5` varchar(128) DEFAULT NULL COMMENT 'jar包指纹',
                                       `description` longtext,
                                       `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否可用',
                                       `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
                                       `update_user` varchar(128) DEFAULT NULL COMMENT '更新人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                       `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                       `motor_version_id` bigint(19) DEFAULT NULL COMMENT '计算引擎版本Id',
                                       PRIMARY KEY (`id`),
                                       KEY `idx_jar_name` (`jar_name`)
) ENGINE=InnoDB AUTO_INCREMENT=13110506254753 DEFAULT CHARSET=utf8mb4 COMMENT='jar包资源管理';

-- ----------------------------
-- Table structure for dpline_job
-- ----------------------------
DROP TABLE IF EXISTS `dpline_job`;
CREATE TABLE `dpline_job` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `job_name` varchar(255) NOT NULL COMMENT '作业名称',
                              `job_content` longtext,
                              `run_mode_type` varchar(50) DEFAULT NULL COMMENT 'run mode type',
                              `file_type` varchar(20) DEFAULT NULL COMMENT 'file type ds or sql',
                              `cluster_id` bigint(11) DEFAULT NULL COMMENT 'cluster id',
                              `application_id` varchar(80) DEFAULT NULL COMMENT 'yarn:application id,k8s: clusterId(jobName) or sessionId(sessionName)',
                              `image_id` bigint(11) DEFAULT NULL COMMENT 'image id',
                              `runtime_options` text COMMENT 'run time options',
                              `other_runtime_config` text COMMENT 'other run time config',
                              `motor_version_id` bigint(19) DEFAULT NULL COMMENT 'motor_version  id',
                              `main_resource_id` bigint(20) DEFAULT NULL COMMENT '资源包Id',
                              `main_class` varchar(100) DEFAULT NULL COMMENT 'main class',
                              `app_args` longtext,
                              `source_content` longtext,
                              `project_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
                              `run_motor_type` varchar(20) DEFAULT 'FLINK' COMMENT 'FLINK or SPARK',
                              `file_id` bigint(20) DEFAULT NULL COMMENT '文件ID',
                              `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否有效',
                              `env` varchar(10) DEFAULT 'dev',
                              `run_job_id` varchar(255) DEFAULT NULL COMMENT '运行的任务 id',
                              `rest_url` varchar(255) DEFAULT NULL COMMENT 'task rest url 地址',
                              `exec_status` varchar(255) DEFAULT NULL COMMENT '作业状态',
                              `deployed` tinyint(2) DEFAULT '0' COMMENT 'is deployed',
                              `alert_type` varchar(40) DEFAULT 'NONE' COMMENT 'alert mode',
                              `alert_mode` varchar(40) DEFAULT 'NONE' COMMENT 'alert mode',
                              `alert_instance_id` bigint(19) DEFAULT NULL COMMENT 'alert instance id',
                              `main_jar_id` bigint(20) DEFAULT NULL COMMENT '当前任务使用的main_jar_id',
                              `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
                              `update_user` varchar(128) DEFAULT NULL COMMENT '更新人',
                              `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `file_tag_id` bigint(20) DEFAULT NULL COMMENT '文件tag ID',
                              PRIMARY KEY (`id`),
                              KEY `index_project_id` (`project_id`),
                              KEY `index_file_id` (`file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12346930981153 DEFAULT CHARSET=utf8mb4 COMMENT='作业';

-- ----------------------------
-- Table structure for dpline_job_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `dpline_job_operate_log`;
CREATE TABLE `dpline_job_operate_log` (
                                          `id` bigint(20) NOT NULL,
                                          `job_id` bigint(20) NOT NULL,
                                          `operate_type` varchar(20) DEFAULT NULL,
                                          `enabled_flag` tinyint(2) DEFAULT NULL,
                                          `operate_log_path` varchar(255) DEFAULT NULL,
                                          `trace_id` varchar(50) DEFAULT NULL COMMENT '日志追踪Id',
                                          `create_user` varchar(128) DEFAULT NULL,
                                          `update_user` varchar(128) DEFAULT NULL,
                                          `create_time` datetime DEFAULT NULL,
                                          `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          `operate_timestamp` bigint(13) NOT NULL,
                                          PRIMARY KEY (`id`),
                                          KEY `index_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for dpline_job_savepoint
-- ----------------------------
DROP TABLE IF EXISTS `dpline_job_savepoint`;
CREATE TABLE `dpline_job_savepoint` (
                                        `id` bigint(20) NOT NULL COMMENT 'save point id',
                                        `savepoint_name` varchar(100) DEFAULT NULL COMMENT '检查点名称',
                                        `job_id` bigint(20) DEFAULT NULL COMMENT 'task instance id',
                                        `savepoint_path` varchar(255) NOT NULL COMMENT 'flink task save poinit address',
                                        `enabled_flag` tinyint(2) DEFAULT '1' COMMENT '1 is avaliable,0 not',
                                        `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                        `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                        `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                                        `update_user` varchar(100) DEFAULT NULL COMMENT '更新人',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_main_resource
-- ----------------------------
DROP TABLE IF EXISTS `dpline_main_resource`;
CREATE TABLE `dpline_main_resource` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                        `name` varchar(100) DEFAULT NULL COMMENT '名称',
                                        `jar_function_type` varchar(20) DEFAULT NULL COMMENT 'jar包用途，MAIN，UDF, CONNECTOR, EXTENDED',
                                        `run_motor_type` varchar(20) DEFAULT NULL COMMENT '使用的计算引擎类型，flink or spark',
                                        `jar_auth_type` varchar(20) DEFAULT NULL COMMENT 'jar的适用范围 public or project',
                                        `project_id` bigint(20) DEFAULT NULL COMMENT '项目ID',
                                        `description` longtext,
                                        `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否可用',
                                        `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
                                        `update_user` varchar(128) DEFAULT NULL COMMENT '更新人',
                                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (`id`),
                                        KEY `idx_jar_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=13103834300321 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for dpline_project
-- ----------------------------
DROP TABLE IF EXISTS `dpline_project`;
CREATE TABLE `dpline_project` (
                                  `id` bigint(20) NOT NULL COMMENT 'key',
                                  `project_name` varchar(100) DEFAULT NULL COMMENT 'project name',
                                  `project_code` bigint(20) DEFAULT NULL COMMENT 'project code ：过期',
                                  `description` varchar(200) DEFAULT NULL,
                                  `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否正常，正常为1，失效为0',
                                  `allow_job_add_edit` tinyint(3) DEFAULT '0' COMMENT '允许生产环境新增作业与编辑作业代码 1:允许，0不允许',
                                  `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                  `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                                  `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                  `update_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_project_user
-- ----------------------------
DROP TABLE IF EXISTS `dpline_project_user`;
CREATE TABLE `dpline_project_user` (
                                       `id` bigint(19) NOT NULL COMMENT 'key',
                                       `user_code` varchar(64) NOT NULL COMMENT 'user code',
                                       `project_id` bigint(19) NOT NULL COMMENT 'project_id',
                                       `user_role` tinyint(4) DEFAULT NULL COMMENT '项目管理员人2,项目负责人1,普通用户0',
                                       `enabled_flag` tinyint(3) DEFAULT '1' COMMENT '正常为1，失效为0',
                                       `create_time` datetime DEFAULT NULL COMMENT 'create time',
                                       `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                                       `update_time` datetime DEFAULT NULL COMMENT 'update time',
                                       `update_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                                       PRIMARY KEY (`id`),
                                       KEY `user_id_index` (`user_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_session
-- ----------------------------
DROP TABLE IF EXISTS `dpline_session`;
CREATE TABLE `dpline_session` (
                                  `id` varchar(64) NOT NULL COMMENT 'key',
                                  `user_id` bigint(19) DEFAULT NULL COMMENT 'user id',
                                  `ip` varchar(45) DEFAULT NULL COMMENT 'ip',
                                  `last_login_time` datetime DEFAULT NULL COMMENT 'last login time',
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `dpline_sys_config`;
CREATE TABLE `dpline_sys_config` (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `config_key` varchar(255) DEFAULT NULL COMMENT '配置类型key',
                                     `config_value` text COMMENT '配置value',
                                     `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否可用',
                                     `create_user` varchar(128) DEFAULT NULL COMMENT '创建人',
                                     `update_user` varchar(128) DEFAULT NULL COMMENT '更新人',
                                     `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1658789240017854467 DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ----------------------------
-- Table structure for dpline_udfs
-- ----------------------------
DROP TABLE IF EXISTS `dpline_udfs`;
CREATE TABLE `dpline_udfs` (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
                               `user_id` int(11) NOT NULL COMMENT 'user id',
                               `func_name` varchar(100) NOT NULL COMMENT 'UDF function name',
                               `class_name` varchar(255) NOT NULL COMMENT 'class of udf',
                               `arg_types` varchar(255) DEFAULT NULL COMMENT 'arguments types',
                               `database` varchar(255) DEFAULT NULL COMMENT 'data base',
                               `description` varchar(255) DEFAULT NULL,
                               `resource_id` int(11) NOT NULL COMMENT 'resource id',
                               `resource_name` varchar(255) NOT NULL COMMENT 'resource name',
                               `create_time` datetime NOT NULL COMMENT 'create time',
                               `update_time` datetime NOT NULL COMMENT 'update time',
                               `flink_version_id` int(11) DEFAULT NULL COMMENT 'flink version id',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dpline_user
-- ----------------------------
DROP TABLE IF EXISTS `dpline_user`;
CREATE TABLE `dpline_user` (
                               `id` bigint(19) NOT NULL COMMENT 'user id',
                               `user_code` varchar(100) DEFAULT NULL COMMENT '员工编码',
                               `user_name` varchar(64) DEFAULT NULL COMMENT 'user name',
                               `password` varchar(64) DEFAULT NULL COMMENT 'user password',
                               `is_admin` tinyint(4) DEFAULT NULL COMMENT 'user type, 1:administrator，0:ordinary user',
                               `email` varchar(64) DEFAULT NULL COMMENT 'email',
                               `phone` varchar(11) DEFAULT NULL COMMENT 'phone',
                               `enabled_flag` tinyint(4) DEFAULT NULL COMMENT '是否正常，正常为1，失效为0',
                               `project_id` int(11) DEFAULT NULL COMMENT 'project id',
                               `create_time` datetime DEFAULT NULL COMMENT 'create time',
                               `create_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                               `update_time` datetime DEFAULT NULL COMMENT 'update time',
                               `update_user` varchar(100) DEFAULT NULL COMMENT '创建人',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `user_code` (`user_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into dpline_user (id,user_code,user_name,password,is_admin,email,phone,enabled_flag) values('100000','100000','admin','e10adc3949ba59abbe56e057f20f883e',1,'xxx@qq.com','123456',1);
-- ----------------------------
-- Table structure for sync_table_topic
-- ----------------------------
DROP TABLE IF EXISTS `sync_table_topic`;
CREATE TABLE `sync_table_topic` (
                                    `id` int(11) NOT NULL AUTO_INCREMENT,
                                    `sync_database` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL,
                                    `sync_table` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL,
                                    `is_effective` int(11) NOT NULL DEFAULT '0',
                                    `split_topic` tinyint(3) DEFAULT '0',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

SET FOREIGN_KEY_CHECKS = 1;
