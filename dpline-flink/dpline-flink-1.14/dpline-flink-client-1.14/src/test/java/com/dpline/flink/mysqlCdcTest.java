//package com.handsome.flink;
//
//import com.alibaba.fastjson2.JSON,
//        JSONObject}
//import com.ververica.cdc.connectors.mysql.source.MySqlSource
//import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema
//import org.apache.flink.api.common.eventtime.WatermarkStrategy
//import org.apache.flink.api.common.functions.FlatMapFunction
//import org.apache.flink.streaming.api.CheckpointingMode
//import org.apache.flink.streaming.api.datastream.{DataStreamSource, SingleOutputStreamOperator}
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
//import org.apache.flink.table.data.{GenericRowData, RowData, StringData, TimestampData}
//import org.apache.flink.types.RowKind
//import org.apache.flink.util.Collector
//import org.apache.hadoop.conf.Configuration
//import org.apache.iceberg.catalog.{Namespace, TableIdentifier}
//import org.apache.iceberg.flink.sink.FlinkSink
//import org.apache.iceberg.flink.{CatalogLoader, TableLoader}
//import org.apache.iceberg.hive.HiveCatalog
//import org.apache.iceberg.types.Types
//import org.apache.iceberg.{PartitionSpec, Schema, Table}
//
//public class MysqlCDC2IcebergDataStream {
//
//
//        // =========获取Mysql CDC的流数据==========
//        def getMysqlCDCSource(senv: StreamExecutionEnvironment): DataStreamSource[String] = {
//        val mySqlSource: MySqlSource[String] = MySqlSource.builder()
//        .hostname("192.168.8.124")
//        .port(3306)
//        .databaseList("d_general")
//        .tableList("d_general.org_main")
//        .username("hnmqet")
//        .password("hnmq123456")
//        // 将SourceRecord转换成Json格式
//        .deserializer(new JsonDebeziumDeserializationSchema())
//        .build()
//
//        val watermarkStrategy: WatermarkStrategy[String] = WatermarkStrategy.noWatermarks()
//        val sourceDataStream: DataStreamSource[String] = senv.fromSource[String](mySqlSource, watermarkStrategy, "Mysql Source")
//
//        sourceDataStream
//        }
//
//        // ==========将String类型的Mysql binlog数据，根据type的不同，转换成RowData数据========
//        def transformSource2RowDataStream(sourceDataStream: DataStreamSource[String]): SingleOutputStreamOperator[RowData] = {
//        // 可能有的rowData为null，所以使用flatMap
//        val rowDataSteam: SingleOutputStreamOperator[RowData] = sourceDataStream.flatMap(new FlatMapFunction[String, RowData] {
//        override def flatMap(input: String, collector: Collector[RowData]): Unit = {
//
//        val binlogJsonObject: JSONObject = JSON.parseObject(input)
//        // 获取DML操作类型
//        val mysqlDMLType = binlogJsonObject.getString("op")
//
//        // 根据DML的类型，确定读取before还是after
//        var beforeOrAfter: String = null
//        var rowKindValue: String = null
//        // 如果是读取snapshot、insert和更新，读取after部分
//        if (mysqlDMLType == "r" || mysqlDMLType == "c") {
//        beforeOrAfter = "after"
//        rowKindValue = "INSERT"
//        } else if (mysqlDMLType == "u") {
//        beforeOrAfter = "after"
//        rowKindValue = "UPDATE_AFTER"
//        } else if (mysqlDMLType == "d") {
//        // 如果是删除，读取before部分
//        beforeOrAfter = "before"
//        rowKindValue = "DELETE"
//        }
//
//        // 如果beforeOrAfter不为空，则读取需要的部分
//        if (beforeOrAfter != null) {
//        val afterOrAfterJsonObject = binlogJsonObject.getJSONObject(beforeOrAfter)
//
//        val id = afterOrAfterJsonObject.getLong("id")
//        val org_name = afterOrAfterJsonObject.getString("org_name")
//        val create_time = afterOrAfterJsonObject.getLong("create_time")
//
//        val rowData: GenericRowData = new GenericRowData(3)
//        // 指定数据的导入类型
//        rowData.setRowKind(RowKind.valueOf(rowKindValue))
//        rowData.setField(0, id)
//        rowData.setField(1, StringData.fromString(org_name))
//        rowData.setField(2, TimestampData.fromEpochMillis(create_time))
//
//        collector.collect(rowData)
//
//        }
//        }
//        })
//
//        rowDataSteam
//
//        }
//
//        // ==========初始化Hive Catalog============
//        def initialHiveCatalog(): (HiveCatalog, java.util.HashMap[String, String]) = {
//        val hiveCatalog: HiveCatalog = new HiveCatalog()
//        hiveCatalog.setConf(new Configuration())
//
//        val hiveCatalogProperties: java.util.HashMap[String, String] =
//        new java.util.HashMap[String, String]()
//        hiveCatalogProperties.put("warehouse", "hdfs://nnha/user/hive/warehouse")
//        hiveCatalogProperties.put("uri", "thrift://bigdata003:9083")
//        hiveCatalogProperties.put("clients", "2") // 客户端连接池大小
//        hiveCatalogProperties.put("property-version", "1")
//
//        hiveCatalog.initialize("hive_catalog", hiveCatalogProperties) // 第一个参数为catalog名称
//
//        (hiveCatalog, hiveCatalogProperties)
//        }
//
//        // ============================创建数据库和表============================
//        def createHiveDbTable(hiveCatalog: HiveCatalog): (Table, TableIdentifier) = {
//        val tableSchema: Schema = new Schema(
//        // 通过Java API生成的Schema，需要给每个字段指定唯一ID
//        Types.NestedField.required(1, "id", Types.LongType.get()),
//        Types.NestedField.required(2, "org_name", Types.StringType.get()),
//        Types.NestedField.optional(3, "create_time", Types.TimestampType.withoutZone())
//
//        )
//
//        hiveCatalog.createNamespace(Namespace.of("d_general"))
//
//        val partitionSpec: PartitionSpec = PartitionSpec.unpartitioned()
//
//        // 参数分别是数据库名和表名
//        val tableProperties: java.util.Map[String, String] = new java.util.HashMap[String, String]()
//        tableProperties.put("format-version", "2")
//        val hiveCatalogTableName: TableIdentifier = TableIdentifier.of("d_general", "org_main")
//        val hiveCatalogTable = hiveCatalog.createTable(hiveCatalogTableName, tableSchema, partitionSpec, tableProperties)
//
//        (hiveCatalogTable, hiveCatalogTableName)
//        }
//
//        def main(args: Array[String]): Unit = {
//
//        val senv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment()
//        senv.enableCheckpointing(10000L, CheckpointingMode.EXACTLY_ONCE)
//        senv.setParallelism(1)
//
//        val sourceDataStream: DataStreamSource[String] = getMysqlCDCSource(senv)
//        val rowDataSteam: SingleOutputStreamOperator[RowData] = transformSource2RowDataStream(sourceDataStream)
//
//        val (hiveCatalog, hiveCatalogProperties): (HiveCatalog, java.util.HashMap[String, String])
//        = initialHiveCatalog()
//        val (hiveCatalogTable, hiveCatalogTableName): (Table, TableIdentifier) = createHiveDbTable(hiveCatalog)
//
//        // ================upsert方式插入数据到表===========
//        val IcebergTableLoader: TableLoader = TableLoader.fromCatalog(CatalogLoader.hive("hive_catalog", new Configuration(), hiveCatalogProperties), hiveCatalogTableName)
//        FlinkSink.forRowData(rowDataSteam)
//        .table(hiveCatalogTable)
//        .tableLoader(IcebergTableLoader)
//        .equalityFieldColumns(ArrayBuffer("id").asJava)
//        // upsert需要指定equality field columns。id不存在则插入，id存在则更新
//        .upsert(true)
//        // overwrite不能同时和upsert使用。每一次commit会进行partition级别的覆盖
//        .overwrite(false)
//        .append()
//
//
//        senv.execute("MysqlCDC2IcebergDataStream");
//
//
//        }
//
//}