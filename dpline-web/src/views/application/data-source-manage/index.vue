<template>
  <div class="data-source-manage">
    <!-- 头部 -->
    <div class="search-container">
      <div class="search-main justify-between">
        <div class="right-content justify-end">
          <div class="justify-start product-line">
            <div class="search-item">
              <p>数据源名称</p>
              <a-input
                placeholder="请输入数据源名称"
                v-model="params.dataSourceName"
              ></a-input>
              <p>类型</p>
            </div>
            <div class="search-item">
              <a-select
                placeholder="选择类型"
                style="width: 184px; height: 28px"
                :allow-clear="true"
                v-model="paramsType"
                default-value="mysql"
              >
                <a-select-option
                  :key="index"
                  :value="item.value"
                  v-for="(item, index) in typeArr"
                >
                  {{ item.label }}
                </a-select-option>
              </a-select>
            </div>
            <div class="search-item">
              <a-button
                @click="queryDataSource"
                style="margin-left: 8px"
                type="primary"
                size="small"
                icon="search"
              >
                查询
              </a-button>
              <a-button
                @click="reset"
                style="margin-left: 8px"
                size="small"
                icon="undo"
              >
                重置
              </a-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="group-button">
      <a-dropdown :trigger="['click']">
        <a-menu slot="overlay" @click="handleDataSourceFlagClick">
          <a-menu-item v-for="(item) in dataSourceFlagList"
                      :key="item.value"> {{item.label}} </a-menu-item>
        </a-menu>
        <a-button size="small" type="primary"> 全部类型 <a-icon type="down" /> </a-button>
      </a-dropdown>
      <a-button
        style="margin-left: 10px"
        @click="showDataForm(`新建数据源`, 'add')"
        type="primary"
        size="small"
        icon="plus"
      >
        添加数据源
      </a-button>
    </div>
    <!-- 内容列表 -->
    <div class="content">
      <chitu-table
        v-loading="isLoading"
        row-key="id"
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="pagination"
        @pageChange="pageChange"
        @pageSizeChange="pageSizeChange"
        :tableOpt="{
          rowClassName: (record) => {
            return record.id === rowId ? 'clickRowStyle' : 'rowStyleNone';
          },
        }"
        @change="handleTableChange"
      >
        <!-- 数据源名称 -->
        <template #dataSourceName="{ record }">
          <div class="dataSourceName" @click="openDataSourceDetail(record)">
            <a-tooltip placement="topLeft">
              <template slot="title">
                <span>{{ record.dataSourceName }}</span>
              </template>
              <div class="title">{{ record.dataSourceName }}</div>
            </a-tooltip>
          </div>
        </template>

        <!-- 环境 -->
        <template #env="{ record }">
          <div>
            {{ calEnvTitle(record.env) }}
          </div>
        </template>

        <!-- 描述 -->
        <template #description="{ record }">
          <div>
            <a-tooltip
              class="reset-toolip"
              overlayClassName="reset-toolip"
              placement="topLeft"
            >
              <template slot="title">
                <span>{{ record.description }}</span>
              </template>
              <div>{{ record.description }}</div>
            </a-tooltip>
          </div>
        </template>
        <!-- 引用作业数 -->
        <template #countJob="{ record }">
          <div
            class="countJob"
            :class="[record.countJob == 0 ? 'disabled' : 'blue']"
            @click="popCountJob(record)"
          >
            {{ record.countJob }}
          </div>
        </template>
        <!-- 引用元表数 -->
        <template #countMetaTable="{ record }">
          <div
            class="countMetaTable blue"
            :class="[record.countMetaTable == 0 ? 'disabled' : 'blue']"
            @click="popMetaTableTable(record)"
          >
            {{ record.countMetaTable }}
          </div>
        </template>
        <!-- 操作 -->
        <template #operation="{ record }">
          <div class="common-action-container">
            <!-- 编辑权限控制 -->
            <a-button
              type="link"
              :disabled="
                !(
                  userInfo.isAdmin === 1 ||
                  userInfo.isLeader === 1 ||
                  userInfo.id === record.owner
                )
              "
              @click="editDataSource(record)"
            >
              <i class="chitutree-h5 chitutreebianji"></i>修改
            </a-button>
            <!-- 删除权限控制 -->
            <a-divider type="vertical" />
            <a-button
              type="link"
              class="btn-danger"
              @click="handleDelete(record)"
              :disabled="
                !(
                  (userInfo.isAdmin === 1 ||
                    userInfo.isLeader === 1 ||
                    userInfo.id === record.owner) &&
                  record.isUsed === 0
                )
              "
            >
              <i class="chitutree-h5 chitutreeshanchu"></i>删除
            </a-button>
          </div>
        </template>
      </chitu-table>
    </div>
    <confirm-dialog
      :visible="deleteVisible"
      type="warning"
      @close="deleteVisible = false"
      @confirm="confirmDelete(deleteItem)"
    >
      <template>
        <p class="word-break">
          删除数据后不可恢复，确定要<span class="warn-message"
            >&nbsp;删除&nbsp;</span
          >吗？
        </p>
      </template>
    </confirm-dialog>
    <!-- 数据源 -->
    <dataForm
      v-if="formVisible"
      class="data-form"
      :formVisible="formVisible"
      :title="title"
      :dataForm="dataForm"
      :typeArr="typeArr"
      :projectUsers="projectUsers"
      :type="type"
      @closeDataFormtModal="closeDataFormtModal"
    />
    <!-- 引用作业清单 -->
    <count-job ref="countJob" />
    <!-- 引用元表清单 -->
    <count-meta-table ref="countMetaTable" />
  </div>
