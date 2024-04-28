<template>
  <div class="project-container">
    <div class="search-container">
      <div class="search-main justify-between">
        <div class="left-content justify-between">
          <p>资源计数<span class="sum">{{jarTotal}}</span></p>
          <a-divider type="vertical" />
<!--          <p>引用作业总数<span class="sum">{{referenceJobsTotal}}</span></p>-->
        </div>
        <div class="right-content justify-end">
          <div class="justify-start product-line">
            <p>JAR包名称</p>
            <search-autocomplete ref="searchRef"
                                 :jarAuthType="this.jarAuthType"
                                 autoMsg="请输入jar包名称"
                                 @search="search" />
            <a-button @click="query"
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
        </div>
      </div>

    </div>
    <div class="group-button">
      <a-select
          v-model="jarTypeLabel"
          class="primary group-select"
          @change="onSelect"
      >
        <!--          :default-value="jarTypeList[0]"-->
        <a-select-option v-for="(item) in jarTypeList"
                         :value="item.value"
                         :key="item.label">
          {{ item.label }}
        </a-select-option>
      </a-select>
      <a-button @click="add"
                class="add-button"
                type="primary"
                size="small"
                icon="plus">
        新增资源
      </a-button>
    </div>
    <!-- <div class="project-top">
      <div class="sub-project-top justify-between">
        <div class="total justify-start">
          <i class="chitutree-h5 chitutreesidenav_ziyuanguanli"></i>资源管理
        </div>
        <div class="right justify-end">
          <div class="search">
            <search-autocomplete @search="search" />
          </div>
          <p @click="add"
             class="new blue-bg"><i class="chitutree-h5 chitutreexinzeng"></i>新增jar</p>
        </div>
      </div>
    </div>
    <div class="data-cpount">
      <p class="item">Jar计数<span>{{jarTotal}}</span></p>
      <a-divider type="vertical" />
      <p class="item">引用作业总数<span>{{referenceJobsTotal}}</span></p>
    </div> -->
    <div class="project-list">
      <div class="sub-list">
        <chitu-table v-loading="isLoading"
                   :columns="columns"
                   rowKey="id"
                   :data-source="dataList"
                   @change="handleChange"
                   :pagination="pagination"
                   @pageChange="pageChange"
                   @pageSizeChange="pageSizeChange">
          <template #name="{record}">
            <span class="name">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.name}}</span>
                </template>
                <span>{{record.name}}</span>
              </a-tooltip>
            </span>
          </template>
          <template #description="{record}">
            <span class="description">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.description}}</span>
                </template>
                <span>{{record.description}}</span>
              </a-tooltip>
            </span>
          </template>
          <template #jarFunctionType="{record}">
            <span class="description">
              <a-tooltip placement="topLeft">
                <template slot="title">
                  <span>{{record.jarFunctionType}}</span>
                </template>
                <span>{{record.jarFunctionType}}</span>
              </a-tooltip>
            </span>
          </template>
<!--          <template #jarVersion="{record}">-->
<!--            <span class="description">-->
<!--              <a-tooltip placement="topLeft">-->
<!--                <template slot="title">-->
<!--                  <span>{{record.jarVersion}}</span>-->
<!--                </template>-->
<!--                <span>{{record.jarVersion}}</span>-->
<!--              </a-tooltip>-->
<!--            </span>-->
<!--          </template>-->
          <template #createTime="{record}">
            <span class="date">
              {{record.createTime}}
            </span>
          </template>

          <template #motorRealVersion="{record}">
            <span class="runMotorType">
              {{record.runMotorType}}
            </span>
          </template>
<!--          <template #motorRealVersion="{record}">-->
<!--            <span class="motorRealVersion">-->
<!--              {{record.motorRealVersion}}-->
<!--            </span>-->
<!--          </template>-->
<!--          关联任务数-->
<!--          <template #jobs="{record}">-->
<!--            <span class="jobs blue"-->
<!--                  @click="viewJobs(record)">-->
<!--              {{record.jobs}}-->
<!--            </span>-->
<!--          </template>-->
          <template #operate="{record}">
            <div class="common-action-container">
              <a-button type="link"
                        @click="update(record)">更新</a-button>
              <a-divider type="vertical" />
<!--              <a-button type="link"-->
<!--                        @click="reload(record)"><i class="chitutree-h5"></i>上传版本</a-button>-->
              <a-divider type="vertical" />
              <a-button type="link"
                        @click="gotoAllVersion(record)">全部版本</a-button>
              <a-divider type="vertical" />
              <a-button type="link"
                        class=" delete-color delete-btn"
                        @click="remove(record)"><i class="chitutree-h5 chitutreeshanchu"></i>全部删除</a-button>
            </div>
          </template>

        </chitu-table>
      </div>
    </div>
    <add-main-resource ref="addMainResource"
             :jarAuthType="this.jarAuthType"
             @addSuccess="addSuccess"
             @confirm="jarConfirm"
             @cancel="jarCancel" />
