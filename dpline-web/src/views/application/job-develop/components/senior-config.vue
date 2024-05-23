<template>
  <div class="senior"
       :class="fileType"
       v-loading="isLoading">
    <a-collapse class="collapse-container"
                :default-active-key="[1,2,3,4,5]"
                @change="collapseChange"
                :bordered="false">
      <template #expandIcon="props">
        <a-icon type="caret-right"
                :rotate="props.isActive ? 90 : 0" />
      </template>
      <a-collapse-panel key="1"
                        header="运行集群"
                        :style="customStyle">
        <div class="inner"
             :class="{'is-min':!isMaxWidth}">
          <div class="row"
               :class="{'justify-start':isMaxWidth}">
            <p class="label">集群类型</p>
            <a-select class="select"
                      v-model="runClusterInfo.clusterType"
                      @change="clusterTypeChange">
              <a-select-option v-for="item in clusterTypeList"
                               :key="item.value">
                {{ item.value }}
              </a-select-option>
            </a-select>
          </div>
          <div class="row"
               :class="{'justify-start':isMaxWidth}">
            <p class="label">计算集群</p>
            <a-select class="select"
                      v-model="runClusterInfo.clusterName"
                      @change="changeClusterName">
              <a-select-option v-for="item in clusterList"
                               :key="item.id">
                {{ item.clusterName }}
              </a-select-option>
            </a-select>
          </div>
        </div>
<!--        <div class="inner"-->
<!--             :class="{'is-min':!isMaxWidth}">-->

<!--        </div>-->
      </a-collapse-panel>
      <a-collapse-panel key="2"
                        header="运行版本"
                        :style="customStyle">
        <div class="inner"
             :class="{'is-min':!isMaxWidth}">
          <div class="row"
               :class="{'justify-start':isMaxWidth}">
            <p class="label">引擎版本</p>
            <a-select class="select"
                      v-model="motorVersion.motorRealVersion"
                      @change="flinkVersionChange">
              <a-select-option v-for="(item,key) in motorVersionList"
                               :key="key"
                               :value="item.id  + ',,' + item.realVersion + ',,' + item.flinkPath">
                <div class="justify-start">{{ item.realVersion }}</div>
                <!--                     v-if="item.betaFlag == 1"></i></div>-->
              </a-select-option>
            </a-select>
          </div>
          <div v-if="runClusterInfo.clusterType === clusterTypeList[0].value"
               class="row"
               :class="{'justify-start':isMaxWidth}">
            <p class="label">运行镜像</p>
            <a-select class="select"
                      v-model="runImageInfo.shortName"
                      @change="dockerImageChange">
              <a-select-option v-for="item in dockerImageList"
                               :key="item.id"
                               :value="item.id + ',,' + item.shortName + ',,' + item.imageName"
                                >
                {{ item.shortName }}
              </a-select-option>
            </a-select>
          </div>
        </div>
      </a-collapse-panel>
      <a-collapse-panel key="3"
                        header="运行模式"
                        :style="customStyle">
        <div class="inner"
             :class="{'is-min':!isMaxWidth}">
          <div class="row"
               :class="{'justify-start':isMaxWidth}">
            <p class="label">运行方式</p>
            <a-select class="select"
                      v-model="runMode"
                      @change="runModeChange">
              <a-select-option v-for="(item,key) in runModeList"
                               :key="key"
                               :value="item.value">
                <div class="justify-start">{{ item.label }}</div>
                <!--                     v-if="item.betaFlag == 1"></i></div>-->
              </a-select-option>
            </a-select>
          </div>
        </div>
      </a-collapse-panel>
      <a-collapse-panel key="4"
                        header="运行资源"
                        :style="customStyle">
        <div class="inner"
             :class="{'is-min':!isMaxWidth}">
          <div class="row"
               :class="{'justify-start':isMaxWidth}">
            <p class="label">Job Manager Memery</p>
            <a-input class="input"
                     type="text"
                     v-model="runtimeOptions.jobManagerMem"
                     placeholder="请输入Job Manager Memory">
              <a-tooltip slot="suffix"
                         title="可输入任意大小，如1024MB、或者1GB，大小写均可">
                <a-icon type="info-circle"
                        style="color: #7FB6FF" />
              </a-tooltip>
            </a-input>
          </div>

          <div class="row"
               :class="{'justify-start':isMaxWidth}">
            <p class="label">Task Manager Memery</p>
            <a-input class="input"
                     type="text"
                     v-model="runtimeOptions.taskManagerMem"
                     placeholder="请输入Job Manager Memory">
              <a-tooltip slot="suffix"
                         title="可输入任意大小，如1024MB、或者1GB，大小写均可">
                <a-icon type="info-circle"
                        style="color: #7FB6FF" />
              </a-tooltip>
            </a-input>
          </div>
        </div>

        <div class="inner"
             :class="{'is-min':!isMaxWidth}">
          <div class="row"
               :class="{'justify-start':isMaxWidth}">
            <p class="label" >Task Manager CPUs</p>
            <a-input class="input"
                     type="number"
                     v-model="runtimeOptions.taskManagerCpu"
                     placeholder="请输入Task Manager CPUs">
              <a-tooltip slot="suffix"
                         title="任何大于 0 的数,例如：1，10.5">
                <a-icon type="info-circle"
                        style="color: #7FB6FF" />
              </a-tooltip>
            </a-input>
          </div>

          <div class="row "
               :class="{'justify-start':isMaxWidth}">
            <p class="label">Job Manager CPUS</p>
              <a-input class="input"
                       type="number"
                       v-model="runtimeOptions.jobManagerCpu"
                       placeholder="请输入Task Manager Memory">
                <a-tooltip slot="suffix"
                           title="以G为单位，可输入任意数，例如1，或者0.5等">
                  <a-icon type="info-circle"
                          style="color: #7FB6FF" />
                </a-tooltip>
              </a-input>
          </div>
        </div>

        <div class="inner"
             :class="{'is-min':!isMaxWidth}">
          <div class="row"
               :class="{'justify-start':isMaxWidth}">
            <p class="label" >Parallelism</p>
            <a-input class="input"
                     type="number"
                     v-model="runtimeOptions.parallelism"
                     placeholder="请输入并发度">
              <a-tooltip slot="suffix"
                         title="任何大于 0 的整数">
                <a-icon type="info-circle"
                        style="color: #7FB6FF" />
              </a-tooltip>
            </a-input>
          </div>
