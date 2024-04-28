package com.dpline.flink;

import com.dpline.flink.core.util.SqlSplitter;
import com.dpline.common.enums.FlinkVersion;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class SqlMainClass {

    private final static String ARGS_F = "f";

    private final static Logger logger = LoggerFactory.getLogger(SqlMainClass.class);

    public static void main(String[] args) throws Exception {
        FlinkVersion flinkVersion = new FlinkVersion();
        flinkVersion.setMajorVersion(1);
        flinkVersion.setMinorVersion(14);
        Options options = getOptions();
        DefaultParser defaultParser = new DefaultParser();
        CommandLine parse = defaultParser.parse(options, args);
        String fileNamePath = parse.getOptionValue(ARGS_F);
        logger.info("Sql path was [{}]",fileNamePath);
//        FlinkSqlInterpreter flinkSqlInterpreter = new FlinkSqlInterpreter();
//        StreamTableEnvironmentImpl tableEnv = (StreamTableEnvironmentImpl) flinkSqlInterpreter.getTableEnv(StreamType.STREAM);
//        // 两分钟做一次 checkpoint
//        tableEnv.execEnv().enableCheckpointing(120000L);
//
//        // 此处不再进行 checkpoint 设置，所有环境设置都统一由外部控制
//        flinkSqlInterpreter.runSqlListOnCluster(readAndFormatFromFile(fileNamePath),StreamType.STREAM);
    }

    private static Options getOptions() {
        Options options = new Options();
        Option f = Option.builder()
                .longOpt(ARGS_F)
                .argName("filename")
                .hasArg(true)
                .required(true)
                .desc("file real path like '/opt/flink/main-file/xxx.sql'").build();
        options.addOption(f);
        return options;
    }

    /**
     *  read file from text file
     *
     * @param fileRealPath
     * @return
     * @throws IOException
     */
    private static List<String> readAndFormatFromFile(String fileRealPath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileRealPath));
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        while((str =bufferedReader.readLine()) != null){
            stringBuilder.append(str);
        }
        String sqlText = stringBuilder.toString();
        return SqlSplitter.splitSql(sqlText);
    }

}

