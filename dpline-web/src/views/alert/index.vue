<template>
  <div class="alert-manage-container">
    <div class="search-container">
      <div class="search-main justify-between">
        <div class="left-content justify-between">
          <p>版本名称</p>
          <a-auto-complete class="search"
                           size="large"
                           v-model="params.vo.instanceName"
                           placeholder='搜索告警名称...'
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
              <span>新建告警</span>
            </a-button>
          </div>
        </div>
      </div>
    </div>
    <div class="table-container">
      <chitu-table v-loading="isLoading"
                 row-key="id"
                 ref="alertManageTable"
                 :columns="columns"
                 :data-source="alertTableData"
                 @change="handleTableChange"
                 :pagination="pagination"
                 @pageChange="pageChange"
                 @pageSizeChange="pageSizeChange">
        <template #enabledFlag="{record}">
          <span class="operation state" @click="updateState(record)">
            {{ record.enabledFlag === 0 ? "未启用" : "已启用" }}
          </span>
        </template>
        <template #operation="{record}">
          <div class="operation">
            <span @click="openAlertDetail(record)">详情</span>
            <a-divider type="vertical"></a-divider>
            <span class="delete-color"
                  @click="handleDeleteAlert(record)">
              <i class="chitutree-h5 chitutreeshanchu"></i>删除
            </span>
          </div>
        </template>
      </chitu-table>
    </div>

    <confirm-dialog :visible="deleteVisible"
                    type="warning"
                    @close="deleteVisible=false"
                    @confirm="deleteAlertInfo(deleteItem)">
      <template>
        <p class="word-break">确定要<span class="warn-message">&nbsp;删除&nbsp;</span>吗？</p>
      </template>
    </confirm-dialog>
    <!-- 添加alert版本 -->
    <add-alert-dialog ref="dialogVisible" @confirmEvent="confirmEvent" />
    <!-- alert版本详情 -->
    <alert-detail ref="alertDetail" @confirmEvent="confirmEvent"/>
  </div>
</template>

<script>
  import addAlertDialog from '../system-setting/components/addAlertDialog.vue'
  import alertDetail from '../system-setting/components/alertDetail.vue'
  import ConfirmDialog from '@/components/confirm-dialog'
  import tableSort from '@/mixins/table-sort'
  export default {
    name: 'alertManageTable',
    mixins: [tableSort],
    props: {},
    components: {
      addAlertDialog,
      alertDetail,
      ConfirmDialog
    },
    computed: {
      columns () {
        return [
          {
            title: '告警名称',
            width: 120,
            dataIndex: 'instanceName'
          },
          {
            title: '告警类型',
            width: 120,
            dataIndex: 'alertType',
            ellipsis: true
          },
          {
            title: '是否启用',
            width: 80,
            dataIndex: 'enabledFlag',
            scopedSlots: { customRender: 'enabledFlag' }
          },
          {
            title: '创建人',
            width: 120,
            dataIndex: 'createUser',
            ellipsis: true
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
          ref: 'alertManageTable'
        },
        tableSortData: {
          columnsName: 'columns',
          ref: 'alertManageTable'
        },
        alertTableData: [],
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
            instanceName: null
          }
        },
        isShowAlertDialog: false,
        alertInfo: {}
      }
    },
    methods: {
      beforeCreate() {
      },
      created() {

      },
      reset () {
        this.$refs.searchAuto.defaultValue = null
        this.params.vo.alertName = null
        this.search()
      },
      search () {
        this.params.page = 1
        this.getAlertInstanceList(this.params)
      },

      // 更新启用状态
      async updateState(record) {
        if (record == null) {
          return
        }

        const newEnabledFlag = record.enabledFlag === 0 ?  1 : 0;
        const params = {
          id: record.id,
          enabledFlag: newEnabledFlag
        }

        let res = await this.$http.post('/system/alertManagement/updateState', this.$qs.stringify(params))
        if (res.code === 200) {
          this.$message.success({ content: '修改状态成功', duration: 2 });
          record.enabledFlag = newEnabledFlag;
        }else {
          this.$message.error({ content: '修改状态失败', duration: 2 });
        }
      },

      // 输入值变化时搜索补全
      async onChange (value) {
        this.params.vo.alertName = value
        const params = this.params
        let res = await this.$http.post('/system/alertManagement/list', params)
        if (res.code === 200) {
          this.alertTableData = [...res.data.rows]
        }
      },
      showDialogEvent () {
        this.$refs.dialogVisible.isShowDialog = true;
      },

      // 打开alert版本详情
      openAlertDetail (alertInfo) {
        this.$refs.alertDetail.open(JSON.parse(JSON.stringify(alertInfo)));
      },

      handleDeleteAlert (item) {
        this.deleteItem = item
        this.deleteVisible = true
      },
      // 删除
      async deleteAlertInfo (alertInfo) {
        const params = { id: Number(alertInfo.id) }
        let res = await this.$http.post('/system/alertManagement/delete', this.$qs.stringify(params))
        if (res.code === 200) {
            this.$message.success({ content: '删除成功', duration: 2 });
            this.getAlertInstanceList(this.params);
        } else {
          this.$message.error(res.msg)
        }
      },
      // 重置分页信息
      resetPagination (pageInfo) {
        this.pagination.current = pageInfo.page
        this.pagination.total = pageInfo.rowTotal
      },
      // engineUserConfirm (data) {
      //   if (data) this.getAlertInstanceList(this.params)
      // },
      // engineUserCancel () {
      //   this.getAlertInstanceList(this.params)
      // },
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
          this.getAlertInstanceList(this.params)
        }
      },
      // 分页数据变化
      pageChange (pageInfo) {
        this.params.page = pageInfo.page
        this.getAlertInstanceList(this.params)
      },
      // pageSize变化回调
      pageSizeChange (pageSizeInfo) {
        this.params.page = pageSizeInfo.current
        this.params.pageSize = pageSizeInfo.size
        this.getAlertInstanceList(this.params)
      },
      // 获取列表
      async getAlertInstanceList (params) {
        this.isLoading = true;
        let res = await this.$http.post('/system/alertManagement/list', params);
        this.isLoading = false
        if (res.code === 200 && res.data != null) {
          this.alertTableData = [...res.data.rows]
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
          this.getAlertInstanceList(this.params)
        }
      }
    },
    mounted () {

    },
    created () {
      this.getAlertInstanceList(this.params)
    }
  }
</script>

<style lang="scss" scoped>
  .alert-manage-container {
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