<!--          <div class="row "-->
<!--               :class="{'justify-start':isMaxWidth}">-->
<!--            <p class="label">Task Managers 数量</p>-->
<!--            <a-input class="input"-->
<!--                     type="number"-->
<!--                     v-model="runtimeOptions.taskManagerNum"-->
<!--                     placeholder="Basic usage">-->
<!--              <a-tooltip slot="suffix"-->
<!--                         title="Extra information">-->
<!--                <a-icon type="info-circle"-->
<!--                        style="color: rgba(0,0,0,.45)" />-->
<!--              </a-tooltip>-->
<!--            </a-input>-->
<!--          </div>-->
        </div>


      </a-collapse-panel>
    <a-collapse-panel class="flink-collapse"
                        key="5"
                        header="引擎配置"
                        :style="customStyle"></a-collapse-panel>
    </a-collapse>
    <div v-show="collapseActive"
         class="flink-wrapper">
      <div class="config-editor-container"
           ref="configEditor"></div>
    </div>
  </div>
</template>

<script>
  import * as monaco from "monaco-editor"
  import {clusterTypeList} from '@/utils/enumType'
  import {runModeList} from "@/utils/enumType"

  export default {
    name: "SeniorConfig",
    data () {
      return {
        clusterTypeList: clusterTypeList,
        // clusterType: clusterTypeList[0].value,
        runMode:'',
        collapseActive: true,
        customStyle: 'border-radius: 4px;margin-bottom: 0;border: 0;overflow: hidden;background:#FFF',
        isLoading: false,
        clusterList: [],
        projectId: '',
        jobConfig: {},
        runtimeOptions: {
          jobManagerCpu: 1,
          jobManagerMem: '1',
          taskManagerCpu: 1,
          taskManagerMem: '1',
          parallelism: 1,
          // taskManagerNum: 1,
        },
        runClusterInfo: {
          clusterType: clusterTypeList[0].value,
          clusterId: '',
          clusterName: ''
        },
        runImageInfo: {
          imageId:'',
          shortName: '',
          imageName: ''
        },
        motorVersion: {
          motorId: '',
          motorPath: '',
          motorRealVersion: ''
        },
        flinkYaml: '',
        monacoInstance: null,
        isFetchingSource: false,
        motorVersionList: [],
        runModeList: runModeList,
        dockerImageList:[],
      }
    },
    props: {
      config: {
        type: Object,
        default: () => {
          return {}
        }
      },
      isShow: {
        type: Boolean,
        default: false
      },
      fileType: {
        type: String,
        default: ''
      },
      isMaxWidth: {
        type: Boolean,
        default: true
      }
    },
    components: {
    },
    watch: {
      config: {
        async handler (val) {
          const jobConfig = JSON.parse(JSON.stringify(val))
          if (jobConfig && Object.keys(jobConfig).length !== 0) {
            this.runClusterInfo = jobConfig.runClusterInfo
            this.flinkYaml = jobConfig.flinkYaml
            this.runMode = jobConfig.runMode || this.runModeList[1].value
            this.motorVersion = jobConfig.motorVersion
            this.runtimeOptions = jobConfig.runtimeOptions
            this.runImageInfo = jobConfig.runImageInfo
            this.isLoading = true
            //获取到文件详情后再获取engine和jar包
            this.getClusters() //保存时需要提交默认的flink引擎配置，没点作业配置，也需要先加载好
            this.getDockerImageList()
            this.isLoading = false
            this.jobConfig = jobConfig
          }
        },
        immediate: true,
        deep: true
      },
      isShow: {
        async handler (val) {
          if (val && !this.monacoInstance) {
            this.init()
          }
          // if (val) {
            //获取到文件详情后再获取engine和jar包
            // this.getClusters() //保存时需要提交默认的flink引擎配置，没点作业配置，也需要先加载好
            // this.getDockerImageList()
          // }
        },
        immediate: true
      }
    },
    beforeCreate () {
      this.runtimeOptions = {
        jobManagerCpu: 1,
        jobManagerMem: '1',
        taskManagerCpu: 1,
        taskManagerMem: '1',
        parallelism: 1,
        // taskManagerNum: 1,
      }
    },
    mounted () {
      this.getFlinkVersionList()
    },
    computed: {
    },
    methods: {
      init () {
        // // console.log('monaco.languages.getLanguages()', monaco.languages.getLanguages())
        // 初始化编辑器实例
        // this.jarFunctionTypeList = jarFunctionTypeList;
        this.monacoInstance = monaco.editor.create(
          // 表示通过ref属性引用的名为configEditor的DOM元素作为编辑器的容器。
          this.$refs["configEditor"], {
          value: this.flinkYaml,
          language: 'yaml',
          minimap: {
            enabled: false
          },
          automaticLayout: true,
          scrollBeyondLastLine: false
          // wordWrap: 'on'
        });
      },
      // 获取flink tag
      async getFlinkVersionList () {
        const params = {}
        const res = await this.$http.post('/system/motorVersion/enableList', params, {
          headers: {
            projectId: Number(this.$route.query.projectId)
          }
        })
        if (res.code === 200) {
          this.motorVersionList = res.data
        }
        if(this.motorVersion.motorId !== '' && this.motorVersion.motorId !== null){
          const motorVersionNew = this.motorVersionList.find(item => Number(item.id) === Number(this.motorVersion.motorId))
          // 还能找到，说明没有删掉
          if (motorVersionNew !== undefined ){
            this.motorVersion.motorId = Number(motorVersionNew.id)
            this.motorVersion.motorRealVersion = motorVersionNew.realVersion
            this.motorVersion.motorPath = motorVersionNew.flinkPath
            this.jobConfig.motorVersion = this.motorVersion
            return
          }
          this.resetMotorVersion()
          this.reSetK8sImage()
        }
      },
      resetMotorVersion(){
        this.motorVersion = {
          motorId: '',
          motorPath: '',
          motorRealVersion: ''
        }
        this.jobConfig.motorVersion = this.motorVersion
      },
      runModeChange(){
        this.jobConfig.runMode = this.runMode
      },
      // async handleImageSearch(){
      //   console.log("重新search查询")
      //   if(this.dockerImageList.length === 0){
      //     // this.dockerImageList = []
      //     this.getDockerImageList()
      //   }
      // },
      dockerImageChange(value){
        const dockerImage =  value.split(",,")
        this.runImageInfo.imageId = dockerImage[0]
        this.runImageInfo.shortName = dockerImage[1]
        this.runImageInfo.imageName = dockerImage[2]
        this.jobConfig.runImageInfo = this.runImageInfo
      },
      clusterTypeChange(){
        console.log(this.runClusterInfo)
        this.jobConfig.runClusterInfo.clusterType = this.runClusterInfo.clusterType
        this.reSetCluster()
        this.reSetK8sImage()
        // console.log(this.flinkVersion)
        // 需要重新设置一次使用版本，置空镜像，重新选择镜像
        // this.flinkVersionChange(this.motorVersion.motorId
        //     +",," + this.motorVersion.motorRealVersion)
      },
      reSetCluster(){
        // 集群类型发生变化，集群选项置空，重新选择集群
        this.runClusterInfo.clusterName=''
        this.runClusterInfo.clusterId=''
        this.getClusters()
      },
      reSetK8sImage(){
          this.runImageInfo = {
            imageId:'',
            imageName: '',
            shortName: ''
          }
          this.jobConfig.runImageInfo = this.runImageInfo
          this.dockerImageList = []
          this.getDockerImageList()
      },
      changeClusterName (value) {
        const findItem = this.clusterList.filter(item => {
          return String(item.id) === String(value)
        })
        this.runClusterInfo.clusterName = findItem[0].clusterName
        this.runClusterInfo.clusterId = findItem[0].id
      },
      async getDockerImageList(){
        // console.log("getDockerImageList")
        // 如果不是 k8s 模式，直接退出
        if(this.runClusterInfo.clusterType !== this.clusterTypeList[0].value){
            this.dockerImageList = []
            return;
        }
        // 如果  引擎版本 为空 直接返回
        if(this.motorVersion.motorId === null || this.motorVersion.motorId === ''){
            this.dockerImageList = []
            return;
        }
        const params = {
          motorVersionId: this.motorVersion.motorId
        }
        const res = await this.$http.post('/system/docker/queryDockerImage', params, {
          headers: {
            projectId: Number(this.$route.query.projectId)
          }
        })
        if (res.code === 200) {
          this.dockerImageList = res.data
        }

      },
      async getClusters () {
        const params = {
          projectId: Number(this.$route.query.projectId),
          clusterType: this.runClusterInfo.clusterType
        }
        const res = await this.$http.post('/file/getFileClusters', this.$qs.stringify(params), {
          headers: {
            projectId: Number(this.$route.query.projectId)
          }
        })
        if (res.code === 200) {
          this.clusterList = res.data
        }
      },
      flinkVersionChange (value) {
        const flinkVersion =  value.split(",,")
        this.motorVersion.motorId = flinkVersion[0]
        this.motorVersion.motorRealVersion = flinkVersion[1]
        this.motorVersion.motorPath = flinkVersion[2]

        // k8s 模式，版本变化，镜像需要初始化
        this.reSetK8sImage()
      },
      collapseChange (key) {
        if (key.includes('5')) {
          this.collapseActive = true
        } else {
          this.collapseActive = false
        }
      }
      // handleSourceSearch (value) {
      //   this.getSource(value)
      // },
      // async handleChangeSource (value) {
      //   if (value) {
      //     const jar = JSON.parse(value.key)
      //     this.jobConfig.jarName = jar.name
      //     this.jobConfig.jarId = jar.id
      //     // this.jobConfig.jarVersion = jar.version//默认选择主版本
      //     // await this.getJarVersion(this.jobConfig.jarName)
      //     if (this.jarVersionData && this.jarVersionData.length) {//切换资源时默认选中第一个
      //       this.jarVersion = { key: JSON.stringify(this.jarVersionData[0]), label: this.jarVersionData[0].version }
      //       this.jobConfig.jarVersion = this.jarVersionData[0].version
      //     }
      //
      //   } else {
      //     this.jobConfig.jarVersion = ''
      //     this.jobConfig.jarName = ''
      //     this.jobConfig.jarId = ''
      //   }
      //
      // },

      // handleChangeJarVersion (value) {
      //   if (value) {
      //     const selectVersion = JSON.parse(value.key)
      //     this.jobConfig.jarVersion = selectVersion.version
      //     this.jobConfig.jarId = selectVersion.id
      //   } else {
      //     this.jobConfig.jarVersion = ''
      //   }
      //
      // },

      // async getSource (value) {
      //   const params = {
      //     name: value || '', //引擎名称
      //     projectId: Number(this.$route.query.projectId),
      //     // version: 'v1'
      //   }
      //   this.isFetchingSource = true
      //   let res = await this.$http.post('/jar/searchJar', params, {
      //     headers: {
      //       projectId: Number(this.$route.query.projectId)
      //     }
      //   })
      //   this.isFetchingSource = false
      //   if (res.code === 200 && res.data) {
      //     this.sourceData = res.data
      //     console.log(this.sourceData)
      //     const findCurrent = this.sourceData.filter(item => {
      //       // return String(item.id) === String(this.jobConfig.jarId)
      //       return item.name === this.jobConfig.jarName//不能用id,jarId是子版本的id
      //     })
      //     if (findCurrent.length === 0) {
      //       this.source = ''
      //     }
      //   }
      // },
      // async getJarVersion (jarName) {
      //   const params = {
      //     orderByClauses: [{
      //       field: "update_time",
      //       orderByMode: 1
      //     }],
      //     page: 1,
      //     pageSize: 2000,
      //     vo: {
      //       name: jarName,
      //       projectId: Number(this.$route.query.projectId)
      //     }
      //   }
      //   this.jarVersionData = []
      //   this.isFetchingJarVersion = true
      //   let res = await this.$http.post('/jar/queryJar', params)
      //   this.isFetchingJarVersion = false
      //   if (res.code === 200) {
      //     if (res.data) {
      //       if (res.data.rows) {
      //         this.jarVersionData = res.data.rows
      //       } else {
      //         this.jarVersionData = []
      //       }
      //       return this.jarVersionData
      //     }
      //   } else {
      //     this.$message.error(res.msg);
      //   }
      // },
    }
  }
