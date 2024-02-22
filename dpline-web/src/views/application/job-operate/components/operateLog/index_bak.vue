<!--
 * @Author: hjg
 * @Date: 2021-11-08 21:32:02
 * @LastEditTime: 2022-01-13 17:18:42
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \bigdata-sdp-frontend2\src\views\application\job-operate\components\operateLog\index.vue
-->
<template>
  <div class="operate-log">
    <chitu-table class="table-data"
                 v-loading="isLoading"
                 :columns="columns"
                 :dataSource="tableData"
                 :loading="loading"
                 row-key="id"
                 :pagination="pagination"
                 @pageChange="pageChange"
                 @pageSizeChange="pageSizeChange">


      <template #operateType="{text,record}">
        <div slot="operateType"
             :class="record.operateType"
        >
          {{ record.operateType }}
        </div>
      </template>

      <template #operateLogPath="{text,record}">
<!--        <div slot="operateLogPath"-->
<!--             class="rule-name">-->
<!--        <span style="color:blue; cursor:pointer; text-decoration: underline"-->
<!--              @click="openLogView(record)">{{ record.operateLogPath }}</span>-->
<!--        </div>-->
        <div slot="operateLogPath"
             class="rule-name">
          <a-tooltip placement="topLeft">
            <template slot="title">
              <span>{{ record.operateLogPath }}</span>
            </template>
            <div style="color:blue; cursor:pointer; text-decoration: underline"
                 @click="openLogView(record)">{{record.operateLogPath}}</div>
          </a-tooltip>
        </div>
      </template>

      <template #operation="{record}">
        <div class="operation">
          <span style="color:blue; cursor:pointer; text-decoration: underline"
                @click="handleDeleteAlert(record)">
              <i class="chitutree-h5 chitutreeshanchu"></i>删除
            </span>
        </div>
      </template>
    </chitu-table>

<!--    <a-table class="tale-data"-->
<!--             v-loading="isLoading"-->
<!--             v-defaultPage="!tableData || (tableData && tableData.length === 0)"-->
<!--             row-key="id"-->
<!--             ref="operateLogTable"-->
<!--             :columns="columns"-->
<!--             :data-source="tableData"-->
<!--             :loading="loading"-->
<!--             :pagination="false"-->
<!--             :scroll="{y: 'calc(100% - 55px)'}"-->
<!--             @change="handleTableChange">-->
<!--      <div slot="operateLogPath"-->
<!--           class="rule-name"-->
<!--           slot-scope="text,record">-->
<!--        <span style="color:blue; cursor:pointer; text-decoration: underline"-->
<!--              @click="openLogView(record)">{{ record.operateLogPath }}</span>-->
<!--      </div>-->
<!--    </a-table>-->
    <!-- 分页 -->
<!--    <div class="footer-page">-->
<!--      <Pagination :pagination="pagination"-->
<!--                  @pageChange="pageChange"-->
<!--                  @pageSizeChange="pageSizeChange" />-->
<!--    </div>-->
    <div>
      <view-dialog ref="viewDialog"/>
    </div>
<!--    <tip-dialog :visible="popConfirm"-->
<!--                type="warning"-->
<!--                title="失败原因"-->
<!--                @close="popConfirm=false"-->
<!--                @confirm="popConfirm=false">-->
<!--      <div>{{errMessage}}</div>-->
<!--    </tip-dialog>-->
  </div>
