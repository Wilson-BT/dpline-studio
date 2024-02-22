<template>
  <div class="cluster-manage-container">
    <div class="search-container">
      <div class="search-main justify-between">
        <div class="left-content justify-between">
          <p>集群名称</p>
          <a-auto-complete class="search"
                           size="large"
                           v-model="params.vo.clusterName"
                           placeholder='搜索集群名称...'
                           isShowBtn="false" />
          <a-button @click="search"
                    style="margin-left:8px;"
                    type="primary"
                    size="small"
                    icon="search">
            查询
          </a-button>
          <a-button @click="reset"
                    style="margin-left:8px;"
                    size="small"
                    icon="undo">
            重置
          </a-button>
        </div>
        <div class="right-content justify-end">
          <div class="justify-start product-line">
            <a-button @click="showDialogEvent"
                      type="primary"
                      size="small"
                      icon="plus">
              <span>新建集群</span>
            </a-button>
          </div>
        </div>
      </div>

    </div>
    <div class="table-container">
      <chitu-table v-loading="isLoading"
                 row-key="id"
                 ref="clusterManageTable"
                 :columns="columns"
                 :data-source="tableDatacluster"
                 @change="handleTableChange"
                 :pagination="pagination"
                 @pageChange="pageChange"
                 @pageSizeChange="pageSizeChange">
        <template #enabledFlag="{record}">
          <div class="refer-user-count" @click="updateState(record)">
            {{ record.enabledFlag === 0 ? "未启用" : "已启用" }}
          </div>
        </template>
        <template #referUserCount="{record}">
          <div class="refer-user-count"
               @click="showclusterrManageDialog(record)">
            {{ record.referUserCount }}
          </div>
        </template>
        <template #referProjectCount="{record}">
          <div>
            {{ record.referProjectCount }}
          </div>
        </template>
        <template #operation="{record}">
          <div class="operation">
            <span @click="openclusterDetail(record)">详情</span>
            <a-divider type="vertical"></a-divider>
            <span class="delete-color"
                  @click="handleDeletecluster(record)">
              <i class="chitutree-h5 chitutreeshanchu"></i>删除
            </span>
          </div>
        </template>
      </chitu-table>
    </div>

    <confirm-dialog :visible="deleteVisible"
                    type="warning"
                    @close="deleteVisible=false"
                    @confirm="deletecluster(deleteItem)">
      <template>
        <p class="word-break">确定要<span class="warn-message">&nbsp;删除&nbsp;</span>吗？</p>
      </template>
    </confirm-dialog>
    <!-- 添加引擎 -->
    <add-cluster-dialog ref="dialogVisible"
                       @confirmEvent="confirmEvent" />
    <!-- 引擎使用者 -->
    <cluster-user-manage ref="clusterUserDialog"
                        @confirm="clusterUserConfirm"
                        @cancel="clusterUserCancel" />
    <!-- 项目引用数 -->
    <cluster-projects ref="clusterProjects"
                     v-if="isShowProjectsDialog"
                     :isShowProjectsDialog="isShowProjectsDialog"
                     :clusterInfo="clusterInfo"
                     @closeProjectModal="closeProjectModal" />
    <!-- 集群详情 -->
    <cluster-detail ref="clusterDetail" @confirmEvent="confirmEvent"/>
  </div>
</template>