<!--    <reference-jobs ref="referenceJobs" />-->
  </div>
</template>

<script>
//新增:
// type: "add",
//     versionType: "primary",
//再次上传:
// type: "reload",
//     versionType: "primary"
// 更新主版本
// type: "update",
//     versionType: "primary"
// 更新历史版本
// type: 'update',
//     versionType: 'history',

import SearchAutocomplete from "./components/search-autocomplete";
// import jarTypeSnippet from "./jar-type-snippet.vue";
import AddMainResource from "./components/add-main-resource";
// import ReferenceJobs from "./components/referenceJobs";
// import {downloadByData} from "../../../utils";
import {jarTypeList,adminTypeList} from '@/utils/enumType'

const columns = [
  {
    dataIndex: "name",
    key: "name",
    title: "资源名称",
    scopedSlots: { customRender: "name" },
    width: 100,
  },

    {
      dataIndex: "description",
      key: "description",
      title: "描述",
      scopedSlots: { customRender: "description" },
      width: 100,
    },
    {
      dataIndex: "jarFunctionType",
      key: "jarFunctionType",
      title: "类型",
      scopedSlots: { customRender: "jarFunctionType" },
      width: 100,
    },
    {
      title: "创建时间",
      dataIndex: "createTime",
      key: "createTime",
      scopedSlots: { customRender: "createTime" },
      width: 150,
      defaultSortOrder: "descend",
      sortDirections: ["ascend", "descend", "ascend"],
      sorter: (a, b) => a.createTime - b.createTime,
    },
  {
    title: "引擎类型",
    key: "runMotorType",
    dataIndex: "runMotorType",
    scopedSlots: { customRender: "runMotorType" },
    width: 100,
  },
    {
      title: "操作",
      key: "operate",
      fixed: "right",
      dataIndex: "operate",
      scopedSlots: { customRender: "operate" },
      width: 215,
    },
  ];

  export default {
    data () {
      return {
        oldProjectId: '',
        jarTypeList: jarTypeList,
        jarTypeLabel: '',
        sourceName: "",
        isLoading: false,
        dataList: [],
        columns,
        isShowMemberDialog: false,
        jarAuthType: "project",
        // jarFunctionTypeList: jarFunctionTypeList,
        isNew: true,
        jarFunctionType: '',
        projectDialog: {
          title: "",
        },
        page: 1,
        pagination: {
          current: 1,
          showSizeChanger: true,
          showQuickJumper: true,
          defaultPageSize: 20,
          total: 0,
        },
        referenceJobsTotal: 0,
        jarTotal: 0,
        order: 1,
      };
    },
    components: {
      SearchAutocomplete,
      AddMainResource,
      // ReferenceJobs,
      // jarTypeSnippet
    },
    created () {
      this.remote()
      this.oldProjectId = this.$route.query.projectId
    },
    watch: {
      $route: {//需要同时监听切换项目和切换页签时的路由变化，beforeRouteEnter和activated只能监听到切换页签，不能监听到项目切换
        handler () {
          // 如果
          if (this.$route.query.projectId !== this.oldProjectId && this.$route.name === 'SourceManage') {
              Object.assign(this.$data, this.$options.data(this))
              this.oldProjectId = this.$route.query.projectId
              this.jarAuthType = this.$route.params.jarAuthType || 'project'
            this.remote()
          }
        },
        deep: true,
        // immediate: true
      },
      '$store.getters.isRemoveTag': {//监听关闭页签，关闭页签后清除缓存
        handler (val, oldVal) {
          if (val === oldVal) {
            return
          }
          if (this.$store.getters.removeRouteName.includes('SourceManage')) {
            this.$common.toClearCache(this);
          }
        }
      }
    },

    mounted () {
    },
    methods: {
      onSelect(value){
        const jarType = this.jarTypeList.find((item) => item.value === value)
        this.jarTypeLabel = jarType.label;
        this.changeJarAuthType(value)
      },
      resetData () {
        this.pagination.defaultPageSize = 20
        this.page = 1;
        this.pagination.current = 1;
        this.sourceName = ''
        this.order = 1
        // this.referenceJobsTotal = 0
        this.jarTotal = 0
        this.dataList = []
        this.$refs.searchRef.keyword = ''
        this.$refs.searchRef.jarAuthType =''
      },
      remote(){
        const jarType = this.jarTypeList.find((item) => item.value === this.jarAuthType)
        this.jarTypeLabel = jarType.label;
        this.getList()
      },
      init () {
        this.getList();
        // this.referenceJobs();
      },
      search (value) {
        this.sourceName = value;
      },
      query () {
        this.pagination.current = 1;
        this.getList();
      },
      reset () {
        this.sourceName = "";
        this.$refs.searchRef.keyword = "";
        this.$refs.searchRef.jarAuthType = "";
        this.pagination.current = 1;
        this.getList();
      },
      async getList () {
        // console.log(this.jarAuthType)
        const params = {
          orderByClauses: [
            {
              field: "create_time",
              orderByMode: this.order,
            },
          ],
          page: this.page,
          pageSize: this.pagination.defaultPageSize,
          vo: {
            jarAuthType: this.jarAuthType,
            name: this.sourceName,
            projectId: Number(this.$route.query.projectId),
          },
        };
        this.dataList = [];
        this.pagination.total = 0;
        this.isLoading = true;
        if(Number(this.$route.query.projectId) === null && this.jarAuthType === 'project'){
            this.$message.error("项目资源缺少参数:[projectId]")
            return
        }
        let res = await this.$http.post("/jar/listMainResource", params);
        this.isLoading = false;
        if (res.code === 200) {
          if (res.data) {
            this.pagination.total = res.data.rowTotal;
            if (!this.sourceName) {
              this.jarTotal = res.data.rowTotal;
            }
            if (res.data.rows) {
              // rows = rows.map((item, index) => {
              //   item.key = index
              //   return item
              // })
              this.dataList = res.data.rows;
            } else {
              this.dataList = [];
            }
          }
        } else {
          this.$message.error(res.msg);
        }
      },
      //切换条件
      changeJarAuthType (value) {
        if (value === this.jarAuthType){
          return;
        }
        this.jarAuthType = value;
        this.getList();
      },
      // async referenceJobs () {
      //   const params = {
      //     jarName: "",
      //     projectId: Number(this.$route.query.projectId),
      //     jarAuthType: this.jarAuthType,
      //   };
      //   let res = await this.$http.post("/jar/referenceJobs", params);
      //   if (res.code === 200) {
      //     if (res.data) {
      //       this.referenceJobsTotal = res.data.total;
      //     }
      //   } else {
      //     this.$message.error(res.msg);
      //   }
      // },
      popMemberDialog (item) {
        this.$refs.memberDialog.open(item);
      },
      allowUpdate(){
        const userInfo = JSON.parse(localStorage.getItem("userInfo"))
        const admin = adminTypeList.find(item => item.label === userInfo.isAdmin);
        return admin !== undefined || this.jarAuthType === "project";
      },
      add () {
        const obj = {
          type: "add",
          data: null,
          title: "新增主资源",
        };
        // 检查权限
        const allowUpdate = this.allowUpdate()
        if(allowUpdate){
          this.$refs.addMainResource.open(obj);
        } else {
          this.$message.error("用户无权限更新资源");
        }
      },
      update (item) {
        const obj = {
          type: "update",
          data: item,
          title: "编辑：" + item.name,
        };
        this.$refs.addMainResource.open(obj);
      },
      // viewJobs (data) {
        // this.$refs.referenceJobs.open(data);
      // },
      remove (item) {
        var onOk = async function () {
          const params = {
            mainResourceId: item.id
          }
          console.log(params)
          let res = await this.$http.post("/jar/deleteWholeResource", params);
          if (res.code === 200) {
            this.$message.success("删除成功");
            this.getList();
          } else {
            this.$message.error(res.msg);
          }
        }.bind(this);
        this.$confirm({
          title: "确定要全部删除吗?",
          content: "",
          okText: "确认",
          cancelText: "取消",
          onOk: onOk,
        });
      },

      jarConfirm () { },
      jarCancel () { },

      // 分页数据变化
      pageChange (pageInfo) {
        this.page = pageInfo.page;
        this.getList();
      },
      // pageSize变化回调
      pageSizeChange (pageSizeInfo) {
        this.pagination.defaultPageSize = pageSizeInfo.size;
        this.page = 1;
        this.pagination.current = 1;
        this.getList();
      },
      addSuccess () {
        this.page = 1;
        this.pagination.current = 1;
        this.getList();
      },
      handleChange (pagination, filters, sorter) {
        if (sorter.order === "ascend") {
          this.order = 0;
          this.getList();
        } else if (sorter.order === "descend") {
          this.order = 1;
          this.getList();
        }
      },
      gotoAllVersion(item) {
        // console.log(item.runMotorType)
        this.$router.push({
          name: "SourceManage_allVersion",
          query: {
            //预留query
            projectId: Number(this.$route.query.projectId),
            projectName: this.$route.query.projectName,
            mainResourceId: Number(item.id),
            name: item.name
          },
          params:{
            // mainResourceId: Number(item.id),
            jarAuthType: this.jarAuthType,
            runMotorType: item.runMotorType
          }
        });
      },
      // gitReplace (url) {
      //   url = url.replace("xxx", "...");
      //   return url;
      // },
      // async download (record) {
      //   const params = {
      //     jarName: record.jarName,
      //     projectId: Number(this.$route.query.projectId),
      //     id: record.id
      //     // version: "v1"
      //   };
      //   this.isLoading = true;
      //   //blob方式
      //   let res = await this.$http.post("/jar/download", params, {
      //     headers: {
      //       projectId: Number(this.$route.query.projectId),
      //     },
      //     responseType: "blob",
      //   });
      //   // console.log('res', res)
      //   if (res) {
      //     if (res.msg) {//有msg说明文件有问题，不能下载
      //       this.$message.error(res.msg);
      //       this.isLoading = false;
      //       return;
      //     }
      //     downloadByData(res.data, record.jarName);
      //     this.isLoading = false;
      //   }
      // },
    },
  };
