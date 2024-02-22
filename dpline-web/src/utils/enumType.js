/**
 * 环境列表相关枚举
 * @type 环境列表
 */
export const envTypeList = [
    {
        label: "测试环境",
        value: "test"
    },
    {
        label: "生产环境",
        value: "prod"
    }
]
//clusterTypeList
export const clusterTypeList = [
    {
        label: "Kubernetes",
        value: "kubernetes"
    },
    {
        label: "Yarn",
        value: "yarn"
    }
]
export const jarFunctionTypeList = [
    {
        label: "MAIN",
        value: "MAIN"
    },
    {
        label: "UDF",
        value:"UDF",
    },
    {
        label: 'CONNECTOR',
        value:"CONNECTOR",
    },
    {
        label: 'CONNECTOR',
        value:"EXTENDED"
    }
]

export const adminTypeList = [
    {
        label: 0,
        value:"GENERAL",
    },
    {
        label: 1,
        value:"ADMIN",
    }
]
export const jarTypeList = [
    { label: "公共资源", value: "public" },
    { label: "项目资源", value: "project" },
]


export const runModeList = [
    { label: "Session", value: "session" },
    { label: "Application", value: "application" },
]

export const execStatusList = [
    {
        value: 'SUBMITTING',
        text: '提交中',
    },
    {
        value: 'INITIALIZING',
        text: '初始化中',
    },
    {
        value: 'RUNNING',
        text: '运行中',
    },
    {
        value: 'CANCELLING',
        text: 'CANCELLING'
    },
    {
        value: 'CANCELED',
        text: 'CANCELED',
    },
    {
        value: 'STOPPING',
        text: 'STOPPING',
    },
    {
        value: 'STOPPED',
        text: 'STOPPED',
    },
    {
        value: 'FAILING',
        text: '失败中',
    },
    {
        value: 'FAILED',
        text: '失败',
    },
    {
        value: 'FINISHED',
        text: '完成',
    },
    {
        value: 'TERMINATED',
        text: '中断',
    },
    {
        value: 'NONE',
        text: '无状态'
    },

]

export const deployedStatusList = [
    {
        key: 0,
        value: 'UNDEPLOYED',
        text: '未部署',
    },
    {
        key: 1,
        value: 'DEPLOYED',
        text: '部署完成',
    },
    {
        key: 2,
        value: 'DEPLOYING',
        text: '部署中',
    },
    {
        key: 3,
        value: 'DEPLOYFAILED',
        text: '部署失败',
    }
]


export const dataSourceFlagList = [
    { label: "公共数据源", value: "public" },
    { label: "项目数据源", value: "project" },
]

export const motorTypeList = [
    {
        label:"FLINK",
        value:"FLINK"
    },
    {
        label:"SPARK",
        value:"SPARK"
    }

]
export const alertTypeList =[
    {
        label:"HTTP",
        value:"HTTP"
    },
    {
        label:"EMAIL",
        value:"EMAIL"
    },
    {
        label:"WECOM",
        value:"WECOM"
    },
    {
        label:"NONE",
        value:"NONE"
    }
]

export const requestTypeList = [
    {
        label:"POST",
        value:"POST"
    },
    {
        label:"GET",
        value:"GET"
    },
]

export const alertModeList =[
    {
        label: "NONE",
        value: "不告警"
    },
    {
        label:"STOPPED",
        value:"任务停止"
    },
    {
        label:"RUNNING",
        value:"任务运行"
    },
    {
        label: "ALL",
        value: "任何状态变化"
    }
]

export const sessionType = [
    "OPERATE_LOG",
    "OTHER"
]