<script>
  import addClusterDialog from '../components/addClusterDialog.vue'
  import clusterUserManage from '../components/clusterUserManage.vue'
  import clusterProjects from '../components/clusterProjects.vue'
  import clusterDetail from '../components/clusterDetail.vue'
  import ConfirmDialog from '@/components/confirm-dialog'
  import tableSort from '@/mixins/table-sort'
  export default {
    name: 'clusterManageTable',
    mixins: [tableSort],
    props: {},
    components: {
      addClusterDialog,
      clusterUserManage,
      clusterProjects,
      clusterDetail,
      ConfirmDialog
    },
    watch: {
      '$store.getters.isRemoveTag': {//监听关闭页签，关闭页签后清除缓存
        handler (val, oldVal) {
          if (val === oldVal) {
            return
          }
          if (this.$store.getters.removeRouteName.includes('SystemSettingcluster')) {
            this.$common.toClearCache(this);
          }
        }
      },
      params: {
        handler () {
        },
        deep: true
      }
    },
    computed: {
      columns () {
        return [
          {
            title: '集群名称',
            width: 268,
            dataIndex: 'clusterName'
          },
          {
            title: '集群部署类型',
            width: 100,
            dataIndex: 'clusterType',
            ellipsis: true
          },
          {
            title: '是否启用',
            width: 80,
            dataIndex: 'enabledFlag',
            scopedSlots: { customRender: 'enabledFlag' }
          },
          {
            title: '关联用户数',
            dataIndex: 'referUserCount',
            sortDirections: ['descend', 'ascend'],
            sorter: () => this.handleTableChange,
            width: 100,
            scopedSlots: { customRender: 'referUserCount' },
            sortOrder: this.sortedInfo.columnKey === 'referUserCount' && this.sortedInfo.order
          },
          {
            title: '项目引用数',
            dataIndex: 'referProjectCount',
            sortDirections: ['descend', 'ascend'],
            sorter: () => this.handleTableChange,
            width: 100,
            scopedSlots: { customRender: 'referProjectCount' },
            sortOrder: this.sortedInfo.columnKey === 'referProjectCount' && this.sortedInfo.order
          },
          {
            title: '创建时间',
            dataIndex: 'createTime',
            defaultSortOrder: 'descend',
            sortDirections: ['descend', 'ascend'],
            scopedSlots: { customRender: 'createTime' },
            width: 120,
            sorter: () => this.handleTableChange,
            sortOrder: this.sortedInfo.columnKey === 'createTime' && this.sortedInfo.order
          },
          {
            title: '操作',
            dataIndex: 'operation',
            fixed: 'right',
            width: 120,
            scopedSlots: { customRender: 'operation' }
          }]
      }
    },
    data () {
      return {
        dataSource: [],
        deleteItem: null,
        deleteVisible: false,
        headerDragData: {
          columnsName: 'columns',
          ref: 'clusterManageTable'
        },
        tableSortData: {
          columnsName: 'columns',
          ref: 'clusterManageTable'
        },
        tableDatacluster: [],
        isLoading: false,
        pagination: {
          current: 5,
          showSizeChanger: true,
          showQuickJumper: true,
          defaultPageSize: 20,
          total: 0,
          pageSizeOptions: ['10', '20', '40', '60']
        },
        params: {
          orderByClauses: [{
            field: "create_time", //排序键名
            orderByMode: 1 //排序模式（0：正序，1：倒序）
          }],
          page: 1,
          pageSize: 20,
          vo: {
            clusterName: null
          }
        },
        isShowProjectsDialog: false,
        clusterInfo: {}
      }
    },
    methods: {
      beforeCreate() {

      },
      reset () {
        this.params.vo.clusterName = null
        this.search()
      },
      search () {
        this.params.page = 1
        this.getclusterManagerList(this.params)
      },

      // 更新启用状态
      async updateState(record) {
        if (record == null) {
          return
        }
        // record.enabledFlag === 0 ? record.enabledFlag = 1 : record.enabledFlag = 0;
        const params = {
          id: record.id,
          enabledFlag: record.enabledFlag === 0 ? 1 : 0
        };

        let res = await this.$http.post('/system/clusterManagement/updateState', params)
        if (res.code === 200) {
          this.$message.success({ content: '修改状态成功', duration: 2 });
          record.enabledFlag = params.enabledFlag
        }else {
          this.$message.error({ content: res.msg, duration: 2 });

        }
      },

      showDialogEvent () {
        this.$refs.dialogVisible.isShowDialog = true;
      },
      // 打开引擎使用人数弹框
      openclusterUseCount (clusterInfo) {
        console.log(JSON.stringify(clusterInfo));
      },
      // 关闭引擎使用数弹框
      closeProjectModal () {
        this.isShowProjectsDialog = false
      },
      // 打开引擎详情
      openclusterDetail (clusterInfo) {
        this.$refs.clusterDetail.open(JSON.parse(JSON.stringify(clusterInfo)));
      },
      handleDeletecluster (item) {
        this.deleteItem = item
        this.deleteVisible = true
      },
      // 删除引擎
      async deletecluster (clusterInfo) {
        const params = { id: clusterInfo.id }
        let res = await this.$http.post('/system/clusterManagement/delete', params)
        if (res.code === 200) {
          if (res.data > 0) {
            this.getclusterManagerList(this.params);
            this.$message.success({ content: '删除成功', duration: 2 });
          } else {
            this.$message.warning({ content: res.msg, duration: 2 });
          }
        } else {
          this.$message.error(res.msg)
        }
      },
      // 重置分页信息
      resetPagination (pageInfo) {
        this.pagination.current = pageInfo.page
        this.pagination.total = pageInfo.rowTotal
      },
      clusterUserConfirm (data) {
        if (data) this.getclusterManagerList(this.params)
      },
      clusterUserCancel () {
        this.getclusterManagerList(this.params)
      },
      // 排序，筛选变化时触发
      handleTableChange (pagination, filters, sorter) {
        let sortObj = {}
        this.params.orderByClauses = []
        this.resetSortMethods(sorter)
        sorter = this.sortedInfo
        if (sorter.order) {
          if (sorter.field === 'clusterVersion') {
            sortObj.field = 'cluster_version'
          } else if (sorter.field === 'createTime') {
            sortObj.field = 'create_time'
          }
          // sorter.order = this.sortedInfo.order
          if (sorter.order === 'ascend') {
            sortObj.orderByMode = 0
          } else {
            // 探索：只有升序和降序
            sortObj.orderByMode = 1
          }
          this.params.orderByClauses.push(sortObj)
          this.getclusterManagerList(this.params)
        }
      },
      // 展示引擎使用者弹框
      showclusterrManageDialog (record) {
        this.$refs.clusterUserDialog.open(record)
      },
      // 分页数据变化
      pageChange (pageInfo) {
        this.params.page = pageInfo.page
        this.getclusterManagerList(this.params)
      },
      // pageSize变化回调
      pageSizeChange (pageSizeInfo) {
        this.params.page = pageSizeInfo.current
        this.params.pageSize = pageSizeInfo.size
        this.getclusterManagerList(this.params)
      },
      // 获取引擎列表
      async getclusterManagerList (params) {
        this.isLoading = true;
        let res = await this.$http.post('/system/clusterManagement/list', params);
        this.isLoading = false
        if (res.code === 200 && res.data != null) {
          this.tableDatacluster = [...res.data.rows]
          this.resetPagination(res.data)
        } else {
          this.$message.error({ content: res.msg, duration: 2 })
        }
      },
      confirmEvent (data) {
        if (data) {
          this.params.page = 1
          this.params.orderByClauses[0].field = 'create_time'
          this.params.orderByClauses[0].orderByMode = 1
          this.getclusterManagerList(this.params)
        }
      }
    },
    mounted () {

    },
    created () {
      this.getclusterManagerList(this.params)
    }
  }