</script>

<style lang="scss" scoped>
  .project-container {
    height: 100%;
    .guide-component {
      width: 100%;
      height: 100%;
    }
    .search-container {
      padding-bottom: 8px;
      background: #eff1f6;
      .search-main {
        height: 56px;
        padding: 0 16px;
        border-bottom: 1px solid #dee2ea;
        box-sizing: border-box;
        background: #ffffff;
        .left-content {
          font-size: 13px;
          margin-left: 18px;
          .sum {
            margin-left: 12px;
            font-size: 16px;
            font-weight: 900;
            color: #0066ff;
          }
        }
        .right-content {
          .product-line {
            margin-right: 16px;
            p {
              margin: 0 8px 0 10px;
            }
          }
          .productLine {
            /deep/ .ant-select-selection--single {
              height: 28px;
              .ant-select-selection__rendered {
                line-height: 28px;
              }
            }
          }
        }
      }
    }
    .group-button {
      padding: 10px 16px;
      .add-button{
        //vertical-align:bottom;
        margin-left:12px;
      }
      .group-select {
        vertical-align:bottom;
        margin-left: 12px;
        width: 90px;height: 28px;
        /deep/ .ant-select-selection--single {
          height:28px;
          background-color: #006EFF;
          color: #fff;
          border: 17px;
          .ant-select-selection__rendered {
            line-height: 28px;
          }
          .ant-select-arrow-icon {
            color: #fff;
          }
        }
      }
    }

    // .project-top {
    //   height: 41px;
    //   font-size: 14px;
    //   border-bottom: 1px solid #d9d9d9;
    //   .sub-project-top {
    //     height: 100%;
    //     padding: 0 16px;
    //     margin: 0 auto;
    //     .total {
    //       font-size: 16px;
    //       font-weight: bold;
    //       /deep/ i {
    //         font-size: 16px !important;
    //         margin-right: 10px;
    //       }
    //       .item {
    //         margin-right: 20px;
    //         color: rgb(71, 66, 66);
    //         font-size: 16px;
    //         &.member {
    //           i {
    //             margin-right: 8px;
    //           }
    //         }
    //       }
    //     }
    //     .right {
    //       height: 38px;
    //       overflow: hidden;
    //       .new {
    //         width: 92px;
    //         height: 28px;
    //         text-align: center;
    //         line-height: 28px;
    //         color: #fff;
    //         font-size: 12px;
    //         cursor: pointer;
    //         margin-top: -2px;
    //         i {
    //           font-size: 12px;
    //           margin-right: 7px;
    //         }
    //       }
    //     }
    //   }
    // }
    // .data-cpount {
    //   height: 40px;
    //   display: flex;
    //   align-items: center;
    //   padding-left: 40px;
    //   .item {
    //     font-size: 12px;
    //     color: #666;
    //     span {
    //       margin: 0 10px;
    //       font-weight: bold;
    //     }
    //   }
    // }
    .project-list {
      // height: calc(100% - 41px - 40px);
      padding: 0 16px;
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
      .sub-list {
        height: 100%;

        .jobs {
          cursor: pointer;
        }
        // /deep/ .ant-table-thead > tr > th {
        //   background: #f9f9f9;
        //   padding: 18px 16px;
        //   color: #333;
        //   font-size: 12px;
        //   border-bottom: 1px solid #f9f9f9;
        // }
        // /deep/ .ant-table-tbody > tr > td {
        //   border-bottom: 1px solid #f9f9f9;
        //   color: #333;
        //   font-size: 14px;
        // }
        /deep/ .name {
          cursor: pointer;
        }

        /deep/ .member {
          cursor: pointer;
        }
        /deep/ .date {
          color: #999;
        }
        /deep/ .operate {
          i {
            margin-right: 3px;
          }
          .ant-btn-link {
            padding-left: 4px;
            padding-right: 4px;
          }
          .delete-btn {
            padding-left: 0 !important;
            &:hover {
              color: #f95353 !important;
            }
          }
        }
        .footer-page {
          height: 72px;
          padding: 20px 16px;
        }
      }
    }
  }
</style>
