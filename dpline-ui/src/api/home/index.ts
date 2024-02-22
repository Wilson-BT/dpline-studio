import request from '/@/utils/request';

/**
 * 获取首页所需数据接口集合
 * @method listProject 项目列表
 */
export function useHomeApi() {
    return {
        listProject: (params?: object) => {
            return request({
                url: '/json/sys/project.json',
                method: 'get',
                data: params,
            });
        }
    };
}
