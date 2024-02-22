package com.dpline.flink.explain;

import com.dpline.common.Constants;
import com.dpline.common.enums.ResponseStatus;
import com.dpline.common.request.FlinkDagRequest;
import com.dpline.common.request.FlinkDagResponse;
import com.dpline.common.request.FlinkRequest;
import com.dpline.common.util.CollectionUtils;
import com.dpline.common.util.StringUtils;
import com.dpline.common.util.TaskPathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * jar 包模式，加载 jar 包，解析 jar 包
 */
public class FlinkJarInterpreter {

    private Logger logger = LoggerFactory.getLogger(FlinkJarInterpreter.class);

    public FlinkDagResponse explain(FlinkRequest flinkRequest) throws Exception {
        FlinkDagResponse.Builder builder = FlinkDagResponse.builder();
        FlinkDagRequest flinkDagRequest  = (FlinkDagRequest)flinkRequest;
        StringBuilder stringBuilder = new StringBuilder(
            TaskPathResolver.getLocalFlinkBinFlink(flinkDagRequest.getFlinkHomeOptions().getFlinkPath())
        );
        stringBuilder.append(" info");
        if(CollectionUtils.isNotEmpty(flinkDagRequest.getExtendedJarResources())){
            flinkDagRequest.getExtendedJarResources().forEach(extendJarResource -> {
                stringBuilder.append(" -C file:").append(extendJarResource.getJarLocalPath());
            });
        }
        if(StringUtils.isNotEmpty(flinkDagRequest.getClassName())){
            stringBuilder.append(" -c ").append(flinkDagRequest.getClassName());
        }
        stringBuilder.append(" ").append(flinkDagRequest.getMainJarResource().getJarLocalPath());
        stringBuilder.append(" ").append(flinkDagRequest.getArgs());
        String content = runCommand(stringBuilder.toString());
        // 查看是否OK
        String DAGContent = extractContent(content);
        // 空则不OK，报错
        if(StringUtils.isNotEmpty(DAGContent)){
            logger.info("Run Flink info command [{}] success.",stringBuilder.toString());
            return builder.responseStatus(ResponseStatus.SUCCESS)
                .dagText(DAGContent)
                .build();
        }
        // 任务运行失败，直接设置为 失败内容
        return builder.responseStatus(ResponseStatus.FAIL)
            .msg(content)
            .build();
    }


    public String runCommand(String command) throws Exception {
        logger.info("====RunCommand CLI命令 ====\n" + command);
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        InputStream inputStream = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
            pb.redirectErrorStream(true); // 将标准错误输出重定向到标准输出
            Process process = pb.start();
            // 获取标准输出流
            inputStream = process.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // 读取并打印输出内容
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append(Constants.CRLF_N);
            }
            // 获取进程的退出状态码
            int exitCode = process.waitFor();
            logger.info("Command executed with exit code: " + exitCode);
            return content.toString();
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String extractContent(String content) {
        String begin = "----------------------- Execution Plan -----------------------";
        String end = "--------------------------------------------------------------";
        int startIndex = content.indexOf(begin);
        int endIndex = content.indexOf(end);

        if (startIndex != -1 && endIndex != -1) {
            startIndex += begin.length(); // Move the index to the end of 'begin'
            return content.substring(startIndex, endIndex).trim();
        }
        logger.error("DAG not Conform to the rules.");
        return "";
    }

}