</template>
<script>
  // import Pagination from '@/components/pagination/index'
  // import tableSort from '../../../../../mixins/table-sort'
  import viewDialog from '../viewDialog'
  // import TipDialog from '@/components/tip-dialog'

  export default {
    name: 'operateLog',
    components: {
      // Pagination,
      viewDialog
      // TipDialog
    },
    // mixins: [tableSort],
    computed: {
      columns () {
        return [{
          title: '操作事件',
          key: 'operateType',
          dataIndex: 'operateType',
          scopedSlots: { customRender: 'operateType' },
          width: 50
        },
        {
            title: '操作日志',
            key: 'operateLogPath',
            dataIndex: 'operateLogPath',
            scopedSlots: { customRender: 'operateLogPath' },
            width: 350
        },
          {
            title: '操作人',
            key: 'createUser',
            dataIndex: 'createUser',
            scopedSlots: { customRender: 'createUser' },
            width: 50
          },
        {
          title: '操作时间',
          key: 'createTime',
          dataIndex: 'createTime',
          defaultSortOrder: 'descend',
          width: 100,
          sorter: () => this.handleTableChange,
          scopedSlots: { customRender: 'createTime' },
          // sortOrder: this.sortedInfo.columnKey === 'createTime' && this.sortedInfo.order
        },
          {
            title: '操作',
            dataIndex: 'operation',
            scopedSlots: { customRender: 'operation' },
            width: 40
          }
        ]
      }
    },
    data () {
      return {
        isLoading: false,
        // errMessage: '',
        popConfirm: false,
        tableData: [],
        loading: false,
        tableSortData: {
          columnsName: 'columns',
          ref: 'operateLogTable'
        },
        pagination: {
          current: 1,
          showSizeChanger: true,
          showQuickJumper: true,
          defaultPageSize: 20,
          total: 0,
          pageSizeOptions: ['10', '20', '40', '60']
        },
        params: {
          orderByClauses: [{
            field: 'create_time',
            orderByMode: 1 //排序模式（0：正序，1：倒序）
          }],
          jobId: null,
          page: 1,
          pageSize: 20
        }
      }
    },
    props: {
      activeName: {
        type: String,
        value: ''
      }
    },
    watch: {
      activeName: {
        async handler (val) {
          if (val === 'operateLog') {
            this.init()
          }
        },
        immediate: true
      }
    },
    created () {

    },
    methods: {
      init () {
        this.getOperateLogList(this.params)
      },
      // popTip (record) {
      //   this.popConfirm = true
      //   this.errMessage = record.message
      // },
      // 排序，筛选变化时触发
      handleTableChange (pagination, filters, sorter) {
        this.resetSortMethods(sorter)
        sorter = this.sortedInfo
        if (sorter.order) {
          if (sorter.order === 'ascend') {
            this.params.orderByClauses[0].orderByMode = 0
          } else {
            this.params.orderByClauses[0].orderByMode = 1
          }
        }
        this.getOperateLogList(this.params)
      },
      handleDeleteAlert(record){
        console.log(record)
      },
      // 分页数据变化
      pageChange (pageInfo) {
        // console.log('---------pageChange: ', pageInfo.page, pageInfo.pageSize)
        this.params.page = pageInfo.page
        this.getOperateLogList(this.params)
      },
      openLogView(record){
        if(record.operateLogPath !== null || record.operateLogPath !== ''){
          this.$refs.viewDialog.handleSeeLog(record)
        } else {
          this.$message.error("尚未部署，请点击部署")
        }

      },
      // pageSize变化回调
      pageSizeChange (pageSizeInfo) {
        // console.log('---------pageSizeChange: ', pageSizeInfo.current, pageSizeInfo.size)
        this.params.page = pageSizeInfo.current
        this.params.pageSize = pageSizeInfo.size
        this.getOperateLogList(this.params)
      },
      // 获取操作日志列表
      async getOperateLogList (params) {
        params.jobId = Number(this.$store.getters.jobInfo.id)
        this.isLoading = true
        let res = await this.$http.post('/log/searchOperateLog', params)
        this.isLoading = false
        if (res.code === 200) {
          this.tableData = res.data.rows
          this.resetPagination(res.data)
        }
      },
      // 重置分页信息
      resetPagination (pageInfo) {
        this.pagination.current = pageInfo.page
        this.pagination.total = pageInfo.rowTotal
      }
    }
  }
</script>
<style lang="scss" scoped>

  .operate-log {
    width: 100%;
    height: 100%;
    .STOP {
      background-color: #ffe0e0;
      display: flex;
      justify-content: center;
      align-items: center;
      color: #ff1414;
      font-weight: bold;
    }
    .START {
      background-color: #47cb89;
      display: flex;
      font-weight: bold;
      justify-content: center;
      align-items: center;
      color: #ffffff;
      border-radius: 2px;
    }
    .RESTART {
      background: #01c14b;
      border-radius: 2px;
      color: #ffe0e0;
      display: flex;
      justify-content: center;
      align-items: center;
      font-weight: bold;
    }

    .DEPLOY {
      background: #8dbaff;
      border-radius: 2px;
      color: #ffffff;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    .TRIGGER {
      background: #ab90e8;
      border-radius: 2px;
      color: #ffe0e0;
      display: flex;
      justify-content: center;
      align-items: center;
      font-weight: bold;
    }

    .CANCEL {
      background: #f95353;
      border-radius: 2px;
      color: #ffe0e0;
      display: flex;
      justify-content: center;
      align-items: center;
      font-weight: bold;
    }

    .table-data {
      height: calc(100% - 72px);
      overflow-y: hidden;
      /deep/ .ant-table-thead > tr > th {
        padding: 12px 16px;
        font-weight: 700;
        font-size: 12px;
      }
      /deep/ .ant-table-tbody > tr > td {
        padding: 7px;
        font-size: 13px;
        font-family: normal;
      }
    }
    /deep/ .ant-table-wrapper {
      height: calc(100% - 72px);
      .ant-spin-nested-loading {
        height: 100%;
        .ant-spin-container {
          height: 100%;
          .ant-table {
            height: 100%;
            font-size: 10px;
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
    .footer-page {
      height: 72px;
      padding: 20px 16px;
    }
  }
</style>
