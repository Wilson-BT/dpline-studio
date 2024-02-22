<!--
 * @Author: hjg
 * @Date: 2021-10-19 11:08:45
 * @LastEditTime: 2022-06-24 10:42:57
 * @LastEditors: Please set LastEditors
 * @Description: 新建引擎
 * @FilePath: \src\views\system-setting\components\addEngineDialog.vue
-->
<template>
  <a-modal class="add-engine-dialog"
           v-model="isShowDialog"
           title="新增引擎"
           width="600px"
           :footer="null">
    <!-- 修改弹框右上角关闭图标 -->
    <template slot="closeIcon"><i class="chitutree-h5 chitutreeguanbi"></i></template>
    <div class="form-info"
         v-loading="isLoading">
      <a-form>
        <a-form-item class="name-url">
          <p>引擎名称<span>*</span></p>
          <a-input v-model="form.engineName"
                   class="name"
                   placeholder="请输入名称"></a-input>
        </a-form-item>
        <!-- 集群部署类型 -->
        <a-form-item class="form-item">
          <p>集群部署类型<span>*</span></p>
          <a-select v-model="form.engineType"
                    placeholder="请选择集群部署类型"
                    @change="handleClusterQueue">
            <a-select-option v-for="(item, index) in engineTypeList"
                             :value="item.value"
                             :key="index">{{ item.label }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item class="form-item">
          <p>集群环境类型<span>*</span></p>
          <a-select v-model="form.engineEnv"
                    placeholder="请选择集群环境类型">
            <a-select-option v-for="(item, index) in engineEnvList"
                             :value="item.value"
                             :key="index">{{ item.label }}</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item v-if="form.engineType === 'kubernetes'" class="name-url">
          <p>NAME_SPACE<span>*</span></p>
          <a-input v-model="form.engineNameSpace"
                   class="name"
                   placeholder="请输入NAMESPACE">
          </a-input>
        </a-form-item>
        <a-form-item v-if="form.engineType === 'kubernetes'" class="name-url">
          <p>KUBE_PATH<span>*</span></p>
          <a-input v-model="form.kubePath"
                   class="name"
                   placeholder="请输入kube config path 的绝对路径">
          </a-input>
        </a-form-item>
        <a-form-item v-if="form.engineType === 'kubernetes'" class="name-url">
          <p>SELECT_LABELS</p>
          <a-input v-model="form.selectLabels"
                   class="name"
                   placeholder="请输入 labels, 使用json格式，如 {'key':'value'}">
          </a-input>
        </a-form-item>

      </a-form>
    </div>
    <div class="footer justify-end">
      <div class="confirm-footer justify-end">
        <a-button @click="cancelEvent"
                  size="small">取消</a-button>
        <a-button style="margin-left:8px"
                  @click="confirmEvent"
                  size="small"
                  type="primary">确定</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script>
export default {
    data () {
      return {
        isLoading: false,
        isShowDialog: false,
        form: {
          engineType: 'Kubernetes',
          engineName: null,
          engineEnv: '测试环境',
          kubePath: '',
          // uatEngineCluster: '',
          engineNameSpace: '',
          // uatEngineQueue: '',
          selectLabels: "",
        },
        engineTypeList: [
          {
            label: "Kubernetes",
            value: "kubernetes",
          },
          {
            label: "Yarn",
            value: "yarn",
          }
        ],
        engineEnvList: [
          {
            label: "测试环境",
            value: "test",
          },
          {
            label: "生产环境",
            value: "prod",
          }
        ],
      }
    },
    watch: {
      isShowDialog: {
        async handler (val) {
          if (val) {
            this.initForm()
            this.isLoading = true
            // await this.getEngineClusterList('uat')
            // await this.getEngineClusterList('prod')
            // await this.getEngineQueueList('uat')
            // await this.getEngineQueueList('prod')
            this.isLoading = false
          }
        }
      }
    },
    methods: {
      // 初始化表单
      initForm () {
        this.isLoading = false
        this.form = {
          engineName: null,
          engineType: 'yarn',
          uatEngineCluster: '',
          engineCluster: '',
          uatEngineQueue: '',
          engineQueue: '',
        }
        this.uatEngineQueueList = []
        this.engineQueueList = []
        this.uatEngineClusterList = []
        this.engineClusterList = []
      },
      // 获取队列列表
      async getEngineQueueList (env) {
        const params = {
          clusterCode: env == 'test' ? this.form.uatEngineCluster : this.form.engineCluster,
          env,
          engineType: this.form.engineType,
        }
        let res = await this.$http.post('/setting/engineSetting/engineQueues', params)
        if (res.code === 0) {
          if (env == 'test') {
            this.uatEngineQueueList = res.data
            if (this.uatEngineQueueList && this.uatEngineQueueList.length) {
              this.form.uatEngineQueue = this.uatEngineQueueList[0].queueName
            } else {
              this.form.uatEngineQueue = ''
            }
          } else {
            this.engineQueueList = res.data
            if (this.engineQueueList && this.engineQueueList.length) {
              this.form.engineQueue = this.engineQueueList[0].queueName
            } else {
              this.form.engineQueue = ''
            }
          }
        } else {
          if (env == 'uat') {
            this.uatEngineQueueList = []
            this.form.uatEngineQueue = ''
          } else {
            this.engineQueueList = []
            this.form.engineQueue = ''
          }
        }
        return res
      },

      // 确认点击事件
      async confirmEvent () {
        if (this.form.engineName === null || this.form.engineName === '' || this.form.engineName.length === 0) {
          return this.$message.warning({ content: '引擎名称不能为空', duration: 2 })
        }
        if (this.form.engineType === null || this.form.engineType === '' || this.form.engineType.length === 0) {
          return this.$message.warning({ content: '未选择集群部署类型', duration: 2 })
        }
        if (this.form.engineType === '' || this.form.engineType.length === 0) {
          return this.$message.warning({ content: '未选择运行环境类型', duration: 2 })
        }
        const params = {
          engineName: this.form.engineName,
          engineType: this.form.engineType,
          uatEngineCluster: this.form.uatEngineCluster,
          engineCluster: this.form.engineCluster,
        }
        if (this.form.engineType == 'yarn') {
          // TODO
        } else {
          params.namespace = this.form.engineQueue
        }

        let res = await this.$http.post('/setting/engineSetting/add', params)
        if (res.code === 0) {
          this.$message.success({ content: '添加成功', duration: 2 })
          this.isShowDialog = false
          this.$emit('confirmEvent', true)
        } else {
          this.$message.error({ content: res.msg, duration: 2 })
        }
      },
      // 取消点击事件
      cancelEvent () {
        this.isShowDialog = false
        this.$emit('cancelEvent', false)
      },
      // 集群部署类型改变，
      async handleClusterQueue (value) {
        if (value) {
          this.isLoading = true

          // await this.getEngineClusterList('uat')
          // await this.getEngineClusterList('prod')
          // await this.getEngineQueueList('uat')
          // await this.getEngineQueueList('prod')
          this.isLoading = false
        }
      },
      async handleEngineCluster (value, env) {
        if (value) {
          this.isLoading = true
          await this.getEngineQueueList(env)
          this.isLoading = false
        }
      }
    },
    created () {

    }
  }
</script>

<style lang="scss" scoped>
  .add-engine-dialog {
    /deep/ .ant-modal-body {
      padding: 0;
    }
    .form-info {
      height: 412px;
      padding: 12px 16px 0;
      overflow-y: auto;
      /deep/ .ant-form-item {
        margin-bottom: 12px;
        height: 48px;
      }
      p {
        height: 16px;
        font-size: 12px;
        line-height: 16px;
        color: #333;
        font-weight: 600;
        span {
          color: red;
        }
      }
      .form-item {
        /deep/ .ant-form-item-children .ant-input {
          height: 28px;
        }
        .user-select {
          /deep/ .ant-select-selection--multiple {
            height: auto;
            min-height: 28px;
          }
          /deep/ .ant-select-selection__rendered {
            height: 28px;
            ul > li {
              margin-top: 1px;
            }
          }
        }
      }
      .name-url {
        /deep/ .ant-form-item-children .ant-input {
          height: 28px;
        }
        .name {
          width: 100%;
        }
        .url {
          width: calc(100% - 184px);
          border-left: 0;
        }
      }
    }
    .footer {
      height: 44px;
      border-top: 1px solid #ddd;
      padding-right: 16px;
    }
    .dynamic-delete-button {
      cursor: pointer;
      position: relative;
      top: 4px;
      font-size: 24px;
      color: #999;
      transition: all 0.3s;
    }
    .dynamic-delete-button:hover {
      color: #777;
    }
    .dynamic-delete-button[disabled] {
      cursor: not-allowed;
      opacity: 0.5;
    }
  }
</style>