</script>
<style lang="scss">
  .chitutreebeta {
    font-size: 40px !important;
    font-weight: 100 !important;
    color: #298dff;
    margin-left: 4px;
  }
  .select-source-option {
    .ant-select-dropdown-content .ant-select-dropdown-menu {
      max-height: 220px !important;
    }
  }
</style>
<style lang="scss" scoped>
  .senior {
    .collapse-container {
      /deep/ .ant-collapse-header {
        font-size: 14px !important;
        font-weight: 900;
        padding: 5px 16px;
        padding-left: 40px;
        i {
          color: #006fff !important;
        }
      }
      /deep/ .ant-collapse-item {
        font-size: 12px;
      }
      /deep/ .ant-collapse-content-box {
        padding: 0px 0px 0 16px;
      }
      .input-row {
        display: flex;
        margin-bottom: 10px;
        align-items: center;
        .dynamic-delete-button {
          cursor: pointer;
          position: relative;
          font-size: 18px;
          color: #999;
          transition: all 0.3s;
        }
      }
    }
    width: 100%;
    font-size: 12px;
    height: calc(100% - 32px);
    display: flex;
    justify-content: flex-start;
    align-items: stretch;
    flex-direction: column;
    overflow-x: hidden;
    overflow-y: auto;
    padding: 0 16px;
    .common-title {
      padding: 10px 0;
      i {
        color: #006fff;
        margin-right: 8px;
      }
      .name {
        font-size: 14px;
        font-weight: 900;
      }
    }
    .inner {
      width: 100%;
      .row {
        .label {
          width: 200px;
        }
        .select {
          width: 325px !important;
        }
        .input {
          width: 50% !important;
        }
        //.select-source {
        //  width: 325px !important;
        //}
      }
      &.is-min {
        display: flex;
        //flex-wrap: wrap;
        .row {
          width: calc(50% - 8px);
          margin-right: 8px;
          .select {
            width: 100% !important;
          }
          .select-source {
            width: 100% !important;
          }
          .input {
            width: 95% !important;
          }
        }
      }
    }
    .flink-wrapper {
      flex: 1;
      display: flex;
      min-height: 500px;
      justify-content: flex-start;
      align-items: stretch;
      flex-direction: column;
      margin-top: 10px;
      .config-editor-container {
        width: 100%;
        height: 90%;
      }
    }
    h3 {
      color: #333;
      font-size: 12px;
      margin-bottom: 8px;
    }
    .select {
      width: 100%;
      font-size: 12px;
    }
    .row {
      width: 100%;
      margin-top: 3px;
      //.cluster-version {
      //  height: 32px;
      //  line-height: 32px;
      //}
      .label {
        margin-bottom: 4px;
      }

      //.select-source {
      //  width: 80%;
      //  font-size: 12px;
      //  /deep/ .name {
      //    width: 100%;
      //    display: block;
      //  }
      //  /deep/ .description {
      //    display: none;
      //  }
      //}
    }
    &.DS {
      .config-editor-container {
        height: calc(100vh - 370px);
      }
    }
  }
</style>