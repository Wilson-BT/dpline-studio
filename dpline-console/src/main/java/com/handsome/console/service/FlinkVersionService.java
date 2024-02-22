package com.handsome.console.service;

import com.handsome.common.enums.ReleaseState;
import com.handsome.dao.entity.User;
import com.handsome.common.util.Result;

public interface FlinkVersionService {
    /**
     *
     * 创建flink 版本
     * @param loginUser
     * @param name
     * @param description
     * @param flinkPath
     * @return
     */
    Result<Object> createFlinkVersion(User loginUser,
                                      String name,
                            ReleaseState releaseState,
                            String description,
                            String flinkPath);
    /**
     * 更新 flink 版本
     *
     * @param loginUser
     * @param releaseState
     * @param alias
     * @param description
     * @param flinkPath
     * @return
     */
    Result<Object> updateFlinkVersion(User loginUser,
                                      int flinkVersionId,
                                      ReleaseState releaseState,
                                      String alias,
                                      String description,
                                      String flinkPath);

    Result<Object> deleteFlinkVersion(User loginUser, int flinkVersionId);


//    Result<Object> queryAllFlinkVersionName(ReleaseState releaseState);

    /**
     * 查询到所有的 Flink 版本
     *
     * @return
     */
    Result<Object> queryFlinkVersionList(ReleaseState releaseState);

}
