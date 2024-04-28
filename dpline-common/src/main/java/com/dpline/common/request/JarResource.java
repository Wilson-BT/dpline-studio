package com.dpline.common.request;

import com.dpline.common.params.CommonProperties;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class JarResource implements Serializable {

    /**
     * remote path
     */
    String remotePath;

    /**
     * local path
     */
    String localParentPath;

    /**
     * jar name
     */
    String jarName;


    public JarResource() {
    }

    public JarResource(String remotePath, String localParentPath, String jarName) {
        this.remotePath = remotePath;
        this.localParentPath = localParentPath;
        this.jarName = jarName;
    }

    public String getJarLocalPath() {
        return CommonProperties.pathDelimiterResolve(localParentPath) + "/" + jarName;
    }
}
