package com.dpline.common.request;

import com.dpline.common.enums.FileType;
import com.dpline.common.enums.RunModeType;
import com.dpline.common.params.FlinkHomeOptions;
import com.dpline.common.util.Asserts;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * flink
 */
@Data
public class FlinkDagRequest extends FlinkRequest {

    String className;

    JarResource mainJarResource;

    String args;


    public FlinkDagRequest() {
    }

    public FlinkDagRequest(String className, JarResource mainJarResource, String args, List<JarResource> extendedJarResources) {
        this.className = className;
        this.mainJarResource = mainJarResource;
        this.args = args;
        this.extendedJarResources = extendedJarResources;
    }


    public void addExtendedJarResource(JarResource jarResource) {
        if(Asserts.isNull(extendedJarResources)){
            extendedJarResources = new ArrayList<>();
        }
        extendedJarResources.add(jarResource);
    }



    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {

        private FlinkHomeOptions flinkHomeOptions;

        private String className;

        private JarResource mainJarResource;

        private String args;

        private RunModeType runModeType;

        private FileType fileType;

        /**
         * extended jars
         */
        List<JarResource> extendedJarResources;

        public Builder flinkHomeOptions(FlinkHomeOptions flinkHomeOptions){
            this.flinkHomeOptions = flinkHomeOptions;
            return this;
        }

        public Builder className(String className){
            this.className = className;
            return this;
        }

        public Builder mainJarResource(JarResource mainJarResource){
            this.mainJarResource = mainJarResource;
            return this;
        }

        public Builder args(String args){
            this.args = args;
            return this;
        }

        public Builder runModeType(RunModeType runModeType){
            this.runModeType = runModeType;
            return this;
        }

        public Builder fileType(FileType fileType){
            this.fileType = fileType;
            return this;
        }

        public Builder extendedJarResources(List<JarResource> extendedJarResources){
            this.extendedJarResources = extendedJarResources;
            return this;
        }

        public FlinkDagRequest build(){
            FlinkDagRequest flinkDagRequest = new FlinkDagRequest();
            flinkDagRequest.setArgs(args);
            flinkDagRequest.setFileType(fileType);
            flinkDagRequest.setFlinkHomeOptions(flinkHomeOptions);
            flinkDagRequest.setRunModeType(runModeType);
            flinkDagRequest.setExtendedJarResources(extendedJarResources);
            flinkDagRequest.setClassName(className);
            flinkDagRequest.setMainJarResource(mainJarResource);
            return flinkDagRequest;
        }

    }

}
