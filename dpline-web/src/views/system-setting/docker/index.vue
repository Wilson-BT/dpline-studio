<template>
  <div class="docker-manage-container">
    <div class="search-container">
      <div class="search-main justify-between">
        <div class="left-content justify-between">
          <p>版本名称</p>
          <a-auto-complete class="search"
                           size="large"
                           v-model="params.vo.imageName"
                           placeholder='搜索镜像名称...'
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
              <span>新建版本</span>
            </a-button>
          </div>
        </div>
      </div>

    </div>
    <div class="table-container">
      <chitu-table v-loading="isLoading"
                   row-key="id"
                   ref="flinkManageTable"
                   :columns="columns"
                   :data-source="dockerImageList"
                   @change="handleTableChange"
                   :pagination="pagination"
                   @pageChange="pageChange"
                   @pageSizeChange="pageSizeChange">
        <template #enabledFlag="{record}">
          <span class="operation state" @click="updateState(record)">
            {{ record.enabledFlag == 0 ? "未启用" : "已启用" }}
          </span>
        </template>
        <template #operation="{record}">
          <div class="operation">
            <span @click="openDockerDetail(record)">详情</span>
            <a-divider type="vertical"></a-divider>
            <span class="delete-color"
                  @click="handleDeleteDocker(record)">
              <i class="chitutree-h5 chitutreeshanchu"></i>删除
            </span>
          </div>
        </template>
      </chitu-table>
    </div>

    <confirm-dialog :visible="deleteVisible"
                    type="warning"
                    @close="deleteVisible=false"
                    @confirm="deleteDockerInfo(deleteItem)">
      <template>
        <p class="word-break">确定要<span class="warn-message">&nbsp;删除&nbsp;</span>吗？</p>
      </template>
    </confirm-dialog>
    <!-- 添加flink版本 -->
    <add-docker-dialog ref="dialogVisible" @confirmEvent="confirmEvent" />
    <!-- flink版本详情 -->
    <docker-detail ref="dockerDetail" @confirmEvent="confirmEvent"/>
  </div>
</template>

<script>
import addDockerDialog from '../components/addDockerDialog.vue'
import dockerDetail from '../components/dockerDetail.vue'
import ConfirmDialog from '@/components/confirm-dialog'
import tableSort from '@/mixins/table-sort'
export default {
  name: 'dockerManageTable',
  mixins: [tableSort],
  props: {},
  components: {
    addDockerDialog,
    dockerDetail,
    ConfirmDialog
  },
  watch: {
    '$store.getters.isRemoveTag': {//监听关闭页签，关闭页签后清除缓存
      handler (val, oldVal) {
        if (val === oldVal) {
          return
        }
        if (this.$store.getters.removeRouteName.includes('SystemSettingVersion')) {
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
          title: '镜像名称',
          width: 120,
          dataIndex: 'shortName'
        },
        {
          title: '镜像地址',
          width: 200,
          dataIndex: 'imageName',
          ellipsis: true
        },
        {
          title: 'HTTP 地址',
          width: 120,
          dataIndex: 'registerAddress',
          ellipsis: true
        },
        {
          title: '用户名',
          width: 120,
          dataIndex: 'registerUser',
          ellipsis: true
        },
        // {
        //   title: '是否启用',
        //   width: 80,
        //   dataIndex: 'enabledFlag',
        //   scopedSlots: { customRender: 'enabledFlag' }
        // },
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
        ref: 'flinkManageTable'
      },
      tableSortData: {
        columnsName: 'columns',
        ref: 'flinkManageTable'
      },
      dockerImageList: [],
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
          shortName: null
        }
      },
      isShowFlinkDialog: false,
      flinkInfo: {}
    }
  },
  methods: {
    beforeCreate() {
    },
    created() {

    },
    reset () {
      this.$refs.searchAuto.defaultValue = null
      this.params.vo.shortName = null
      this.search()
    },
    search () {
      this.params.page = 1
      this.getDockerVersionList(this.params)
    },

    // 更新启用状态
    async updateState(record) {
      if (record == null) {
        return
      }
      record.enabledFlag === 0 ? record.enabledFlag = 1 : record.enabledFlag = 0;
      let res = await this.$http.post('/system/docker/update', record)
      if (res.code === 200) {
        this.$message.success({ content: '修改状态成功', duration: 2 });
      }
    },

    // 输入值变化时搜索补全
    async onChange (value) {
      this.params.vo.shortName = value
      const params = this.params
      let res = await this.$http.post('/system/docker/list', params)
      if (res.code === 200) {
        this.dockerImageList = [...res.data.rows]
      }
    },
    showDialogEvent () {
      this.$refs.dialogVisible.isShowDialog = true;
    },

    // 打开flink版本详情
    openDockerDetail (dockerImageInfo) {
      this.$refs.dockerDetail.open(JSON.parse(JSON.stringify(dockerImageInfo)));
    },
    handleDeleteDocker (item) {
      this.deleteItem = item
      this.deleteVisible = true
    },
    // 删除
    async deleteDockerInfo (value) {
      const params = { id: value.id }
      let res = await this.$http.post('/system/docker/delete', params)
      if (res.code === 200) {
        if (res.data > 0) {
          this.getDockerVersionList(this.params);
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
    engineUserConfirm (data) {
      if (data) this.getDockerVersionList(this.params)
    },
    engineUserCancel () {
      this.getDockerVersionList(this.params)
    },
    // 排序，筛选变化时触发
    handleTableChange (pagination, filters, sorter) {
      let sortObj = {}
      this.params.orderByClauses = []
      this.resetSortMethods(sorter)
      sorter = this.sortedInfo
      if (sorter.order) {
        if (sorter.field === 'createTime') {
          sortObj.field = 'create_time'
        }
        if (sorter.order === 'ascend') {
          sortObj.orderByMode = 0
        } else {
          sortObj.orderByMode = 1
        }
        this.params.orderByClauses.push(sortObj)
        this.getDockerVersionList(this.params)
      }
    },
    // 分页数据变化
    pageChange (pageInfo) {
      this.params.page = pageInfo.page
      this.getDockerVersionList(this.params)
    },
    // pageSize变化回调
    pageSizeChange (pageSizeInfo) {
      this.params.page = pageSizeInfo.current
      this.params.pageSize = pageSizeInfo.size
      this.getDockerVersionList(this.params)
    },
    // 获取列表
    async getDockerVersionList (params) {
      this.isLoading = true;
      let res = await this.$http.post('/system/docker/list', params);
      this.isLoading = false
      if (res.code === 200 && res.data != null) {
        this.dockerImageList = [...res.data.rows]
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
        this.getDockerVersionList(this.params)
      }
    }
  },
  mounted () {

  },
  created () {
    this.getDockerVersionList(this.params)
  }
}
</script>

<style lang="scss" scoped>
.docker-manage-container {
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
  .state {
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
</style>