</script>

<style lang="scss" scoped>
.search {
  //自动补齐输入框
  /deep/ .ant-input {
    height: 28px !important;
    width: 184px;
  }
  /deep/ .search-btn {
    width: 28px;
    height: 28px;
    text-align: center;
    cursor: pointer;
    color: #fff;
    i {
      font-size: 14px;
    }
  }
}
  .cluster-manage-container {
    width: 100%;
    height: 100%;
    color: #333;
    font-size: 14px;
    font-weight: 400;
    .search-container {
      padding-bottom: 8px;
      background: #eff1f6;
      .search-main {
        height: 56px;

        border-bottom: 1px solid #dee2ea;
        box-sizing: border-box;
        background: #ffffff;
        .left-content {
          font-size: 12px;
          color: #2e2c37;
          p {
            margin: 0 8px 0 20px;
          }
        }
        .right-content {
          .product-line {
            margin-right: 16px;
            p {
              margin: 0 8px 0 20px;
            }
          }
        }
      }
    }
    .table-container {
      padding: 12px 16px;
    }
    /deep/ .ant-table-wrapper {
      height: calc(100% - 72px);
      .ant-spin-nested-loading {
        height: 100%;
        .ant-spin-container {
          height: 100%;
          .ant-table {
            height: 100%;
            .ant-table-content {
              height: 100%;
              .ant-table-scroll {
                overflow: hidden;
                height: 100%;
                .ant-table-body {
                  height: 100%;
                }
              }
            }
          }
        }
      }
    }
    .tale-data {
      height: calc(100% - 72px);
      // overflow-y: scroll;
      /deep/ .ant-table-thead > tr > th {
        padding: 10.5px 16px;
        font-weight: 700;
        font-size: 12px;
      }
      /deep/ .ant-table-tbody > tr > td {
        padding: 6px 16px;
      }
    }
    .use-count,
    .operation {
      // font-size: 14px;
      font-weight: 400;
      color: #0066ff;
      .exist {
        cursor: pointer;
        i {
          font-size: 18px !important;
        }
      }
      .noexit {
        color: #ccc;
        cursor: default;
      }
      .delete {
        cursor: pointer;
      }
      i {
        margin-right: 6px;
      }
    }
    .refer-user-count {
      // font-size: 14px;
      font-weight: 400;
      color: #0066ff;
      cursor: pointer;
    }
    .creation-date {
      // font-size: 14px;
      // color: #ccc;
    }
    .operation {
      span {
        margin-right: 16px;
        cursor: pointer;
      }
    }
    .footer-page {
      height: 72px;
      padding: 20px 16px;
    }
  }
</style>
