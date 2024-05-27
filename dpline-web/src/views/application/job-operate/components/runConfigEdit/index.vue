<template>
    <confirm-dialog :visible="isPop"
                     title="修改运行配置"
                     :width="600"
                     @close="destory()"
                     @confirm="confirm()">
      <div class="start-main"
           v-loading="isLoading">
        <a-collapse class="collapse-container"
                    :default-active-key="[1,2,3,4]"
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
                          v-model="runModeType"
                          @change="runModeChange">
                  <a-select-option v-for="(item,key) in runModeList"
                                   :key="key"
                                   :value="item.value">
                    <div class="justify-start">{{ item.value }}</div>
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
            </div>
          </a-collapse-panel>
          <a-collapse-panel key="6"
                            v-show="fileType==='DS'"
                            header="App参数"
                            :style="customStyle">
            <div class="inner">
              <div class="argvRow">
                <p class="label">运行参数</p>
                <a-input v-model="appArgs"
                         type="textarea"
                         placeholder="请输入jar包的额外参数，如-Dxxx=xxx等，具体由main-jar包自行控制"
                />
              </div>
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
      <div slot="footer"></div>
    </confirm-dialog>
</template>
<script>
import ConfirmDialog from '@/components/confirm-dialog/index'
import resourceColor from '../../mixins/resource-color.js'
import {clusterTypeList, runModeList} from "@/utils/enumType";
import * as monaco from "monaco-editor";
export default {
  name: 'runConfigEdit',
  mixins: [resourceColor],
  components: {
    ConfirmDialog
  },
  // props: {
  //   runCallback: {
  //     type: Function,
  //     value: () => { }
  //   }
  // },
  data () {
    return {
      fileType: '',
      confirmDisabled: false,
      resourceData: null,
      jobId: '',
      isPop: false,
      isMaxWidth:false,
      clusterTypeList: clusterTypeList,
      // clusterType: clusterTypeList[0].value,
      runModeType:'',
      appArgs:'',
      collapseActive: false,
      customStyle: 'border-radius: 4px;margin-bottom: 0;border: 0;overflow: hidden;background:#FFF',
      isLoading: false,
      clusterList: [],
      projectId: '',
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
        clusterId: null,
        clusterName: ''
      },
      runImageInfo: {
        imageId: null,
        shortName: '',
        imageName: ''
      },
      motorVersion: {
        motorId: null,
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
  watch: {
    isPop: {
      handler (val) {
        if (val && !this.monacoInstance) {
          console.log("init")
          this.init()
        }
      },
      immediate: true
    }
  },
  created () {

  },
  methods: {
    init(){
      this.$nextTick(()=>{
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
      })
    },
    confirm () {
      this.updateJobRunConfig()
    },
    async open (record) {
      this.isPop = true
      this.runtimeOptions = JSON.parse(record.runtimeOptions)
      this.motorVersion.motorId = Number(record.motorVersionId)
      this.appArgs = record.appArgs
      this.flinkYaml = record.otherRuntimeConfig
      this.runClusterInfo.clusterId = Number(record.clusterId)
      this.fileType = record.fileType
      this.runImageInfo.imageId = Number(record.imageId)
      this.jobId = record.id
      this.runModeType = record.runModeType
      // if (this.collapseActive && !this.monacoInstance) {
      //   this.init()
      // }
      // if (!this.monacoInstance) {
      //   this.$nextTick(()=>{
      //     this.monacoInstance = monaco.editor.create(
      //         // 表示通过ref属性引用的名为configEditor的DOM元素作为编辑器的容器。
      //         this.$refs["configEditor"], {
      //           value: "nihao",
      //           language: 'yaml',
      //           minimap: {
      //             enabled: false
      //           },
      //           automaticLayout: true,
      //           scrollBeyondLastLine: false
      //           // wordWrap: 'on'
      //         });
      //   })

        // console.log(this.monacoInstance)
      // }
      this.getFlinkVersionList()
      this.getMotorVersionById(Number(record.motorVersionId))
      this.getClusterById(Number(record.clusterId))
      this.getDockerImageById(Number(record.imageId))
    },
    async getMotorVersionById(value){
      if(value === null || value === 0){
        return
      }
      const params = {
        flinkVersionId: value,
      }
       // 获取 motorVersion 的版本信息
      let res = await this.$http.post('/system/motorVersion/selectById', this.$qs.stringify(params), {
         headers: {
           projectId: Number(this.$route.query.projectId)
         }
      })
      if (res.code === 200) {
        this.motorVersion = res.data;
      } else {
        this.$message.warning({ content: res.msg, duration: 2 })
      }
    },
    async getClusterById(value){
      if(value === null || value === 0){
        return
      }
      const params = {
        clusterId: value,
      }
      // 获取 MotorVersion 的版本信息
      let res = await this.$http.post('/system/clusterManagement/selectById', this.$qs.stringify(params), {
        headers: {
          projectId: Number(this.$route.query.projectId)
        }
      })
      if (res.code === 200) {
        this.runClusterInfo = res.data
      } else {
        this.$message.warning({ content: res.msg, duration: 2 })
      }
    },
    async getDockerImageById(value){
      if(value === null || value === 0){
        return
      }
      const params = {
        imageId: value,
      }
      // 获取 MotorVersion 的版本信息
      let res = await this.$http.post('/system/docker/selectById', this.$qs.stringify(params), {
        headers: {
          projectId: Number(this.$route.query.projectId)
        }
      })
      if (res.code === 200) {
        this.runImageInfo = res.data
      } else {
        this.$message.warning({ content: res.msg, duration: 2 })
      }
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
          return
        }
        this.reSetK8sImage()
      }
    },
    collapseChange (key) {
      if (key.includes('5')) {
        this.collapseActive = true
      } else {
        this.collapseActive = false
      }
    },
    dockerImageChange(value){
      const dockerImage =  value.split(",,")
      this.runImageInfo.imageId = dockerImage[0]
      this.runImageInfo.shortName = dockerImage[1]
      this.runImageInfo.imageName = dockerImage[2]
    },
    // 更新作业
    async updateJobRunConfig () {
      let params = {
        runtimeOptions: JSON.stringify(this.runtimeOptions),
        motorVersionId: this.motorVersion.motorId,
        appArgs: this.appArgs,
        otherRuntimeConfig: this.monacoInstance.getValue(),
        clusterId: Number(this.runClusterInfo.clusterId),
        fileType: this.fileType,
        imageId: Number(this.runImageInfo.imageId),
        id: this.jobId,
        runModeType: this.runModeType,
        clusterType: this.runClusterInfo.clusterType,
      }
      const res = await this.$http.post('/job/updateRunConfig', this.$qs.stringify(params), {
        headers: {
          projectId: Number(this.$route.query.projectId)
        }
      })
      if (res.code === 200) {
        this.isPop = false
        this.$message.success("更新成功")
      } else {
        this.$message.error("更新失败")
      }
      this.$emit("runCallback",res)
    },
    changeClusterName (value) {
      const findItem = this.clusterList.filter(item => {
        return String(item.id) === String(value)
      })
      this.runClusterInfo.clusterName = findItem[0].clusterName
      this.runClusterInfo.clusterId = findItem[0].id
    },
    clusterTypeChange(){
      console.log(this.runClusterInfo)
      this.reSetCluster()
      this.reSetK8sImage()
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
      this.dockerImageList = []
      this.getDockerImageList()
    },
    runModeChange(value){
      this.runModeType = value
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
    destory(){
      this.isPop = false;
      this.monacoInstance = null;
      this.collapseActive = false;
    }
  },
  destroyed() {
    this.isPop = false
  }
}
</script>
<style lang="scss" scoped>
/deep/ .confirm-dialog {
  .ant-modal {
    .ant-modal-content {
      width: 600px;
      .confirm-main {
        margin-top: 10px;
      }
    }
    .ant-modal-body{
      width: 100%;
    }
  }
}
.start-main {
  width: 100%;
  height: 400px;
  font-size: 12px;
  display: flex;
  justify-content: flex-start;
  align-items: stretch;
  flex-direction: column;
  overflow-x: hidden;
  overflow-y: auto;
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
    .argvRow {
      width: 96%;
    }
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
  .select {
    width: 100%;
    font-size: 12px;
  }
  .row {
    width: 100%;
    margin-top: 3px;
    .label {
      margin-bottom: 4px;
    }

  }
}
</style>
