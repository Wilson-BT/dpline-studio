import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class FlatMessageToTable {

    public static void main(String[] args){
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode() // 声明为流任务
                //.inBatchMode() // 声明为批任务
                .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        StreamTableEnvironment tenv = StreamTableEnvironment.create(env, settings);

        setupConfig(env);

        extractedKafka(tenv);

    }

    private static void setupConfig(StreamExecutionEnvironment env) {
// start a checkpoint every 1000 ms
//        env.enableCheckpointing(1000);

// advanced options:

// set mode to exactly-once (this is the default)
//        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

// make sure 500 ms of progress happen between checkpoints
//        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);

//        checkpoints have to complete within one minute, or are discarded
//        env.getCheckpointConfig().setCheckpointTimeout(60000);

// only two consecutive checkpoint failures are tolerated
//        env.getCheckpointConfig().setTolerableCheckpointFailureNumber(2);

// allow only one checkpoint to be in progress at the same time
//        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);

// enable externalized checkpoints which are retained
// after job cancellation
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

// enables the unaligned checkpoints
//        env.getCheckpointConfig().enableUnalignedCheckpoints();

// sets the checkpoint storage where checkpoint snapshots will be written
//        env.getCheckpointConfig().setCheckpointStorage("file:///f:/data/checkpoint/");

// enable checkpointing with finished tasks
//        Configuration config = new Configuration();
//        config.set(ExecutionCheckpointingOptions.ENABLE_CHECKPOINTS_AFTER_TASKS_FINISH, true);
//        env.configure(config);
    }

    private static void extracted1(StreamTableEnvironment tenv) {
        tenv.executeSql("CREATE TABLE tidb_fas_supplier_account( \n" +
                "`id` STRING,\n" +
                "`zone_no` STRING,\n" +
                "`company_no` STRING,\n" +
                "`shop_no` STRING,\n" +
                "`counter_no` STRING,\n" +
                "`supplier_no` STRING,\n" +
                "`su_company_name` STRING,\n" +
                "`bank_name` STRING,\n" +
                "`bank_account` STRING,\n" +
                "`bank_account_name` STRING,\n" +
                "`tax_no` STRING,\n" +
                "`address` STRING,\n" +
                "`account_set_no` STRING,\n" +
                "`account_set_name` STRING,\n" +
                "`old_account_set_no` STRING,\n" +
                "`old_account_set_name` STRING,\n" +
                "`status` INT,\n" +
                "`auditor` STRING,\n" +
                "`audit_time` TIMESTAMP,\n" +
                "`settle_month` STRING,\n" +
                "`remark` STRING,\n" +
                "`create_user` STRING,\n" +
                "`create_time` TIMESTAMP,\n" +
                "`update_time` TIMESTAMP,\n" +
                "PRIMARY KEY (`id`) NOT ENFORCED\n" +
                " ) WITH (\n" +
                "'connector' = 'tidb-cdc',\n" +
                "'database-name' = 'topmall',\n" +
                "'tikv.grpc.timeout_in_ms' = '20000','tikv.batch_get_concurrency' = '1',\n" +
                "'pd-addresses' = '10.9.252.125:2379,10.10.214.118:2379,10.9.252.119:2379',\n" +
                "'table-name' = 'fas_supplier_account')\n");

        tenv.executeSql("CREATE TABLE tsc_supplier_account( \n" +
                "`id` STRING,\n" +
                "`zone_no` STRING,\n" +
                "`company_no` STRING,\n" +
                "`shop_no` STRING,\n" +
                "`counter_no` STRING,\n" +
                "`supplier_no` STRING,\n" +
                "`su_company_name` STRING,\n" +
                "`bank_name` STRING,\n" +
                "`bank_account` STRING,\n" +
                "`bank_account_name` STRING,\n" +
                "`tax_no` STRING,\n" +
                "`address` STRING,\n" +
                "`account_set_no` STRING,\n" +
                "`account_set_name` STRING,\n" +
                "`old_account_set_no` STRING,\n" +
                "`old_account_set_name` STRING,\n" +
                "`status` INT,\n" +
                "`auditor` STRING,\n" +
                "`audit_time` TIMESTAMP,\n" +
                "`settle_month` STRING,\n" +
                "`remark` STRING,\n" +
                "`create_user` STRING,\n" +
                "`create_time` TIMESTAMP,\n" +
                "`update_time` TIMESTAMP,\n" +
                "PRIMARY KEY (`id`) NOT ENFORCED\n" +
                " ) WITH (\n" +
                "'connector' = 'jdbc',\n" +
                "'password' = 'retail_dev',\n" +
                "'url' = 'jdbc:mysql://10.9.252.125:4000/retail_fas',\n" +
                "'username' = 'retail_dev',\n" +
                "'table-name' = 'tsc_supplier_account')\n");

        String script = "INSERT INTO tsc_supplier_account Select * FROM tidb_fas_supplier_account";

        tenv.executeSql(script);

        System.out.println("====>tidb_fas_supplier_account");
    }

    private static void extractedKafka(StreamTableEnvironment tenv) {
        tenv.executeSql("CREATE TABLE mdm_region (\n" +
                "  id STRING,\n" +
                "  name STRING,\n" +
                "  region_no STRING,\n" +
                "  status INT,\n" +
                "  zone_no STRING,\n" +
                "  create_time TIMESTAMP,\n" +
                "  create_user STRING,\n" +
                "  update_time TIMESTAMP,\n" +
                "  update_user STRING,\n" +
                "  remark STRING,\n" +
                "  `topic` STRING METADATA VIRTUAL,\n" +
                "  `partition` BIGINT METADATA VIRTUAL,\n" +
                "  PRIMARY KEY (`id`) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'kafka',\n" +
                "   'topic' = 'retail_ticdc_streamx',\n" +
                "   'properties.bootstrap.servers' = '10.9.251.38:9092,10.9.251.99:9092,10.9.251.100:9092',\n" +
                "   'properties.group.id' = 'topmall-cdc',\n" +
                "   'format' = 'canal-json' , \n" +
                "   'canal-json.database.include' = 'retail_fas',\n" +
                "   'canal-json.table.include' = 'mdm_region', \n" +
                "   'scan.startup.mode' = 'group-offsets'\n" +
                ")");

        tenv.executeSql("select * from mdm_region").print();

//        tenv.executeSql("CREATE TABLE fas_mdm_region (\n" +
//                "  id STRING,\n" +
//                "  name STRING,\n" +
//                "  region_no STRING,\n" +
//                "  status INT,\n" +
//                "  zone_no STRING,\n" +
//                "  create_time TIMESTAMP,\n" +
//                "  create_user STRING,\n" +
//                "  update_time TIMESTAMP,\n" +
//                "  update_user STRING,\n" +
//                "  remark STRING,\n" +
//                "  PRIMARY KEY (`id`) NOT ENFORCED\n" +
//                ") WITH (\n" +
//                "   'connector' = 'jdbc',\n" +
//                "   'url' = 'jdbc:mysql://10.9.251.102:3306/topmall',\n" +
//                "   'table-name' = 'mdm_region',\n" +
//                "   'username' = 'retail_mdm_y',\n" +
//                "   'password' = 'retail_mdm_y'\n" +
//                ")");
//
//        tenv.executeSql("INSERT INTO fas_mdm_region SELECT * FROM mdm_region");
    }


    private static void extracted(StreamTableEnvironment tenv) {
        tenv.executeSql("CREATE TABLE mdm_region (\n" +
                "  id STRING,\n" +
                "  name STRING,\n" +
                "  region_no STRING,\n" +
                "  status INT,\n" +
                "  zone_no STRING,\n" +
                "  create_time TIMESTAMP,\n" +
                "  create_user STRING,\n" +
                "  update_time TIMESTAMP,\n" +
                "  update_user STRING,\n" +
                "  remark STRING,\n" +
                "  PRIMARY KEY (`id`) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'tidb-cdc',\n" +
                "   'tikv.grpc.timeout_in_ms' = '20000',\n" +
                "   'pd-addresses' = '10.9.252.125:2379,10.10.214.118:2379,10.9.252.119:2379',\n" +
                "   'database-name' = 'topmall',\n" +
                "   'table-name' = 'mdm_region'\n" +
                ")");

        tenv.executeSql("CREATE TABLE fas_mdm_region (\n" +
                "  id STRING,\n" +
                "  name STRING,\n" +
                "  region_no STRING,\n" +
                "  status INT,\n" +
                "  zone_no STRING,\n" +
                "  create_time TIMESTAMP,\n" +
                "  create_user STRING,\n" +
                "  update_time TIMESTAMP,\n" +
                "  update_user STRING,\n" +
                "  remark STRING,\n" +
                "  PRIMARY KEY (`id`) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://10.9.251.102:3306/topmall',\n" +
                "   'table-name' = 'mdm_region',\n" +
                "   'username' = 'retail_mdm_y',\n" +
                "   'password' = 'retail_mdm_y'\n" +
                ")");

        tenv.executeSql("INSERT INTO fas_mdm_region SELECT * FROM mdm_region");
    }

}
