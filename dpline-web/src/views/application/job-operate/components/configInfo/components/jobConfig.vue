<!--
 * @Author: hjg
 * @Date: 2021-11-09 15:56:52
 * @LastEditTime: 2022-09-19 19:49:47
 * @LastEditors: Please set LastEditors
 * @Description: In User Settings Edit
 * @FilePath: \src\views\application\job-operate\components\configInfo\components\jobConfig.vue
-->
<template>
  <div class="job-config"
       v-loading="isLoading">
    <object-table :keyArrs="keyArrs"
                  :dataInfo="dataInfo" />
  </div>
</template>
<script>
  import objectTable from './objectTable.vue'
  // import {config} from "@/utils/config";
  export default {
    components: {
      objectTable
    },
    data () {
      return {
        isLoading: false,
        dataInfo: {},
        keyArrs: []
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
          if (val === 'jobConfig') {
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
        this.getConfigInfo()
      },
      // 获取作业配置信息
      async getConfigInfo () {
        // console.log('jobInfo', this.$store.getters.jobInfo)
        let params = {
          confType: 'JOB_CONFIG',
          id: this.$store.getters.jobInfo.id
        }
        this.isLoading = true
        let res = await this.$http.post('/job/jobConf', this.$qs.stringify(params))
        this.isLoading = false
        if (res.code === 200) {
          this.dataInfo = res.data
          this.getObjectKeys()
        }
      },
      // 映射key的释义
      getObjectKeys () {
        for (let key in this.dataInfo) {
          let text = null
          let flag = true
          if (key === 'runModeType') {
            text = '运行模式'
            flag = true
          } else if (key === 'clusterName') {
            text = '集群名称'
            flag = true
          } else if (key === 'motorVersion') {
            text = '引擎版本'
            flag = true
          } else if (key === 'imageName') {
            text = '镜像名称'
            flag = true
          } else if (key === 'defaultParallelism') {
            text = '默认并行度'
            flag = true
          } else if (key === 'jobManagerMemory') {
            text = 'JobManager内存'
            flag = true
          } else if (key === 'taskManagerMemory') {
            text = 'TaskManager内存'
            flag = true
          } else if (key === 'jobManagerCpus') {
            text = 'jobManager cpu'
            flag = true
          } else if (key === 'taskManagerCpus') {
            text = 'taskManager cpu'
            flag = true
          } else {
            text = key
            flag = false
          }
          if (flag) {
            let obj = {
              text: text,
              key: key,
              type: 'text'
            }
            this.keyArrs.push(obj)
          }
        }
      }
    }
  }
</script>
<style lang="scss" scoped>
  .job-config {
    width: 100%;
    height: 100%;
    /deep/ .table-item {
      width: 870px;
      .left {
        width: 400px;
      }
      .right {
        width: 470px;
      }
    }
  }
</style>
