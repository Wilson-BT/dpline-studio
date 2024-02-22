import request from '/@/utils/request';

/**
 * 获取首页所需数据接口集合
 * @methon addProject 新增项目
 * @method listProject 项目列表
 */
export function useProjectApi() {
    return {
        addProject: (params: object) => {
            return request({
                url: "projects",
                method: "post",
                data: params
            })
        },

        listProject: (params?: object) => {
            return request({
                url: "projects",
                method: "get",
                params: params
            });
        }
    };
}
