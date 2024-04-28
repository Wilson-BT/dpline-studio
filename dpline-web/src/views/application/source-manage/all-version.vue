<template>
  <div class="project-container">
    <div class="project-top">

      <div class="sub-project-top justify-start">
        <p class="blue jar-name"
           @click="back"><i class="chitutree-h5 chitutreefanhui "></i>{{name}}</p>
        <div class="total justify-start">
          <p class="item">Jar计数 : {{jarTotal}}</p>
          <p class="item">引用作业总数 : {{jobTotal}}</p>
        </div>
        <div class="total justify-end">
          <a-button @click="add"
                    class="add-button"
                    type="primary"
                    size="small"
                    icon="plus">
            新增jar
          </a-button>
        </div>

      </div>


    </div>
    <div class="project-list">
      <div class="sub-list">
        <chitu-table v-loading="isLoading"
                   :columns="columns"
                   :data-source="dataList"
                   rowKey="id"
                   @change="handleChange"
                   :pagination="pagination"
                   @pageChange="pageChange"
                   @pageSizeChange="pageSizeChange">
          <template #jarName="{record}">
            <span class="jarName">
              {{record.jarName}}
            </span>
          </template>
          <template #motorRealVersion="{record}">
            <span class="motorRealVersion">
              {{record.motorRealVersion}}
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
          <template #updateTime="{record}">
            <span class="date">
              {{record.updateTime}}
            </span>
          </template>
          <template #createUser="{record}">
            <span class="createUser">
              {{record.createUser}}
            </span>
          </template>
<!--          <template #jobs="{record}">-->
<!--            <span class="jobs blue"-->
<!--                  @click="viewJobs(record)">-->
<!--              {{record.jobs}}-->
<!--            </span>-->
<!--          </template>-->
          <template #operate="{record}">
            <div class="common-action-container">
              <a-button type="link"
                        @click="update(record)"><i class="chitutree-h5 "></i>更新</a-button>
              <a-divider type="vertical" />
              <a-button type="link"
                        @click="download(record)">下载</a-button>
              <a-divider type="vertical" />
              <a-button  type="link"
                        class=" delete-color delete-btn"
                        @click="remove(record)"><i class="chitutree-h5 chitutreeshanchu"></i>删除</a-button>
            </div>
          </template>
<!--          <a-button type="link"-->
<!--                    :disabled="!isProdEnv"-->
<!--                    @click="edit(record)"><i class="chitutree-h5 chitutreebianji"></i>更新</a-button>-->
          <!-- <span class="git"
                slot="git"
                slot-scope="record">
            <a class="blue"
               :href="record.git"
               target="_blank"> {{gitReplace(record.git)}}</a>
          </span> -->

        </chitu-table>

      </div>
    </div>
<!--    <reference-jobs ref="referenceJobs" />-->
    <add-jar ref="addJar"
             @addSuccess="addSuccess" />
  </div>
</template>

