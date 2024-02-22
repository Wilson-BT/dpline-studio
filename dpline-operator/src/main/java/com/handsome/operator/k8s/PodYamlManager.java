package com.handsome.operator.k8s;

import com.handsome.common.Constants;
import com.handsome.common.enums.ResFsType;
import com.handsome.common.request.SubmitRequest;
import com.handsome.common.util.Asserts;
import com.handsome.common.util.FileUtils;
import com.handsome.common.util.PropertyUtils;
import com.handsome.common.util.TaskPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.Optional;


public class PodYamlManager {


    public static final String MAIN_FILE_KEY = "main-file";

    private static final String CUSTOM_POD_FILE_NAME = "code-pod-template.yaml";

    private static final String POD_FILE_NAME = "pod-template.yaml";

    public static final String EXTENDED_JARS = "extended-jars";

    public static final String SQL_POD_TEM_NAME = "sql-pod-template.yaml";

    private final SubmitRequest submitRequest;

    private String localTaskPath;

    public String remoteNodePrefix = PropertyUtils.getProperty(Constants.K8S_LOCAL_PATH_PREFIX);

    private static final Logger logger = LoggerFactory.getLogger(PodYamlManager.class);

    public PodYamlManager(SubmitRequest submitRequest) {
        this.submitRequest = submitRequest;
        this.localTaskPath = TaskPath.getTaskDeployDir(submitRequest.getTaskDefinitionOptions().getProjectCode(),
                                                            submitRequest.getTaskDefinitionOptions().getTaskId());
    }

    public String initPodTemplate() throws IOException {
        // read
        InputStream sourceInPutStream = getSourceFile();
        // update
        Optional<String> optionalS = replaceParams(sourceInPutStream);
        // write to new file
        return optionalS.map(this::flushOut).orElse("");
    }

    private String flushOut(String content) {
        String podFilePath = this.localTaskPath + "/" + POD_FILE_NAME;
        FileWriter fileWriter = null;
        try {
            FileUtils.createDir(this.localTaskPath, ResFsType.LOCAL);
            fileWriter = new FileWriter(podFilePath);
            fileWriter.write(content);
            fileWriter.flush();
            logger.info("Create pod file success, pod path {}", podFilePath);
        } catch (IOException e) {
            FileUtils.deleteFile(podFilePath, ResFsType.LOCAL);
            logger.error("Create pod file error.");
            e.printStackTrace();
        } finally {
            if (Asserts.isNotNull(fileWriter)) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return podFilePath;
    }

    private Optional<String> replaceParams(InputStream resourceAsStream) throws IOException {
        if (resourceAsStream == null) {
            return Optional.empty();
        }
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        // 部署地址: project_code/taskName
        String taskRemoteLocalPath = String.format("%s%s", remoteNodePrefix, this.submitRequest.getOtherOptions().getDeployAddress());
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.startsWith("#")) {
                break;
            }
            // 直接替换
            if (line.contains("${FLINK_POD_NAME}")) {
                line = line.replace("${FLINK_POD_NAME}",submitRequest.getFlinkTaskInstanceName());
            }
            if (line.contains("${FLINK_NAMESPACE}")) {
                line = line.replace("${FLINK_NAMESPACE}", submitRequest.getK8sOptions().getNameSpace());
            }
            if (line.contains("${SERVICE_ACCOUNT}")) {
                line = line.replace("${SERVICE_ACCOUNT}", submitRequest.getK8sOptions().getServiceAccount());
            }
            if (line.contains("${IMAGE_NAME}")) {
                line = line.replace("${IMAGE_NAME}", submitRequest.getK8sOptions().getImageAddress());
            }
            if (line.contains("${USER_MAIN_JARS}")) {
                line = line.replace("${USER_MAIN_JARS}",  taskRemoteLocalPath + "/" + MAIN_FILE_KEY);
            }
            if (line.contains("${CONNECTOR_UDF_JARS}")) {
                line = line.replace("${CONNECTOR_UDF_JARS}", taskRemoteLocalPath + "/" + EXTENDED_JARS);
            }
            stringBuilder.append(line).append("\n");
        }
        return Optional.of(stringBuilder.toString());
    }

    private InputStream getSourceFile() {
        String podName;
        switch (submitRequest.getTaskDefinitionOptions().getTaskType()){
            case SQL:
                podName = SQL_POD_TEM_NAME;
                break;
            case CUSTOM_CODE:
                podName = CUSTOM_POD_FILE_NAME;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + submitRequest.getTaskDefinitionOptions().getTaskType());
        }
        return PodYamlManager.class.getClassLoader().getResourceAsStream(podName);
    }

    /**
     * 读取 yaml 文件，并转化为map
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Map<String, String> readYamlToMap(File file) {
        try {
            if (file.exists()) {
                Yaml yaml = new Yaml();
                //也可以将值转换为Map
                Map<String, String> map = (Map<String, String>) yaml.load(new FileInputStream(file));
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
