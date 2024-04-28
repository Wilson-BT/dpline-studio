CREATE TABLE `dpline_access_token` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
`user_id` int(11) DEFAULT NULL COMMENT 'user id',
`token` varchar(64) DEFAULT NULL COMMENT 'token',
`expire_time` datetime DEFAULT NULL COMMENT 'end time of token ',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dpline_alert_instance` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`alter_type` tinyint(11) NOT NULL COMMENT 'alter type',
`instance_params` text COMMENT 'plugin instance params. Also contain the params value which user input in web ui.',
`create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
`update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
`instance_name` varchar(200) DEFAULT NULL COMMENT 'alert instance name',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `dpline_docker_image` (
`id` int(11) NOT NULL COMMENT 'flink task tag id',
`image_name` varchar(50) DEFAULT NULL COMMENT 'docker image name',
`register_address` varchar(100) NOT NULL DEFAULT '' COMMENT 'flink task tag name',
`register_password` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'flink task run mode',
`register_user` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'is open chain,default: not open',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
`alias_name` varchar(30) DEFAULT NULL COMMENT 'docker image alias name',
PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dpline_flink_run_task_instance` (
`id` bigint(20) NOT NULL COMMENT 'flink task instance id',
`flink_task_instance_name` varchar(100) NOT NULL DEFAULT '' COMMENT 'flink task instance name',
`run_mode` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'flink task run mode',
`open_chain` tinyint(2) NOT NULL DEFAULT '1' COMMENT '0 is not open chain,default: open',
`resource_options` varchar(100) DEFAULT NULL COMMENT 'resource option like mem',
`resolve_order` tinyint(2) DEFAULT '0' COMMENT '0:parent first;1:child first',
`checkpoint_options` varchar(255) DEFAULT NULL COMMENT 'checkpoint options',
`deploy_address` varchar(255) DEFAULT NULL COMMENT 'deploy address',
`deployed` tinyint(2) DEFAULT NULL COMMENT 'is deployed',
`k8s_namespace_Id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'k8s namespace id',
`k8s_session_cluster_id` int(11) DEFAULT NULL COMMENT 'k8s session cluster id',
`exposed_type` tinyint(2) DEFAULT NULL COMMENT 'NodePort or clusterIp or rebalance',
`flink_image_id` int(11) DEFAULT NULL COMMENT 'flink image id',
`alert_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Alarm type: 0 is not sent,1 process is sent failed',
`alert_instance_id` int(11) DEFAULT NULL COMMENT 'alert instance id',
`restart_num` tinyint(2) NOT NULL DEFAULT '1' COMMENT 'restart num',
`checkpoint_address` varchar(255) NOT NULL DEFAULT '0' COMMENT 'checkpoint address',
`exec_status` tinyint(2) DEFAULT NULL COMMENT 'exec status',
`restart_options` varchar(100) DEFAULT NULL COMMENT 'restart options',
`flink_task_tag_id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'flink task tag id',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
`project_code` bigint(20) NOT NULL DEFAULT '0' COMMENT 'project_code',
`user_id` int(11) NOT NULL DEFAULT '0' COMMENT 'task instance create user',
`rest_url` varchar(100) DEFAULT NULL COMMENT 'task rest url',
PRIMARY KEY (`id`) USING BTREE,
KEY `flink_task_tag_index` (`flink_task_tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into dpline_flink_run_task_instance (`id`,`flink_task_instance_name`,`run_mode`,`deployed`,`k8s_namespace_Id`,`exposed_type`,`alert_type`,`alert_instance_id`,`exec_status`,`rest_url`)
values('10000000000','flink-sync-database-retail-gms','1','1','5459591802016','0','3','1','2','http://10.250.135.111:31856')

CREATE TABLE `dpline_flink_session` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
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
`k8s_cluster_id` varchar(200) DEFAULT NULL COMMENT 'kubernetes_cluster_id',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dpline_flink_tag_task_res_relation` (
`id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'id',
`resource_id` int(11) DEFAULT NULL COMMENT 'resource id',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
`draft_tag_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:task tag;1:task draft',
UNIQUE KEY `unique_index` (`id`,`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dpline_flink_tag_task_udf_relation` (
`id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'id',
`udf_id` int(11) DEFAULT NULL COMMENT 'resource id',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
`draft_tag_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0:task tag;1:task draft',
UNIQUE KEY `unique_index` (`id`,`udf_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dpline_flink_task_definition` (
`id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'id',
`task_name` varchar(100) NOT NULL DEFAULT '' COMMENT 'task name',
`description` varchar(200) DEFAULT NULL,
`flink_version_id` int(11) NOT NULL DEFAULT '0' COMMENT 'flink version id',
`project_code` bigint(20) DEFAULT NULL COMMENT 'project code',
`task_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'task type ',
`sql_text` text,
`main_jar_path` varchar(50) DEFAULT NULL COMMENT 'main jar path',
`main_class_name` varchar(50) DEFAULT NULL COMMENT 'main jar class name',
`class_params` varchar(255) DEFAULT NULL COMMENT 'jar class main mtds args',
`resource_ids` varchar(100) DEFAULT NULL COMMENT 'task dependence resources',
`udf_ids` varchar(100) DEFAULT NULL COMMENT 'task dependence udfIds',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dpline_flink_task_tag_log` (
`id` bigint(20) NOT NULL DEFAULT '0' COMMENT 'id',
`task_definition_id` bigint(20) DEFAULT NULL COMMENT 'task draft id',
`tag_name` varchar(100) NOT NULL DEFAULT '' COMMENT 'tag name',
`description` varchar(200) DEFAULT NULL,
`flink_version_id` int(11) NOT NULL DEFAULT '0' COMMENT 'flink version id',
`project_code` bigint(20) DEFAULT NULL COMMENT 'project code',
`task_type` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'task type ',
`sql_text` text,
`main_jar_path` varchar(50) DEFAULT NULL COMMENT 'main jar path',
`main_class_name` varchar(50) DEFAULT NULL COMMENT 'main jar class name',
`class_params` varchar(255) DEFAULT NULL COMMENT 'jar class main mtds args',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `dpline_flink_version` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
`flink_name` varchar(64) DEFAULT NULL COMMENT 'flink name',
`description` varchar(255) DEFAULT NULL,
`flink_path` varchar(255) DEFAULT NULL COMMENT 'flink-client home path',
`online` tinyint(2) DEFAULT NULL,
`real_version` varchar(20) DEFAULT NULL COMMENT 'real version',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`),
UNIQUE KEY `dpline_flink_version` (`flink_name`,`flink_path`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
CREATE TABLE `dpline_k8s_namespace` (
`id` bigint(20) NOT NULL COMMENT 'id',
`name_space` varchar(64) DEFAULT NULL COMMENT 'k8s name_space',
`description` varchar(255) DEFAULT NULL,
`kube_path` varchar(255) DEFAULT NULL COMMENT 'flink-client home path',
`selector_lables` varchar(255) DEFAULT NULL,
`service_account` varchar(255) DEFAULT NULL,
`release_state` tinyint(2) DEFAULT '0' COMMENT 'is online',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`),
UNIQUE KEY `dpline_k8s_namespace` (`name_space`,`kube_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `dpline_project` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
`name` varchar(100) DEFAULT NULL COMMENT 'project name',
`code` bigint(20) NOT NULL COMMENT 'encoding',
`description` varchar(200) DEFAULT NULL,
`user_id` int(11) DEFAULT NULL COMMENT 'creator id',
`flag` tinyint(4) DEFAULT '1' COMMENT '0 not available, 1 available',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`),
KEY `user_id_index` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
CREATE TABLE `dpline_relation_flink_version_user` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
`user_id` int(11) DEFAULT NULL COMMENT 'user id',
`flink_version_id` int(11) DEFAULT NULL COMMENT 'flink version id',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `dpline_relation_k8s_namespace_user` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
`user_id` int(11) DEFAULT NULL COMMENT 'user id',
`k8s_namespace_id` int(11) DEFAULT NULL COMMENT 'k8s namespace id',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `dpline_relation_project_user` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
`user_id` int(11) NOT NULL COMMENT 'user id',
`project_id` int(11) DEFAULT NULL COMMENT 'project id',
`perm` int(11) DEFAULT '1' COMMENT 'limits of authority',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`),
KEY `user_id_index` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `dpline_relation_resources_user` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`user_id` int(11) NOT NULL COMMENT 'user id',
`resources_id` int(11) DEFAULT NULL COMMENT 'resource id',
`perm` int(11) DEFAULT '1' COMMENT 'limits of authority',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `dpline_session` (
`id` varchar(64) NOT NULL COMMENT 'key',
`user_id` int(11) DEFAULT NULL COMMENT 'user id',
`ip` varchar(45) DEFAULT NULL COMMENT 'ip',
`last_login_time` datetime DEFAULT NULL COMMENT 'last login time',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `dpline_task_savepoint` (
`id` bigint(20) NOT NULL COMMENT 'savepoint id',
`task_instance_id` bigint(20) NOT NULL COMMENT 'flink task instance id',
`save_point_address` varchar(100) NOT NULL COMMENT 'flink task save poinit address',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
CREATE TABLE `dpline_user` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'user id',
`user_name` varchar(64) DEFAULT NULL COMMENT 'user name',
`user_password` varchar(64) DEFAULT NULL COMMENT 'user password',
`user_type` tinyint(4) DEFAULT NULL COMMENT 'user type, 0:administrator，1:ordinary user',
`email` varchar(64) DEFAULT NULL COMMENT 'email',
`phone` varchar(11) DEFAULT NULL COMMENT 'phone',
`create_time` datetime DEFAULT NULL COMMENT 'create time',
`update_time` datetime DEFAULT NULL COMMENT 'update time',
`state` tinyint(4) DEFAULT '1' COMMENT 'state 0:disable 1:enable',
PRIMARY KEY (`id`),
UNIQUE KEY `user_name_unique` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;