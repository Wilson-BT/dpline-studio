package com.dpline.console.service;

import com.dpline.common.util.Result;
import com.dpline.dao.entity.FlinkVersion;
import com.dpline.dao.generic.Pagination;
import com.dpline.dao.rto.FlinkVersionRto;
import org.apache.ibatis.annotations.Param;

public interface FlinkVersionService {

    /**
     * create
     *
     * @param flinkVersion
     * @return
     */
    Result<Object> create(FlinkVersion flinkVersion);

    /**
     * 删除
     *
     * @param flinkVersion
     * @return
     */
    int delete(FlinkVersion flinkVersion);

    /**
     * 修改状态
     *
     * @param flinkVersion
     * @return
     */
    int updateState(FlinkVersion flinkVersion);

    /**
     * 更新
     *
     * @param flinkVersion
     * @return
     */
    Result<Object> updateInfo(FlinkVersion flinkVersion);

    /**
     * 分页查询flink版本列表
     *
     * @param flinkVersionRto
     * @return
     */
    Pagination<FlinkVersion> list(FlinkVersionRto flinkVersionRto);

    Result<Object> queryFlinkVersion();

    Result<Object> searchFlinkVersion(String motorType);

    Result<Object> selectFlinkVersionById(Long flinkVersionId);

}