</template>
<script>
// import tableHeaderDrag from '../../../mixins/table-header-drag'
import dataForm from "./components/dataForm.vue";
// import tableSort from '../../../mixins/table-sort'
import CountJob from "./components/count-job/index";
import CountMetaTable from "./components/count-meta-table/index";
import ConfirmDialog from "@/components/confirm-dialog";
import {dataSourceFlagList, envTypeList} from "@/utils/enumType";
import dataSource from "@/utils/dataSourceUtil"
export default {
  // mixins: [tableHeaderDrag, tableSort],
  mixins: [],
  components: {
    dataForm,
    CountJob,
    CountMetaTable,
    ConfirmDialog,
  },
  computed: {
    columns() {
      return [
        {
          title: "ID",
          key: "id",
          dataIndex: "id",
          width: 60,
          scopedSlots: { customRender: "id" }
        },
        {
          title: "数据源名称",
          key: "dataSourceName",
          dataIndex: "dataSourceName",
          width: 170,
          scopedSlots: { customRender: "dataSourceName" }
        },
        {
          title: "环境",
          key: "env",
          dataIndex: "env",
          width: 80,
          scopedSlots: { customRender: "env" }
        },
        {
          title: "类型",
          key: "dataSourceType",
          dataIndex: "dataSourceType",
          width: 90,
          scopedSlots: { customRender: "dataSourceType" }
        },
        {
          title: "描述",
          key: "description",
          dataIndex: "description",
          width: 170,
          scopedSlots: { customRender: "description" },
        },
        {
          title: "引用作业数",
          key: "countJob",
          dataIndex: "countJob",
          width: 80,
          scopedSlots: { customRender: "countJob" },
        },
        {
          title: "引用元表数",
          key: "countMetaTable",
          dataIndex: "countMetaTable",
          width: 80,
          scopedSlots: { customRender: "countMetaTable" },
        },
        {
          title: "创建人",
          key: "createUser",
          dataIndex: "createUser",
          width: 80,
          scopedSlots: { customRender: "createUser" }
        },
        {
          title: "创建时间",
          key: "createTime",
          dataIndex: "createTime",
          width: 150,
          scopedSlots: { customRender: "createTime" }
        },
        {
          title: "更新人",
          key: "updateUser",
          dataIndex: "updateUser",
          width: 80,
          scopedSlots: { customRender: "updateUser" }
        },
        {
          title: "更新时间",
          key: "updateTime",
          dataIndex: "updateTime",
          width: 150,
          scopedSlots: { customRender: "updateTime" }
        },
        {
          title: "操作",
          key: "operation",
          dataIndex: "operation",
          width: 150,
          fixed: "right",
          scopedSlots: { customRender: "operation" },
        },
      ];
    },
  },
  data() {
    return {
      dataSourceFlagList: [],
      oldProjectId: "",
      isLoading: false,
      deleteVisible: false,
      deleteItem: null,
      type: "add",
      formVisible: false,
      title: "新建数据源",
      pagination: {
        current: 1,
        showSizeChanger: true,
        showQuickJumper: true,
        defaultPageSize: 20,
        total: 0,
        pageSizeOptions: ["10", "20", "40", "60"],
      },
      tableSortData: {
        columnsName: "columns",
        ref: "dataSourceTable",
      },
      headerDragData: {
        columnsName: "columns",
        ref: "dataSourceTable",
      },
      typeArr: [
        { value: "mysql", label: "mysql" },
        { value: "kafka", label: "kafka" }
      ],
      tableData: [],
      loading: false,
      userInfo: {
        isAdmin: null,
        isLeader: null,
        employeeNumber: null,
      },
      paramsType: undefined,
      params: {
        orderByClauses: [
          {
            field: "create_time", //排序键名
            orderByMode: 1, //排序模式（0：正序，1：倒序）
          },
        ],
        page: 1,
        pageSize: 20,
        dataSourceName: "",
        dataSourceType: "",
        jobName: ""
      },
      dataForm: {
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
        hadoopConfDir: "",
        clusterName: "",
        enabledDatahub: 0,
        authKafkaClusterAddr: "",
        clusterToken: "",
        enabledFlag: 0,
        supportDatahub: 0,
      },
      rowId: null,
      projectUsers: [],
    };
  },
  created() {
    this.init();
    this.oldProjectId = this.$route.query.projectId;
  },
  watch: {
    $route: {
      //需要同时监听切换项目和切换页签时的路由变化，beforeRouteEnter和activated只能监听到切换页签，不能监听到项目切换
      handler() {
        if (
          this.$route.name === "DataSourceManage" &&
          this.$route.query.projectId !== this.oldProjectId
        ) {
          Object.assign(this.$data, this.$options.data(this));
          this.init();
          this.oldProjectId = this.$route.query.projectId;
        }
      },
      deep: true,
      // immediate: true
    },
    "$store.getters.isRemoveTag": {
      //监听关闭页签，关闭页签后清除缓存
      handler(val, oldVal) {
        if (val === oldVal) {
          return;
        }
        if (this.$store.getters.removeRouteName.includes("DataSourceManage")) {
          this.$common.toClearCache(this);
        }
      },
    },
  },
  mounted() {},
  // beforeRouteEnter: (to, from, next) => {
  //   next((vm) => {
  //     if (vm.$route.query.projectId !== vm.oldProjectId) {
  //       Object.assign(vm.$data, vm.$options.data(vm))

  //       vm.init()
  //       vm.oldProjectId = vm.$route.query.projectId
  //     }
  //   })
  // },
  methods: {
    init() {
      this.dataSourceFlagList = dataSourceFlagList;
      this.getDataSourceList(this.params);
    },
    handleDataSourceFlagClick(record) {
      console.log("获取点击的数据为:{}", record);
    },
    popCountJob(record) {
      if (!record.countJob) return;
      this.$refs.countJob.open(record, this.params);
    },
    popMetaTableTable(record) {
      if (!record.countMetaTable) return;
      this.$refs.countMetaTable.open(record, this.params);
    },
    // 根据环境标识输出对应中文名称
    calEnvTitle(envValue) {
      let envTitle = '';
      for (let i = 0; i < envTypeList.length; i++) {
        if (envTypeList[i].value == envValue) {
          envTitle = envTypeList[i].label;
          break
        }
      }
      console.log("打印标记一下:{}, 名称:{}", envValue, envTitle);
      return envTitle;
    },
    // 获取项目成员
    async getUserList() {
      const params = {
        id: Number(this.$route.query.projectId),
      };
      let res = await this.$http.post(
        "/project/projectManagement/getProjectUser",
        params
      );
      if (res.code === 200) {
        this.projectUsers = res.data;
      }
    },
    handleDelete(item) {
      this.deleteItem = item;
      this.deleteVisible = true;
    },
    // 删除数据
    async confirmDelete(dataSourceInfo) {
      // console.log('deleteItem-dataSourceInfo: ', dataSourceInfo)
      let params = {
        id: dataSourceInfo.id,
      };
      let res = await this.$http.post("/dataSource/delete", params, {
        headers: {
          projectId: Number(this.$route.query.projectId),
        },
      });
      if (res.code === 200) {
        this.getDataSourceList(this.params);
        this.$message.success({ content: "删除成功", duration: 2 });
      } else {
        this.$message.error({ content: res.msg, duration: 2 });
      }
    },
    // 点击查询
    queryDataSource() {
      this.params.page = 1;
      this.pagination.current = 1;
      this.getDataSourceList(this.params);
    },
    resetData() {
      this.params = {
        orderByClauses: [
          {
            field: "create_time", //排序键名
            orderByMode: 1, //排序模式（0：正序，1：倒序）
          },
        ],
        page: 1,
        pageSize: 20,
        dataSourceName: "",
        dataSourceType: "",
        jobName: ""
      };
      this.paramsType = "";
      this.pagination.current = 1;
    },
    reset() {
      this.resetData();
      this.getDataSourceList(this.params);
    },
    // 显示数据源详情
    openDataSourceDetail(dataSourceInfo) {
      this.rowId = dataSourceInfo.id;
      this.dataForm = dataSource.convertToDataSource(dataSourceInfo)
      this.dataForm.password = this.dataForm.password || "******";
      this.showDataForm(`${this.titleText}数据源详情`, "detail");
    },
    // 修改数据源
    editDataSource: function (dataSourceInfo) {
      this.dataForm.id = "";
      this.dataForm.streamLoadUrl = "";
      this.rowId = dataSourceInfo.id;
      this.dataForm = dataSource.convertToDataSource(dataSourceInfo);
      this.dataForm.password = this.dataForm.password || "******";
      this.showDataForm(`编辑${dataSourceInfo.dataSourceName}数据源`, "edit");
    },
    // 显示数据源窗口
    showDataForm(title, type) {
      this.title = title;
      this.type = type;
      if (type === "add") {
        this.dataForm = {
          dataSourceName: "",
          dataSourceType: "",
          description: "",
          dataSourceUrl: "",
          databaseName: "",
          userName: "",
          password: "",
          certifyType: "default",
          streamLoadUrl: "",
          hbaseZnode: "",
          hadoopConfDir: "",
          clusterName: "",
          enabledDatahub: 0,
          authKafkaClusterAddr: "",
          clusterToken: "",
          enabledFlag: 0,
          supportDatahub: 0,
        };
      }
      this.formVisible = true;
    },
    // 关闭数据源窗口
    closeDataFormtModal(data) {
      this.formVisible = false;
      if (data) {
        this.params.orderByClauses.field = "create_time";
        this.params.orderByClauses.orderByMode = 1;
        this.params.page = 1;
        this.getDataSourceList(this.params);
      }
    },
    // 排序，筛选变化时触发
    handleTableChange(pagination, filters, sorter) {
      if (sorter.order) {
        if (sorter.order === "ascend") {
          this.params.orderByClauses[0].orderByMode = 0;
        } else {
          this.params.orderByClauses[0].orderByMode = 1;
        }
      }
      this.params.orderByClauses[0].field = this.$common.toLine(sorter.field);
      this.getDataSourceList(this.params);
    },
    // 分页数据变化
    pageChange(pageInfo) {
      this.params.page = pageInfo.page;
      this.getDataSourceList(this.params);
    },
    // pageSize变化回调
    pageSizeChange(pageSizeInfo) {
      this.params.page = pageSizeInfo.current;
      this.params.pageSize = pageSizeInfo.size;
      this.getDataSourceList(this.params);
    },
    // 重置分页信息
    resetPagination(pageInfo) {
      this.params.page = pageInfo.page;
      this.pagination.current = pageInfo.page;
      this.pagination.total = pageInfo.rowTotal;
    },
    // 获取数据源列表
    async getDataSourceList(params) {
      if (this.paramsType != undefined) {
        params.dataSourceType = this.paramsType;
      } else {
        params.dataSourceType = "";
      }
      this.isLoading = true;
      let res = await this.$http.post("/system/dataSource/list", params, {
        headers: {
          projectId: Number(this.$route.query.projectId),
        },
      });
      this.isLoading = false;
      if (res.code === 200) {
        this.tableData = res.data.rows;
        this.$log("tableData ", this.tableData, "1");
        this.resetPagination(res.data);
      } else {
        this.$message.error({ content: res.msg, duration: 2 });
      }
    },
  },
};
</script>
<style lang="scss" scoped>
/deep/ .clickRowStyle {
  background-color: #f7f0ff;
}
.warn-message {
  color: #ff9300;
}
.rowStyleNone {
  background-color: #fff;
}
.data-source-manage {
  width: 100%;
  height: 100%;
  font-size: 12px;
  color: #333;
  .search-container {
    padding-bottom: 8px;
    background: #eff1f6;
    .search-main {
      padding: 8px 16px;
      border-bottom: 1px solid #dee2ea;
      box-sizing: border-box;
      background: #ffffff;
      .right-content {
        .product-line {
          margin-right: 16px;
          flex-flow: wrap;
          .search-item {
            margin: 8px 0;
            display: flex;
            align-items: center;
            .ant-input {
              width: 184px;
              height: 28px !important;
              line-height: 28px !important;
            }
            p {
              margin: 0 8px 0 20px;
            }
          }
        }
        .product-line {
          /deep/ .ant-select-selection--single {
            height: 28px !important;
            .ant-select-selection__rendered {
              line-height: 28px !important;
            }
          }
        }
      }
    }
  }
  .group-button {
    padding: 12px 16px;
  }
  .content {
    padding: 0 16px;
    .countJob,
    .countMetaTable {
      cursor: pointer;
      &.disabled {
        cursor: default;
        color: #333;
      }
    }

    .dataSourceName {
      .title {
        color: #0066ff;
        cursor: pointer;
      }
    }
    .operation {
      display: flex;
      .operation-btn {
        user-select: none;
        margin-right: 10px;
        i {
          margin-right: 5px;
        }
      }
      .operation-btn-active {
        color: #0066ff;
        cursor: pointer;
      }
      .operation-btn-disabled {
        color: #999;
        cursor: not-allowed;
      }
      button {
        border: none;
        padding: 0;
        margin-right: 24px;
        color: #0066ff;
        i {
          margin-right: 5px;
        }
        &:hover,
        &:focus {
          background-color: #f7f0ff;
        }
        &:disabled {
          background-color: #f7f0ff;
          color: #999;
        }
      }
      /deep/ .ant-btn[disabled] {
        background-color: #fff;
        color: #999;
        &:hover {
          background-color: #f7f0ff;
        }
      }
    }
  }
}
</style>
