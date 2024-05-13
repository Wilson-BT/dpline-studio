package com.dpline.common.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YarnClusterParams  implements ClusterParams{

    /**
     * config path
     */
    private String hadoopHome;

    /**
     *  yarn proxy url
     */
    private String yarnProxyUrl;

}
