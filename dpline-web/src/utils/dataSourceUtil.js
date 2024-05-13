const dataSource = {
    convertObj: (dataSourceType, connectionParamStr) => {
        let resultObj = {
            user: '',
            password: '',
            url: ''
        }
        if (!connectionParamStr || connectionParamStr === null || connectionParamStr === undefined) {
            return resultObj;
        }
        let connectionParamJson = JSON.parse(connectionParamStr);
        switch (dataSourceType) {
            case "mysql":
                resultObj.user = connectionParamJson.user;
                resultObj.password = connectionParamJson.password;
                var newJdbcUrl = connectionParamJson.jdbcUrl.replace("jdbc:mysql://", "");
                resultObj.url = newJdbcUrl;
                break
            case "kafka":
                break
        }
        return resultObj;
    },

    /**
     * 将原始数据格式转为页面展示数据对象
     * @param dataSourceInfo 原始数据格式
     */
    convertToDataSource: (dataSourceInfo) => {
        let dataForm = {
            id: "",
            dataSourceName: "",
            dataSourceType: null,
            streamLoadUrl: "",
            description: "",
            createUser: "",
            dataSourceUrl: "",
            databaseName: "",
            userName: "",
            password: "",
            certifyType: "default",
            hbaseZnode: "",
            hadoopHome: "",
            clusterName: "",
            enabledDatahub: 0,
            authKafkaClusterAddr: "",
            clusterToken: "",
            enabledFlag: 0,
            supportDatahub: 0,
        };
        if (dataSourceInfo === null || dataSourceInfo === undefined) {
            return dataForm;
        }
        dataForm.dataSourceName = dataSourceInfo.dataSourceName;
        dataForm.dataSourceType = dataSourceInfo.dataSourceType;
        dataForm.description = dataSourceInfo.description;
        let dataSourceObj = dataSource.convertObj(dataSourceInfo.dataSourceType, dataSourceInfo.connectionParams);
        dataForm.dataSourceUrl = dataSourceObj.url;
        dataForm.userName = dataSourceObj.user;
        dataForm.password = dataSourceObj.password;
        return dataForm;
    }
};

export default dataSource