<script>
  // import ReferenceJobs from './components/referenceJobs'
  import AddJar from './components/add-jar'
  import { downloadByData } from "../../../utils";
  import {adminTypeList} from "@/utils/enumType";

  const columns = [
    {
      dataIndex: 'jarName',
      key: 'jarName',
      title: 'JAR包名称',
      scopedSlots: { customRender: 'jarName' },
      width: 150
    },
    {
      dataIndex: 'motorRealVersion',
      key: 'motorRealVersion',
      title: '引擎版本',
      scopedSlots: { customRender: 'motorRealVersion' },
      width: 60
    },
    {
      dataIndex: 'jarPath',
      key: 'jarPath',
      title: 'JAR包路径',
      scopedSlots: { customRender: 'jarPath' },
      width: 350
    },
    {
      dataIndex: 'description',
      key: 'description',
      title: '版本描述',
      scopedSlots: { customRender: 'description' },
      width: 150
    },
    // {
    //   dataIndex: 'git',
    //   key: 'git',
    //   title: 'git_url',
    //   scopedSlots: { customRender: 'git' },
    //   width: '40%'
    // },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      scopedSlots: { customRender: 'updateTime' },
      width: 100,
      defaultSortOrder: 'descend',
      sortDirections: ['ascend', 'descend', 'ascend'],
      sorter: (a, b) => a.updateTime - b.updateTime
    },
    {
      title: '创建人',
      key: 'createUser',
      dataIndex: 'createUser',
      scopedSlots: { customRender: 'createUser' },
      width: 80
    },
    // {
    //   title: '引用作业',
    //   key: 'jobs',
    //   dataIndex: 'jobs',
    //   scopedSlots: { customRender: 'jobs' },
    //   width: 150
    // },
    {
      title: '操作',
      key: 'operate',
      dataIndex: 'operate',
      scopedSlots: { customRender: 'operate' },
      width: 150
    },
  ];



  export default {
    data () {
      return {
        name:'',
        mainResourceId: '',
        currentTag: {},
        // oldProjectId: '',
        isLoading: false,
        // jarName: '',
        dataList: [],
        columns,
        // isShowMemberDialog: false,
        jarAuthType:'',
        // isNew: true,
        projectDialog: {
          title: ''
        },
        page: 1,
        pagination: {
          current: 1,
          // showSizeChanger: true,
          // showQuickJumper: true,
          defaultPageSize: 20,
          total: 0
        },
        jobTotal: 0,
        jarTotal: 0,
        order: 1
      };
    },
    components: {
      // ReferenceJobs,
      AddJar
    },
    // beforeRouteEnter: (to, from, next) => {
    //   next((vm) => {
    //     // console.log(vm)
    //   })
    // },
    watch: {
      '$store.getters.isRemoveTag': {//监听关闭页签，关闭页签后清除缓存
        handler (val, oldVal) {
          if (val === oldVal) {
            return
          }
          if (this.$store.getters.removeRouteName.includes('SourceManage_allVersion')) {
            this.$common.toClearCache(this);
          }
        }
      }
      // '$route.query.projectId': {
      //   handler () {
      //     console.log(this.$route.query.projectId, typeof this.$route.query.projectId, this.oldProjectId, typeof this.oldProjectId)
      //     if (this.$route.query.projectId && this.$route.query.projectId !== this.oldProjectId) {
      //       this.getCurrentTag()
      //       this.$store.dispatch('tagsView/delView', this.currentTag).then(() => {
      //         // if (this.$route.name === 'SourceManage_allVersion') {//切换项目时，如果停留在当前页，关闭当前页，再跳到资源管理页，如果不在当前页，则关闭当前页面的页签
      //         //   this.$router.push({
      //         //     name: 'SourceManage',
      //         //     query: {
      //         //       projectId: this.$route.query.projectId,
      //         //       projectName: this.$route.query.projectName,
      //         //       projectCode: this.$route.query.projectCode
      //         //     }
      //         //   })
      //         // }
      //       })
      //       this.oldProjectId = this.$route.query.projectId
      //     }

      //   },
      //   deep: true,
      //   // immediate: true
      // }
    },
    computed: {
      // isProdEnv () {
      //   return this.$store.getters.env === 'prod'
      // }
    },
    created () {
      this.mainResourceId = this.$route.query.mainResourceId
      this.jarAuthType = this.$route.params.jarAuthType
      this.oldProjectId = this.$route.query.projectId
      this.runMotorType = this.$route.params.runMotorType
      this.name = this.$route.query.name
      // console.log(this.runMotorType)
      this.getList()
      // this.referenceJobs()
    },
    mounted () {
      this.$bus.$off('changeProject').$on('changeProject', this.changeProject)//切换项目触发
      // setTimeout(() => {
      //   this.getList()
      // }, 5 * 1000)
    },
    methods: {
      add () {
        const obj = {
          type: "add",
          data: null,
          title: "新增jar包资源",
          runMotorType: this.runMotorType,
          mainResourceId: this.mainResourceId
        };
        // 检查权限
        if(this.allowUpdate()){
          this.$refs.addJar.open(obj);
        } else {
          this.$message.error("用户无权限更新资源");
        }
      },
      allowUpdate(){
        const userInfo = JSON.parse(localStorage.getItem("userInfo"))
        const admin = adminTypeList.find(item => item.label === userInfo.isAdmin);
        return admin !== undefined || this.jarAuthType === "project";
      },

      changeProject () {
        this.getCurrentTag()
        this.$store.dispatch('tagsView/delView', this.currentTag).then(() => {
          // if (this.$route.name === 'SourceManage_allVersion') {//切换项目时，如果停留在当前页，关闭当前页，再跳到资源管理页，如果不在当前页，则关闭当前页面的页签
          //   this.$router.push({
          //     name: 'SourceManage',
          //     query: {
          //       projectId: this.$route.query.projectId,
          //       projectName: this.$route.query.projectName,
          //       projectCode: this.$route.query.projectCode
          //     }
          //   })
          // }
        })
      },
      getCurrentTag () {
        const visitedViews = this.$store.state.tagsView.visitedViews
        for (let i = 0; i < visitedViews.length; i++) {
          if (visitedViews[i].firstRouteName === 'SourceManage_allVersion') {
            this.currentTag = visitedViews[i]
            break
          }
        }
      },
      async getList () {
        const params = {
          orderByClauses: [{
            field: "update_time",
            orderByMode: this.order
          }],
          page: this.page,
          pageSize: this.pagination.defaultPageSize,
          vo: {
            mainResourceId: this.mainResourceId,
          }
        }
        this.dataList = []
        this.pagination.total = 0
        this.isLoading = true
        let res = await this.$http.post('/jar/listJar', params)
        this.isLoading = false
        if (res.code === 200) {
          if (res.data) {
            this.pagination.total = res.data.rowTotal
            this.jarTotal = res.data.rowTotal
            if (res.data.rows) {
              this.dataList = res.data.rows
            } else {
              this.dataList = []
            }
          }
        } else {
          this.$message.error({ content: res.msg, duration: 2 });
        }
      },
      // async referenceJobs () {
      //   // 查询该jar包关联的所有任务数量 (所有版本)
      //   const params = {
      //     jarName: this.jarName,
      //     projectId: Number(this.$route.query.projectId),
      //     jarAuthType: this.jarAuthType,
      //     jarVersion: ""
      //   }
      //   let res = await this.$http.post('/jar/referenceJobs', params)
      //   if (res.code === 200) {
      //     if (res.data) {
      //       this.jobTotal = res.data.total
      //     }
      //   } else {
      //     this.$message.error(res.msg);
      //   }
      // },
      popMemberDialog (item) {
        this.$refs.memberDialog.open(item)
      },
      update (item) {
        const obj = {
          type: 'update',
          mainResourceId: this.mainResourceId,
          runMotorType: item.runMotorType,
          data: item,
          title: '更新：' + item.jarName
        }
        this.$refs.addJar.open(obj)
      },
      remove (item) {
        var onOk = async function () {
          const params = {
            id: item.id
          }
          let res = await this.$http.post('/jar/deleteJar', params)
          if (res.code === 200) {
            this.$message.success('删除成功');
            this.getList()
          } else {
            this.$message.error(res.msg);
          }
        }.bind(this)
        this.$confirm({
          title: '确定要删除吗?',
          content: '',
          okText: '确认',
          cancelText: '取消',
          onOk: onOk
        });
      },

      // viewJobs (data) {
      //   this.$refs.referenceJobs.open(data)
      // },
      // 分页数据变化
      pageChange (pageInfo) {
        this.page = pageInfo.page
        this.getList()
      },
      // pageSize变化回调
      pageSizeChange (pageSizeInfo) {
        this.pagination.defaultPageSize = pageSizeInfo.size
        this.page = 1
        this.pagination.current = 1
        this.getList()
      },
      addSuccess () {
        this.page = 1
        this.pagination.current = 1
        this.getList()
      },
      handleChange (pagination, filters, sorter) {
        if (sorter.order === 'ascend') {
          this.order = 0
          this.getList()
        } else if (sorter.order === 'descend') {
          this.order = 1
          this.getList()
        }

      },
      back () {

        this.getCurrentTag()
        this.$store.dispatch('tagsView/delView', this.currentTag).then(()=>{
          this.$router.push({
            name: "SourceManage",
            query: {
              //预留query
              projectId: Number(this.$route.query.projectId),
              projectName: this.$route.query.projectName,
              // projectCode: this.$route.query.projectCode
            },
            params: {
              jarAuthType: this.jarAuthType,
              projectName: this.$route.query.projectName,
              // projectCode: this.$route.query.projectCode
            }
          });

        })

      },
      // gitReplace (url) {
      //   url = url.replace('xxx', '...')
      //   return url
      // },
      async download (record) {
        const params = {
          jarName: record.jarName,
          projectId: Number(this.$route.query.projectId),
          id: record.id
        }
        this.isLoading = true
        let res = await this.$http.post('/jar/download', params, {
          headers: {
            // responseType: "application/java-archive",
            projectId: Number(this.$route.query.projectId)
          },
          responseType: "blob",
        })
        if (res) {
          if (res.msg) {
            this.$message.error(res.msg)
            this.isLoading = false
            return
          }
          downloadByData(res.data, record.jarName);
          this.isLoading = false
        }
      }
    }
  };
</script>

<style lang="scss" scoped>
  .project-container {
    height: 100%;
    .guide-component {
      width: 100%;
      height: 100%;
    }
    .project-top {
      height: 52px;
      font-size: 14px;
      .sub-project-top {
        height: 100%;
        padding: 0 16px;
        margin: 0 auto;
        .jar-name {
          margin-right: 39px;
          cursor: pointer;
        }
        .total {
          .item {
            margin-right: 20px;
            color: #666;
            &.member {
              i {
                margin-right: 8px;
              }
            }
          }
        }
        .right {
          .new {
            width: 92px;
            height: 28px;
            text-align: center;
            line-height: 28px;
            color: #fff;
            font-size: 12px;
            cursor: pointer;
            i {
              font-size: 12px;
              margin-right: 7px;
            }
          }
        }
      }
    }
    .project-list {
      height: calc(100% - 52px - 50px);
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
    }
  }
</style>