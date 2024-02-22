package com.handsome.flink.core.context;

import com.handsome.common.params.SqlResult;
import com.handsome.common.enums.StreamType;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

public abstract class AbstractFlinkSqlInterpreter {

    public abstract SqlResult verifySql(List<String> sqls, StreamType streamType);

    public abstract String getStreamGraph(List<String> sqls,StreamType streamType) throws Exception;

    public abstract String runSqlListOnCluster(List<String> sqls,StreamType streamType) throws Exception;

    public abstract Map extractTableConfigOptions();

    public abstract void createEnv(StreamType streamType);

    public abstract void updateEnvConfig(Map<String,String> envConfig,StreamType streamType);

    /**
     * Static native method to parse sql type
     *
     * @param stmt
     * @param sqlCommand
     * @return
     */
    public static Optional<SqlCommandParser.SqlCommandCall> parseByRegexMatching(String stmt, SqlCommandParser.SqlCommand sqlCommand) {
        if (sqlCommand == null) {
            // parse statement via regex matching
            for (SqlCommandParser.SqlCommand cmd : SqlCommandParser.SqlCommand.values()) {
                Optional<SqlCommandParser.SqlCommandCall> sqlCommandCall = regexMatchingExecute(cmd, stmt);
                if (sqlCommandCall.isPresent()) {
                    return sqlCommandCall;
                }
            }
            return Optional.empty();
        }
        return regexMatchingExecute(sqlCommand, stmt);
    }

    public static Optional<SqlCommandParser.SqlCommandCall> regexMatchingExecute(SqlCommandParser.SqlCommand cmd, String stmt) {
        if (cmd.pattern != null) {
            final Matcher matcher = cmd.pattern.matcher(stmt);
            if (matcher.matches()) {
                final String[] groups = new String[matcher.groupCount()];
                for (int i = 0; i < groups.length; i++) {
                    groups[i] = matcher.group(i + 1);
                }
                return cmd.operandConverter.apply(groups)
                        .map((operands) -> {
                            String[] newOperands = operands;
                            if (cmd == SqlCommandParser.SqlCommand.EXPLAIN) {
                                // convert `explain xx` to `explain plan for xx`
                                // which can execute through executeSql method
                                newOperands = new String[]{"EXPLAIN PLAN FOR " + operands[0] + " " + operands[1]};
                            }
                            return new SqlCommandParser.SqlCommandCall(cmd, newOperands, stmt);
                        });
            }
        }
        return Optional.empty();
    }

}